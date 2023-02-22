package demo

import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._

class OpenWorkload extends Simulation {

  val httpProtocol = http
    .baseUrl("http://computer-database.gatling.io") // Here is the root for all relative URLs
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8") // Here are the common headers
    .doNotTrackHeader("1")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")

  val scn = scenario("Scenario Name") // A scenario is a chain of requests and pauses
    .exec(http("home").get("/"))
    .pause(2)
    .exec(http("Search").get("/computers?f=macbook"))
    .pause(2)
    .exec(http("Select").get("/computers/6"))

  setUp(scn.inject(
    constantUsersPerSec(2) during (10),
    constantUsersPerSec(4) during (10),
    constantUsersPerSec(6) during (10)
  )
    .protocols(httpProtocol))
}
