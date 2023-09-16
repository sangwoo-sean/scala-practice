# 1. ZIO Effect
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

# 6. SLF4J
https://zio.dev/zio-logging/slf4j2

# 7. http4s
[Introduction to http4s](https://www.baeldung.com/scala/http4s-intro)

# 8. circe

# 9. [sbt](https://www.scala-sbt.org/)
>Test is the configuration and means that ScalaCheck will only be on the test classpath and it isn’t needed by the main sources. This is generally good practice for libraries because your users don’t typically need your test dependencies to use your library.

의존성 뒤에 `Test` 라고 붙어있으면 해당 의존성이 test classpath 에 있을 것이며, main sources 를 필요로 하지 않는다는 뜻이다. 라이브러리를 연습할 때 이렇게 사용하는 것이 좋다.

# 10. Tapir

참고 Repo: https://github.com/softwaremill/realworld-tapir-zio
환율 가져오기
문서: https://www.koreaexim.go.kr/ir/HPHKIR020M01?apino=2&viewtype=C&searchselect=&searchword=
API Request URL: https://www.koreaexim.go.kr/site/program/financial/exchangeJSON

# 11. application.conf

test code 에서 config 를 사용하려면, 각 suite 에 provideLayer(ConfigProvider) 하여 사용한다.

테스트 코드 내에서 직접 값을 지정하여 사용할 수도 있고 직접 파일에 있는 값을 불러올 수도 있다.

## 1. 테스트 내에서 직접 지정한 값을 가져오기

```
ConfigProvider.fromMap(Map(key -> value))
```

key(변수명) 과 value(변수에 들어갈 값) 을 직접 Map 형태로 세팅할 수 있다.

## 2. .conf 파일에서 불러오기

```
TypesafeConfigProvider
    .fromHoconFilePath("conf/application.conf")
    .orElse(TypesafeConfigProvider.fromResourcePath())
```
위에서부터 순서대로 적용된다.

지정해놓은 경로인 "conf/application.conf" 파일에서 먼저 찾는다.

없으면 "test/resources/*.conf" 파일에서 먼저 찾는다. (테스트 코드이기 때문에)

거기에도 없으면 "main/resources/*.conf" 파일에서 찾는다.

거기에도 없으면 변수값이 없는 것으로 핸들링된다.


# 12. Error Handling

에러를 한곳에서 모아서 처리해주는 방법이 따로 있는지? ex) intercepter, filter, advisor, AOP 등

# 13. [ZIOAspect](https://zio.dev/api/zio/zioaspect$)

# 14. [TestAspect](https://zio.dev/api/zio/test/testaspect$)

`withLiveClock` 은 테스트시에도 실제 시간을 사용할 수 있게 해줌

# 15. [AKKA](https://akka.io/)
> Akka is a toolkit for building highly concurrent, distributed, and resilient message-driven applications for Java and Scala.
 

# 16. HOCON
> Human-Optimized Configuration Object Notation

# 17. [ZLayers](https://scalac.io/ebook/mastering-modularity-in-zio-with-zlayer/deep-look-into-modular-applications-with-zio/)

ZEnvironment 로만으로도 의존성 관리를 할 수 있겠지만, 규모가 커지고 복잡해지는 프로젝트에서 ZLayer 을 사용함으로써 의존성 관리를 보다 쉽고 안전하게 할 수 있도록 도와준다.

# 18. [ZIO JSON](https://zio.dev/zio-json/)
> ZIO Json is a fast and secure JSON library with tight ZIO integration.

scala 객체와 JSON string 간에 변환할 수 있도록 하는 라이브러리.
Codec, Encoder, Decoder 등을 설정할 수 있다.

`libraryDependencies += "dev.zio" %% "zio-json" % "0.6.1"`

# 19. [ZStream](https://zio.dev/reference/stream/zstream/)
> `ZStream` is as a `ZIO` program that could emit multiple values
