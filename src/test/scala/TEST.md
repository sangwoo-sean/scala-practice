# 1. sbt Shell 에서 실행시

`testOnly HelloWorldSpec`
: HelloWorldSpec 파일만 실행

`testOnly HelloWorldSpec -- -t "bar"`
: bar tag 가 있는 테스트들만 실행

만약 포함되는게 없다면 "No tests were executed" 라고 출력되고 Completed 된다.

# 2. Terminal 실행시

`sbt Test/run`
: 입력시 모든 테스트중에서 어떤 테스트를 run 할지 입력하는 prompt 가 출력된다.


---

## TimeClockSpec
> Instead of moving by itself, the clock time only changes when adjusted or set by the user, using the adjust and setTime methods. The clock time never changes by itself.

시간의 흐름을 조정해가며 테스트해볼 수 있다.
그러나 시간이 스스로 가지 않기 때문에, 원하는 시간대로 직접 조정해주어가며 사용해야 한다.
- 실제로 딜레이보다 적은 시간을 adjust 했더니 시간이 가지 않아 프로그램을 강제로 멈추어야 하는 경우가 생겼다.

# 3. IntelliJ 에서 실행시
코드라인 부근의 재생버튼을 눌러서 실행하게 되면 `Spec::label` 과 같은 형식으로 `RunConfiguration` 의 이름이 결정된다.

이 때 `::` 뒤에 오는 label 이 포함된 모든 테스트가 실행되게 된다. (`LabelSpec` 참고)

이는 suite 와 test에 모두 적용된다.

`1. sbt Shell 에서 실행시` 에서 언급된 `testOnly HelloWorldSpec -- -t "bar"` 와 동일한 기능이라고 볼 수 있다.