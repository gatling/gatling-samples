package demo

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class SimpleWorkloadWithAssertions extends Simulation {

  val httpProtocol = http
    .baseUrl("http://alpha.frontline:8080")
    .acceptEncodingHeader("gzip, deflate")
    .disableUrlEncoding
    .check(status.is(200))

  val scn1 = scenario("scenario1")
    .during(10) {
      exec(http("json").get("/json"))
    }

  val scn2 = scenario("scenario2")
    .during(10) {
      group("groupa") {
        exec(http("jsona").get("/json"))
          .exec(http("jsona1k").get("/json1k"))
      }.group("groupb") {
        exec(http("jsonb10k").get("/json10k"))
          .group("groupc") {
            exec(http("jsonbc1k").get("/json1k"))
          }
      }
    }

  setUp(
    scn1.inject(
      rampUsers(100).during(10)
    ),
    scn2.inject(
      rampUsers(100).during(10)
    )
  )
    .assertions(global.responseTime.max.lt(1000))
    .protocols(httpProtocol)
}
