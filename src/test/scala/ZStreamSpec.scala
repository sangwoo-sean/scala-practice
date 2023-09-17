import zio.Console.printLine
import zio.{durationInt, Chunk, Random, Schedule, Scope, ZIO}
import zio.stream.{ZSink, ZStream}
import zio.test.{assertTrue, Spec, TestAspect, TestEnvironment, ZIOSpecDefault}

object ZStreamSpec extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("Create ZStream")(
      test("apply") {
        for {
          res <- ZStream(1, 2, 3)
            .tap(printLine(_))
            .runCount
        } yield assertTrue(res == 3)
      },
      test("unit - contains a single Unit value") {
        for {
          res <- ZStream.unit.runCount
        } yield assertTrue(res == 1)
      },
      test("never - produces no value or fails with an error") {
        for {
          res <- ZStream.never // this stream never ends
            .timeoutTo(1.seconds)(ZStream.unit) // alternative stream
            .runCount
        } yield assertTrue(res == 1)
      },
      test("repeat - repeats using the specified schedule") {
        for {
          res <- ZStream(1)
            .repeat(Schedule.recurs(3)) // 1, (1, 1 ,1)
            .tap(printLine(_))
            .runCount
        } yield assertTrue(res == 4)
      },
      test("iterate") {
        for {
          res <- ZStream
            .iterate(1)(_ + 1) // 1, 2, 3, ...
            .tap(printLine(_)) // 1, 2, 3, 4
            .takeWhile(_ < 4)  // 1, 2, 3
            .map(x => x)
            .runCount
        } yield assertTrue(res == 3)
      },
      test("range") {
        for {
          res <- ZStream
            .range(1, 5) // 1, 2, 3, 4
            .runCount
        } yield assertTrue(res == 4)
      },
      test("success") {
        for {
          res <- ZStream.succeed(1).runCount
        } yield assertTrue(res == 1)
      },
      test("fail") {
        for {
          res <- ZStream
            .fail("Nope")
            .tapError { e =>
              println(e)
              ZIO.fail(e)
            }
            .orElse(ZStream(1, 2, 3))
            .runCount
        } yield assertTrue(res == 3)
      },
      test("from chunks") {
        for {
          res <- ZStream
            .fromChunks(Chunk(1, 2, 3), Chunk(4, 5, 6))
            .tap(printLine(_))
            .runCount
        } yield assertTrue(res == 6)
      },
      test("from ZIO") {
        for {
          res <- ZStream
            .fromZIO(Random.nextInt)
            .tap(printLine(_))
            .runCount
        } yield assertTrue(res == 1)
      }
    ) @@ TestAspect.withLiveClock +
      suite("ZStream Operations")(
        test("take") {
          for {
            _ <- ZIO.unit
            stream = ZStream.iterate(0)(_ + 1)

            s1 <- stream.take(3).runCollect
            s2 <- stream.takeWhile(_ < 3).runCollect
            s3 <- stream.takeUntil(_ == 3).runCollect
            s4 <- stream.takeUntil(_ == 3).takeRight(2).runCollect
          } yield assertTrue(
            s1 == Chunk(0, 1, 2) &&
              s2 == Chunk(0, 1, 2) &&
              s3 == Chunk(0, 1, 2, 3) &&
              s4 == Chunk(2, 3)
          )
        },
        test("map") {
          for {
            res <- ZStream
              .fromIterable(0 to 5)
              .map(_.toString)
              .runCollect
          } yield assertTrue(res == Chunk("0", "1", "2", "3", "4", "5"))
        },
        test("mapAccum") {
          for {
            res <- ZStream
              .fromIterable(0 to 5)
              .mapAccum(0)((acc, next) => (acc + next, acc + next))
              .runCollect
          } yield assertTrue(res == Chunk(0, 1, 3, 6, 10, 15))
        },
        test("as") {
          for {
            res <- ZStream(1, 2, 3, 4, 5)
              .as(0)
              .runCollect
          } yield assertTrue(res == Chunk(0, 0, 0, 0, 0))
        },
        test("scan") {
          for {
            res <- ZStream(1, 2, 3, 4, 5)
              .scan(0)(_ + _)
              .runCollect
          } yield assertTrue(res == Chunk(0, 1, 3, 6, 10, 15))
        },
        test("runFold") {
          for {
            res <- ZStream(1, 2, 3, 4, 5)
              .runFold(0)(_ + _)
          } yield assertTrue(res == 15)
        },
        suite("drain")(
          test("why you need it") {
            for {
              res <- (ZStream.fromZIO(printLine("Started")) ++
                ZStream(1, 2, 3, 4, 5) ++
                ZStream.fromZIO(printLine("Finished"))).runCollect
            } yield assertTrue(res == Chunk((), 1, 2, 3, 4, 5, ()))
          },
          test("when you use it") {
            for {
              res <- (ZStream.fromZIO(printLine("Started")).drain ++
                ZStream(1, 2, 3, 4, 5) ++
                ZStream.fromZIO(printLine("Finished")).drain).runCollect
            } yield assertTrue(res == Chunk(1, 2, 3, 4, 5))
          },
        ),
        test("changes") {
          for {
            res <- ZStream(1, 1, 1, 2, 2, 3, 4).changes.runCollect
          } yield assertTrue(res == Chunk(1, 2, 3, 4))
        },
        test("changesWith") {
          for {
            res <- ZStream((1, 1), (1, 2), (1, 3), (2, 4), (2, 5), (3, 6))
              .changesWith((e1, e2) => e1._1 == e2._1)
              .runCollect
          } yield assertTrue(res == Chunk((1, 1), (2, 4), (3, 6)))
        },
        suite("collecting - perform filter & map in a single step")(
          test("collect") {
            for {
              res <- ZStream(1, 2, 3, 4, 5).collect {
                case x if x % 2 == 0 => x * 2
              }.runCollect
            } yield assertTrue(res == Chunk(4, 8))
          },
          test("collectRight") {
            for {
              res <- ZStream(Left(1), Right(2), Right(3), Left(4), Right(5)).collectRight.runCollect
            } yield assertTrue(res == Chunk(2, 3, 5))
          },
          test("collectSome") {
            for {
              res <- ZStream(Left(1), Right(2), Right(3), Left(4), Right(5))
                .map(_.toOption)
                .collectSome
                .runCollect
            } yield assertTrue(res == Chunk(2, 3, 5))
          },
          suite("zipping")(
            test("zip") {
              for {
                res <- ZStream(1, 2, 3, 4, 5)
                  .zip(ZStream(6, 7, 8))
                  .runCollect
              } yield assertTrue(res == Chunk((1, 6), (2, 7), (3, 8)))
            },
            test("zipWith") {
              for {
                res <- ZStream(1, 2, 3, 4, 5)
                  .zipWith(ZStream(6, 7, 8))((a, b) => (a, b))
                  .runCollect
              } yield assertTrue(res == Chunk((1, 6), (2, 7), (3, 8)))
            },
            test("zipAll") {
              for {
                res <- ZStream(1, 2, 3, 4, 5)
                  .zipAll(ZStream(6, 7, 8))(0, 0)
                  .runCollect
              } yield assertTrue(res == Chunk((1, 6), (2, 7), (3, 8), (4, 0), (5, 0)))
            },
            test("zipAllWith") {
              for {
                res <- ZStream(1, 2, 3, 4, 5)
                  .zipAllWith(ZStream(6, 7, 8))((_, 0), (0, _))((a, b) => (a, b)) // left 만 있을 때, right 만 있을 때, 둘 다 있을 때
                  .runCollect
              } yield assertTrue(res == Chunk((1, 6), (2, 7), (3, 8), (4, 0), (5, 0)))
            },
            test("zipLatest") {
              for {
                _ <- ZIO.unit
                s1 = ZStream(1, 2, 3)
                  .schedule(Schedule.spaced(100.milliseconds))
                  .debug("s1")

                s2 = ZStream(4, 5, 6, 7)
                  .schedule(Schedule.spaced(50.milliseconds))
                  .debug("s2")

                res <- s1
                  .zipLatest(s2)
                  .debug("res")
                  .runCollect
              } yield assertTrue(res == Chunk((1, 4), (1, 5), (2, 5), (2, 6), (2, 7), (3, 7)))
              // s1: 1    s2: 4, 5    res: (1, 4), (1, 5)
              // s1: 2    s2: 5       res: (2, 5)
              // s1: 2    s2: 6, 7    res: (2, 6), (2, 7)
              // s1: 3    s2: 7       res: (3, 7)
            },
            test("zipWithIndex") {
              for {
                res <- ZStream("a", "b", "c").zipWithIndex.runCollect
              } yield assertTrue(res == Chunk(("a", 0L), ("b", 1L), ("c", 2L)))
            }
          ) @@ TestAspect.withLiveClock,
          suite("Cross Product")(
            test("cross") {
              for {
                _ <- ZIO.unit
                s1 = ZStream(1, 2, 3)
                s2 = ZStream("a", "b")

                res1 <- (s1 cross s2).runCollect
                res2 <- (s1 <*> s2).runCollect

                expected = Chunk((1, "a"), (1, "b"), (2, "a"), (2, "b"), (3, "a"), (3, "b"))
              } yield assertTrue(res1 == expected && res2 == expected)
            },
            test("crossLeft") {
              for {
                _ <- ZIO.unit
                s1 = ZStream(1, 2, 3)
                s2 = ZStream("a", "b")

                res1 <- (s1 crossLeft s2).runCollect
                res2 <- (s1 <* s2).runCollect

                expected = Chunk(1, 1, 2, 2, 3, 3)
              } yield assertTrue(res1 == expected && res2 == expected)
            },
            test("crossRight") {
              for {
                _ <- ZIO.unit
                s1 = ZStream(1, 2, 3)
                s2 = ZStream("a", "b")

                res1 <- (s1 crossRight s2).runCollect
                res2 <- (s1 *> s2).runCollect

                expected = Chunk("a", "b", "a", "b", "a", "b")
              } yield assertTrue(res1 == expected && res2 == expected)
            }
          ),
          test("partition") {
            for {
              res <- ZStream
                .fromIterable(0 to 100)
                .partition(_ % 2 == 0, buffer = 50)

              s1 <- res._1.runCount
              s2 <- res._2.runCount
            } yield assertTrue(s1 == 51 && s2 == 50)
          },
          test("partitionEither") {
            for {
              res <- ZStream
                .fromIterable(1 to 10)
                .partitionEither(x => ZIO.succeed(if (x < 5) Left(x) else Right(x)))

              s1 <- res._1.runCollect
              s2 <- res._2.runCollect
            } yield assertTrue(s1 == Chunk(1, 2, 3, 4) && s2 == Chunk(5, 6, 7, 8, 9, 10))
          },
          test("groupBy") {
            for {
              res <- ZStream("Mary", "James", "Robert", "Patricia", "John", "Jennifer", "Rebecca", "Peter")
                .groupBy(x => ZIO.succeed((x.head, x))) {
                  case (char, stream) =>
                    ZStream.fromZIO(stream.runCount.map(count => char -> count))
                }
                .runCollect

              // (P -> ZStream(Patricia, Peter)      )
              // (R -> ZStream(Robert, Rebecca)      )
              // (M -> ZStream(Mary)                 )
              // (J -> ZStream(James, John, Jennifer))
            } yield assertTrue(
              res.contains(('P', 2L)) &&
                res.contains(('R', 2L)) &&
                res.contains(('M', 1L)) &&
                res.contains(('J', 3L))
            )
          },
          test("grouped") {
            for {
              res <- ZStream
                .fromIterable(0 to 8)
                .grouped(3)
                .runCollect
            } yield assertTrue(res == Chunk(Chunk(0, 1, 2), Chunk(3, 4, 5), Chunk(6, 7, 8)))
          },
          suite("Concatenation")(
            test("concat") {
              for {
                _ <- ZIO.unit
                s1 = ZStream(1, 2, 3)
                s2 = ZStream(4, 5)

                res1 <- (s1 ++ s2).runCollect
                res2 <- (s1 concat s2).runCollect
              } yield assertTrue(res1 == Chunk(1, 2, 3, 4, 5) && res2 == Chunk(1, 2, 3, 4, 5))
            },
            test("concatAll") {
              for {
                _ <- ZIO.unit
                s1 = ZStream(1, 2, 3)
                s2 = ZStream(4, 5)

                res <- ZStream.concatAll(Chunk(s1, s2)).runCollect
              } yield assertTrue(res == Chunk(1, 2, 3, 4, 5))
            }
          ),
          test("merge - picks elements RANDOMLY from specified streams") {
            for {
              _ <- ZIO.unit
              s1 = ZStream(1, 2)
              s2 = ZStream(3)

              res <- (s1 merge s2).runCollect.debug("merge")
            } yield assertTrue(res.contains(1) && res.contains(2) && res.contains(3))
          },
          test("interleave - merge two streams DETERMINISTICALLY") {
            for {
              _ <- ZIO.unit
              s1 = ZStream(1, 2, 3)
              s2 = ZStream(4, 5)

              res <- (s1 interleave s2).runCollect
            } yield assertTrue(res == Chunk(1, 4, 2, 5, 3))
          },
          test("interleaveWith") {
            for {
              _ <- ZIO.unit
              s1 = ZStream(1, 3, 5, 7, 9)
              s2 = ZStream(2, 4, 6, 8, 10)

              res <- s1.interleaveWith(s2)(ZStream(true, false, false).forever).runCollect
              // true  -> left  == s1
              // false -> right == s2
              // s1, s2, s2, s1, s2, s2, ...
            } yield assertTrue(res == Chunk(1, 2, 4, 3, 6, 8, 5, 10, 7, 9))
          },
          test("intersperse") {
            for {
              res1 <- ZStream(1, 2, 3)
                .intersperse(0)
                .runCollect
              res2 <- ZStream(1, 2, 3)
                .intersperse(10, 0, 10)
                .runCollect
            } yield assertTrue(res1 == Chunk(1, 0, 2, 0, 3) && res2 == Chunk(10, 1, 0, 2, 0, 3, 10))
          },
          test("broadcast") {
            for {
              _ <- ZIO.scoped {
                ZStream
                  .fromIterable(1 to 10)
                  .mapZIO(_ => Random.nextInt)
                  .map(Math.abs)
                  .map(_ % 100)
                  .tap(e => printLine(s"Emit $e element before broadcasting"))
                  .broadcast(2, 3) // 2개의 스트림으로 3개씩 브로드캐스트 하겠다.
                  .flatMap { streams =>
                    for {
                      out1 <- streams(0).runFold(0)((acc, e) => Math.max(acc, e))
                        .flatMap(x => printLine(s"Maximum: $x"))
                        .fork
                      out2 <- streams(1).schedule(Schedule.spaced(1.second))
                        .foreach(x => printLine(s"Logging to the Console: $x"))
                        .fork
                      _ <- out1.join.zipPar(out2.join)
                    } yield ()
                  }
              }
            } yield assertTrue(true)
          } @@ TestAspect.withLiveClock,
          test("buffering") {
            for {
              res <- ZStream
                .fromIterable(1 to 10)
                .rechunk(1)
                .tap(x => zio.Console.printLine(s"before buffering: $x"))
                .buffer(4)
                .tap(x => zio.Console.printLine(s"after buffering: $x"))
                .schedule(Schedule.spaced(2.second))
                .runCollect
            } yield assertTrue(true)
          } @@ TestAspect.withLiveClock,
          test("debouncing") {
            for {
              res <- (ZStream(1, 2, 3) ++
                ZStream.fromZIO(ZIO.sleep(500.millis)) ++
                ZStream(4, 5) ++
                ZStream.fromZIO(ZIO.sleep(10.millis)) ++
                ZStream(6) ++
                ZStream.fromZIO(ZIO.sleep(10.millis)) ++
                ZStream(7, 8)
              ).debounce(100.millis) //emit only after a pause of at least 100 ms
                .debug("debounce")
                .runCollect
            } yield assertTrue(res == Chunk(3, 8))
          } @@ TestAspect.withLiveClock,
        )
      ) +
      suite("ZStream Error Handling")(
        test("???") {
          for {
            _ <- ZIO.unit
          } yield assertTrue(true)
        }
      )

}
