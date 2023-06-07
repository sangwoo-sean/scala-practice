import scala.math.abs
//A for-comprehension is syntactic sugar for map, flatMap and filter operations on collections.
//The general form is for (s) yield e

for {
  x <- 1 to 2
  y <- 3 to 4
  if (x + y == 5)
} yield (x, y)
//is equivalent to
(1 to 2)
  .flatMap(x => (3 to 4)
  .filter(y => x + y == 5)
  .map(y => (x, y)))


def isGoodEnough(guess: Double, x: Double) =
  abs(guess * guess - x) < 0.001

isGoodEnough(0.001, 0.001)
isGoodEnough(0.1e-20, 0.1e-20)
isGoodEnough(1.0e20, 1.0e20)
isGoodEnough(1.0e50, 1.0e50)


def isGoodEnough2(guess: => Double, x: => Double) =
  abs(guess * guess - x) < 0.001

isGoodEnough2(0.001, 0.001)
isGoodEnough2(0.1e-20, 0.1e-20)
isGoodEnough2(1.0e20, 1.0e20)
isGoodEnough2(1.0e50, 1.0e50)