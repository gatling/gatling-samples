package demo

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class GroupsSample extends Simulation {

  val httpProtocol = http
    .baseUrl("https://gatling.io")
    .acceptEncodingHeader("gzip, deflate")

  val scn = scenario("scenario")
    .exec(http("reqNoGroup").get("/#{foo}"))


  setUp(scn.inject(atOnceUsers(1)))
    .protocols(httpProtocol)
}
