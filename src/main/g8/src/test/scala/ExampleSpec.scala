package $package_name$

import org.scalatest.flatspec.{AnyFlatSpec => FlatSpec}
import org.scalatest.matchers.should.Matchers

class ExampleSpec extends FlatSpec with Matchers {
  it should "create db statements" in {
    Example.createHsqldbObjectsStatements.nonEmpty shouldBe true
  }
}
