import zio.{Chunk, Scope}
import zio.test.{Spec, TestEnvironment, ZIOSpecDefault, assertTrue}
import zio.prelude._
import zio.prelude.newtypes._

import scala.util.Try

case class Topic(value: String)
case class Votes(value: Int)

object Votes {

  implicit val VotesAssociative: Associative[Votes] = new Associative[Votes] {
    override def combine(l: => Votes, r: => Votes): Votes = Votes(l.value + r.value)
  }

  implicit val VotesCommutative: Commutative[Votes] = new Commutative[Votes] {
    override def combine(l: => Votes, r: => Votes): Votes = Votes(l.value + r.value)
  }

}

case class VoteMap(map: Map[Topic, Votes])

object VoteMap {
  def combine(l: VoteMap, r: VoteMap): VoteMap = VoteMap(l.map <> r.map)
}

/** A new type in ZIO Prelude is "a type that has the same underlying representation as another type at RUNTIME" but is
  * "a separate type at COMPILE TIME"
  */

object ZIOPreludeSpec extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("prelude")(
      suite("Associative")(
        test("string") {
          val result = "Hello, " <> "World"
          assertTrue(result == "Hello, World")
        },
        test("chunk") {
          val result = Chunk(1, 2) <> Chunk(3, 4)
          assertTrue(result == Chunk(1, 2, 3, 4))
        },
        test("map") {
          val result = Map(1 -> "a", 2 -> "b") <> Map(2 -> "c")
          assertTrue(result == Map(1 -> "a", 2 -> "bc"))
        },
        test("custom") {
          val result = Votes(3) <> Votes(2)
          assertTrue(result == Votes(5))
        },
        suite("newtype")(
          test("sum") {
            val result = Sum(1) <> Sum(2)
            assertTrue(result == Sum(3))
          },
          test("prod") {
            val result = Prod(2) <> Prod(3)
            assertTrue(result == Prod(6))
          },
          test("and") {
            val result1 = And(true) <> And(false)
            val result2 = And(true) <> And(true)
            assertTrue(result1 == And(false) && result2 == And(true))
          },
          test("or") {
            val result1 = Or(true) <> Or(false)
            val result2 = Or(false) <> Or(false)
            assertTrue(result1 == Or(true) && result2 == Or(false))
          },
          test("min") {
            val result = Min(1) <> Min(2)
            assertTrue(result == Min(1))
          },
          test("max") {
            val result = Max(1) <> Max(2)
            assertTrue(result == Max(2))
          },
        )
      ) +
        suite("New Types")(
          test("normal types can be interchangeable") {
            type ANumber = Int
            type SNumber = Int
            def takeAnum(num: ANumber) = num

            val anum: ANumber = 1
            val snum: SNumber = 1

            assertTrue(anum == snum && takeAnum(anum) == anum && takeAnum(snum) == snum)
          },
          test("NewType makes same type different") {
            object AccountNumber extends Newtype[Int]
            type AccountNumber = AccountNumber.Type
            object SequenceNumber extends Newtype[Int]
            type SequenceNumber = SequenceNumber.Type
            def takeAnumNewType(num: AccountNumber) = num

            val anumNewType = AccountNumber(1)
            val snumNewType = SequenceNumber(1)

            val isSame = anumNewType == snumNewType // this will be evaluated in runtime. so it works.
            //takeAnumNewType(snumNewType) //type mismatch
//            assertTrue(anumNewType == snumNewType)// this will be evaluated in compile time. so type mismatch
            assertTrue(takeAnumNewType(anumNewType) == anumNewType && isSame)
          }
        )
    )

}
