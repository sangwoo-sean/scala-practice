val fruitList = List("apples", "oranges", "pears")
val fruit = "apples" :: "oranges" :: "pears" :: Nil
fruit.head
fruit.tail
fruit.tails
fruit.tail.tail
fruit.tail.tail.tail
val empty = List()
val empty = Nil

val nums = Vector("louis", "frank", "hiromi")
nums(1)
nums.updated(2, "helena")

val fruitSet = Set("apple", "banana", "pear", "banana")
fruitSet.size

val r: Range = 1 until 5
var s: Range = 1 to 5
1 to 10 by 3
6 to 1 by -2

val s = (1 to 6).toSet
s.map(_ + 2)

fruitList.length
fruitList.last
fruitList.init
fruitList.take(2)
fruitList.drop(1)
fruitList.splitAt(1)
fruitList(0)
fruitList ++ fruit
fruitList.reverse
fruitList.updated(0, "grape")
fruitList.indexOf("oranges")
fruitList.contains("oranges")
fruitList.filter(v => v == "oranges")
fruitList.filterNot(v => v == "oranges")
fruitList.partition(v => v == "oranges")
fruitList.takeWhile(v => v.length > 5)
fruitList.dropWhile(v => v.length > 5)
fruitList.span(v => v.length > 5) // (list takeWhile p, list dropWhile p)

fruitList.reduceLeft((a, b) => a + b)
fruitList.reduceRight((a, b) => a + b)
fruitList.foldLeft("start ")((a, b) => a + b)
fruitList.foldRight(" end")((a, b) => a + b)
fruitList.forall(v => v.length > 5)
fruitList.zip(fruit)
fruitList.zip(fruit).unzip
fruitList.flatMap(v => v)
fruitList.flatten
List(1, 2, 3).sum
List(10, 2, 3, 4).product
List(10, 2, 3, 4).max
List(10, 2, 3, 4).min
List(10, 2, 3, 4).groupBy(x => x % 2)
List(3, 2, 3, 4).distinct

"new" +: fruitList
"new" :+ fruitList
fruitList :+ "new"
fruitList +: "new"

val myMap = Map("I" -> 1, "V" -> 5, "X" -> 10)
myMap("I")
//myMap("A") // java.util.NoSuchElementException: key not found: A
myMap.get("A")
myMap.get("I")
myMap.updated("V", 15)
