package demo

import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._

class BbcWorkload extends Simulation {

  val httpProtocol = http
    //    .baseURL("https://frontline-load-balancer-264866616.eu-central-1.elb.amazonaws.com")
    .baseUrls("https://35.157.149.255:8000", "https://35.157.130.28:8000")
    //    .baseURL("http://frontline-load-balancer-264866616.eu-central-1.elb.amazonaws.com")
    .acceptEncodingHeader("gzip, deflate")
    .disableUrlEncoding
    .check(status.is(200))

  val scn = scenario("BBC News")
    .exec(http("news").get("/news"))

  setUp(
    scn.inject(rampUsersPerSec(100) to (20000) during (2 minutes))
  )
    .protocols(httpProtocol)
}
