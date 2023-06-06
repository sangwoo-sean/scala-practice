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
