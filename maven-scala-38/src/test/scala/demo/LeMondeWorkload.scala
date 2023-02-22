package demo

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class LeMondeWorkload extends Simulation {

  val httpProtocol = http
    .acceptEncodingHeader("gzip, deflate")
    .disableCaching
    .check(status.is(200))
  //    .inferHtmlResources

  val scn = scenario("scenario1")
    .repeat(5) {
      group("gg") {
        exec(http("lemonde").get("https://www.lemonde.fr/"))
          .pause(1)
          .exec(http("blog").get("https://www.pythian.com/blog/investigating-io-performance-on-amazon-ec2/"))
          .pause(1)
          .exec(http("go").get("http://hearthstone.gamersorigin.com/"))
      }
    }

  setUp(
    scn.inject(constantUsersPerSec(5) during (2 minutes))
  )
    .protocols(httpProtocol)
}
