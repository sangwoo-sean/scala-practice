import org.scalatest.funsuite.AnyFunSuite

class ScopeTest {
  val value: String = "value"
  private val privateVal: String = "private"
  private[this] val scopedVal: String = "scoped" // the most strict modifier
  private[ScopeTest] val scopedVal2: String = "scoped" // restrict to ScopeTest

  def getValue: String = value
  def getPrivateVal: String = privateVal
  def getScopedVal: String = scopedVal
}
object ScopeTest {
  final val tester = new ScopeTest

  def getValue: String = tester.value // can access (of course)
  def getPrivateVal: String = tester.privateVal // can access
//  def getScopedVal: String = tester.scopedVal // cannot access. strict!
  def getScopedVal: String = tester.getScopedVal //it has to be this way
  def getScopedVal2: String = tester.scopedVal2 //can access
}

class ScalaSyntaxTest extends AnyFunSuite{

  test("access with values") {
    val scopeTest = new ScopeTest
    assert(scopeTest.value == "value")
//    scopeTest.privateVal //inaccessible
//    scopeTest.scopedVal ///inaccessible
  }

  test("access with getters") {
    val scopeTest = new ScopeTest
    assert(scopeTest.getValue == "value")
    assert(scopeTest.getPrivateVal == "private")
    assert(scopeTest.getScopedVal == "scoped")
  }

}
