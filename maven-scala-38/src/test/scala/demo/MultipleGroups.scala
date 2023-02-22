package demo

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class MultipleGroups extends Simulation {

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
    .group("other group") {
      exec(http("jsona").get("/json"))
        .exec(http("jsona1k").get("/json1k"))
    }.group("group1") {
    exec(http("jsonb10k").get("/json10k"))
      .group("group11") {
        exec(http("jsonbc1k").get("/json1k"))
          .group("group111") {
            exec(http("jsonbc1k").get("/json1k"))
          }
          .group("group112") {
            exec(http("jsonbc1k").get("/json1k"))
          }
      }.group("group12") {
      exec(http("jsonbc1k").get("/json1k"))
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
