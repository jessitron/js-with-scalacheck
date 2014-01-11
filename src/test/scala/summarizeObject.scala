package jessitron.canhazjs

import org.scalatest._
import org.scalatest.prop.PropertyChecks
import org.scalatest.selenium._
import org.openqa.selenium._
import org.scalacheck._
import spray.json._
import DefaultJsonProtocol._

class SummarizeObjectsSpec extends FunSpec with ShouldMatchers with WebBrowser with Chrome with PropertyChecks {
  /*
   * Say I have an array of objects like
   * { "source": "pid 1234",
   *   "message": "Go",
   *   "timestamp": 1235123514
   * }
   * and I want the count of "Go" messages, and min and max timestamps.
   */

  // generate input data
  val sources: Gen[String] = Gen.choose(1, 9999) map { "pid " + _ }
  val messages: Gen[String] = Gen.oneOf("Go", "Stop", "Run in circles")
  val timestamps: Gen[Long] = Gen.choose(1, 99999) map { _ + new java.util.Date().getTime}

  case class Detail(source: String, message: String, timestamp: Long)
  object Detail {
    import DefaultJsonProtocol._
    implicit val format: JsonFormat[Detail] = jsonFormat3(apply)
  }

  val details: Gen[Detail] = for {
    s <- sources
    m <- messages
    t <- timestamps
  } yield Detail(s,m,t)

  val detailSeqs:Gen[Seq[Detail]] = for {
    n <- Gen.choose(0, 20)
    l <- Gen.listOfN(n, details)
  } yield l

  // Now use those in a test
  it("Can use the JS to summarize the details") {

    go to "file://nowhere.html"

    forAll(detailSeqs) { input: Seq[Detail] =>

      val inputJson = input.toJson.prettyPrint

      val fullScript = jsonUnderTest + s"\n return summarize($inputJson);"
      info(fullScript)

      val result: Summary  = executeScript(fullScript) match {
        case s: String => s.asJson.convertTo[Summary]
        case x => fail("What is this? " + x)
      }

      val goCount = input.map(_.message).filter(_ == "Go").size

      result.goCount should be (goCount)
    }
  }

  case class Summary(goCount: Int)
  object Summary {
    import DefaultJsonProtocol._
    implicit val format: JsonFormat[Summary] = jsonFormat1(apply)
  }

  // todo: load from classpath?
  val jsonUnderTest = """
     function summarize(arr) {
       var messages = arr.map(function(x) { return x.message; });

       var goCount = arr.filter(function(x) { return x === 'Go'}).length;

       return {
         'goCount': goCount
       };
     }
  """


}
