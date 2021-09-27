package demo

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class SseWorkload extends Simulation {

  val httpProtocol = http
    .baseUrl("https://www.w3schools.com/html")
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")
    .disableWarmUp

  val scn = scenario("sse_test")
    .exec(http("Get page").get("/tryit.asp?filename=tryhtml5_sse"))
    .exec(sse("Open SSE").connect("/demo_sse.php")
      .await(5)(
        sse.checkMessage("checkName1").check(substring("The server time is:")),
        sse.checkMessage("checkName2").check(substring("The server time is:")),
        sse.checkMessage("checkName3").check(substring("The server time is:"))
      )
    )
    .pause(3)
    .exec(sse("Close sse").close)

  setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}
