import zio.Scope
import zio.test.{assertTrue, Spec, TestEnvironment, ZIOSpecDefault}

object VarianceSpec extends ZIOSpecDefault {

  abstract class Food
  case class Egg()       extends Food
  case class BoiledEgg() extends Egg()

  // Covariant class
  // 부모 <- 자식 업캐스트만 가능
  class Box[+T]

  // Contravariant class
  // 자식 <- 부모 다운캐스트만 가능
  class Eater[-T] {
    def consume(item: T): Unit = println(item)
  }

  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("variance")(
      test("covariance cast") {
        val upcast1: Box[Food] = new Box[Egg]
        val upcast2: Box[Egg]  = new Box[BoiledEgg]
        val upcast3: Box[Food] = new Box[BoiledEgg]
//        val downcast1: Box[Egg]       = new Box[Food]
//        val downcast2: Box[BoiledEgg] = new Box[Egg]
//        val downcast3: Box[BoiledEgg] = new Box[Food]

        // BoiledEggBox -> EggBox -> FoodBox (O)
        // FoodBox -> EggBox -> BoiledEggBox (X)
        // 삶은달걀은 음식상자에 넣을 수 있다.
        // 모든음식을 삶은달걀 상자에 넣을 수 없다.
        assertTrue(true)
      },
      test("contravariance cast") {
//        val upcast1: Eater[Food]        = new Eater[Egg]
//        val upcast2: Eater[Egg]         = new Eater[BoiledEgg]
//        val upcast3: Eater[Food]        = new Eater[BoiledEgg]
        val downcast1: Eater[Egg]       = new Eater[Food]
        val downcast2: Eater[BoiledEgg] = new Eater[Egg]
        val downcast3: Eater[BoiledEgg] = new Eater[Food]

        // FoodEater -> EggEater -> BoiledEggEater (O)
        // BoiledEggEater -> EggEater -> FoodEater (X)
        // 모든음식을 먹을 수 있는 사람은 삶은달걀을 먹을수 있다.
        // 삶은달걀만 먹을 수 있는 사람이 모든음식을 먹을 수 있는건 아니다.
        // 더 특정한 좁은 범위의 타입과 상호작용하고 싶을 때
        assertTrue(true)
      },
      test("contravariance example") {
        val foodEater: Eater[Food]           = new Eater[Food]
        val eggEater: Eater[Egg]             = new Eater[Egg]
        val boiledEggEater: Eater[BoiledEgg] = new Eater[BoiledEgg]

        foodEater.consume(Egg())
        foodEater.consume(BoiledEgg())
        eggEater.consume(Egg())
        eggEater.consume(BoiledEgg())
//        boiledEggEater.consume(Egg) //boiledEggEater can't consume raw Egg
        boiledEggEater.consume(BoiledEgg())

        assertTrue(true)
      }
    )

}
