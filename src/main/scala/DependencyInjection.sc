import zio._

class Compiler() {}
object Compiler {
  val layer: ZLayer[Any, Nothing, Compiler] =
    ZLayer.succeed(new Compiler())
}

class Formatter() {}
object Formatter {
  val layer: ZLayer[Any, Nothing, Formatter] =
    ZLayer.succeed(new Formatter())
}
class Editor(formatter: Formatter, compiler: Compiler) {}
object Editor {
  val layer: ZLayer[Formatter with Compiler, Nothing, Editor] =
    ZLayer {
      for {
        formatter <- ZIO.service[Formatter]
        compiler  <- ZIO.service[Compiler]
      } yield new Editor(formatter, compiler)
    }
}

val formatter = new Formatter()
val compiler = new Compiler()
val editor = new Editor(formatter, compiler)

val editor: ZLayer[Formatter with Compiler, Nothing, Editor] =
  (Formatter.layer ++ Compiler.layer) >>> Editor.layer

val editor: ZLayer[Compiler, Nothing, Editor] =
  Formatter.layer >>> Editor.layer