package demo

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class Http2Workload extends Simulation {

  val httpProtocol = http
    .baseUrl("https://www.bbc.com")
    .acceptEncodingHeader("gzip, deflate")
    .disableUrlEncoding
    .enableHttp2

  val scn = scenario("pidgin")
    .exec(http("pidgin").get("/pidgin"))

  setUp(scn.inject(constantUsersPerSec(1) during (15 seconds)))
    .protocols(httpProtocol)
}
