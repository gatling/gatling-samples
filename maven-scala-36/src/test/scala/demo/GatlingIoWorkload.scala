package demo

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class GatlingIoWorkload extends Simulation {

  val httpProtocol = http
    .baseUrl("https://gatling.io")
    .acceptEncodingHeader("gzip, deflate")

  val scn = scenario("scenario")
    .repeat(3) {
      exec(http("home").get("/"))
        .pause(1)
        .exec(http("aaa").get("/"))
        .pause(1)
        .exec(http("zzz").get("/"))
        .pause(1)
        .exec(http("bbb").get("/"))
        .pause(1)
    }

  setUp(scn.inject(
    //constantUsersPerSec(2).during(10)
    atOnceUsers(1)
  ))
    .protocols(httpProtocol)
}
