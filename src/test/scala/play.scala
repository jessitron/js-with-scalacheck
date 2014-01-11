package jessitron.canhazjs

import org.scalatest._
import org.scalatest.prop.PropertyChecks
import org.scalatest.selenium._
import org.openqa.selenium._

class PlaySpec extends FunSpec with ShouldMatchers with WebBrowser with HtmlUnit with PropertyChecks {

  it("Can run javascript") {
    go to "file://garbage.html"

    forAll{ s:String =>
      whenever (s.length > 0 && !s.contains("'") && !s.endsWith("\\")) {

        val result = executeScript(s"return '$s';")

        result should be (s)
      }
    }
  }
}
