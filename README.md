# 1. Effect
## what is ZIO Effect?
> ZIO effects are precise plans that describe a computation or interaction. Ultimately, every effect must be executed by the ZIO runtime.

## why use ZIO Effect?
> if you pass the method some code to execute, that this code will be stored inside the ZIO effect so that it can be managed by ZIO, and benefit from features like retries, timeouts, and automatic error logging.

If you are using a custom environment for your application, you will have to supply your environment to the effect  before you return it from `run`
- `ZIO.provideEnvironment`
- `ZIO.provide` if you are using layers


# 2. For Comperhensions
> For comprehensions provide a procedural syntax for creating chains of effects, and are the fastest way for most programmers to get up to speed using ZIO.


# 3. [ZIO Quill](https://zio.dev/zio-quill/)
> The library's core is designed to support multiple target languages, currently featuring specializations for Structured Query Language (SQL)

# 4. [Doobie](https://tpolecat.github.io/doobie/)
> doobie is a pure functional JDBC layer for Scala and Cats

# 5. Cats