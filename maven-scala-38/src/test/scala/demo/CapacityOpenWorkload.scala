package demo

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class CapacityOpenWorkload extends Simulation {

  val httpProtocol = http
    .acceptEncodingHeader("gzip, deflate")
    .enableHttp2
    .http2PriorKnowledge(Map(
      "http2.golang.org" -> true
    ))
    .disableWarmUp


  val scn = scenario("pidgin")
    .exec(http("frontline").get("https://http2.golang.org/"))
    .pause(1 second)

  setUp(scn.inject(
    incrementUsersPerSec(5)
      .times(5)
      .eachLevelLasting(10 seconds)
      .separatedByRampsLasting(10 seconds)
      .startingFrom(10))
  ).protocols(httpProtocol)

}