# 1. sbt Shell 에서 실행시

`testOnly HelloWorldSpec`
: HelloWorldSpec 파일만 실행

`testOnly HelloWorldSpec -- -t "bar"`
: bar tag 가 있는 테스트들만 실행

만약 포함되는게 없다면 "No tests were executed" 라고 출력되고 Completed 된다.

# 2. Terminal 실행시

`sbt Test/run`
: 입력시 모든 테스트중에서 어떤 테스트를 run 할지 입력하는 prompt 가 출력된다.
