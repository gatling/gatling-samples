package demo

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class FastWorkload extends Simulation {

  val httpProtocol = http
    .baseUrl("http://alpha.frontline:8080")
    .acceptEncodingHeader("gzip, deflate")
    .shareConnections
    .disableUrlEncoding
    .check(status.is(200))

  val scn = scenario("scenario1")
    .repeat(10) {
      exec(http("json").get("/json"))
        .exec(http("hello").get("/hello"))
        .exec(http("json1k").get("/json1k"))
        .exec(http("json10k").get("/json10k"))
        .pause(1)
    }

  setUp(
    scn.inject(atOnceUsers(5))
  )
    .protocols(httpProtocol)
    .assertions(global.failedRequests.count.is(1000))
}
