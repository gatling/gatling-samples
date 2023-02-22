package demo

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class TonsOfRequestsWorkload extends Simulation {

  val httpProtocol = http
    .baseUrl("https://alpha.frontline:8081")
    .acceptEncodingHeader("gzip, deflate")
    .shareConnections
    .disableUrlEncoding
    .check(status.is(200))

  val scn = scenario("scenario")
    .repeat(9999, "i") {
      exec(http("request_${i}").get("/json1k"))
    }

  setUp(scn.inject(atOnceUsers(1)))
    .protocols(httpProtocol)
}
