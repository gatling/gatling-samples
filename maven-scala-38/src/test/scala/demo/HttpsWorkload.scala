package demo

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class HttpsWorkload extends Simulation {

  val httpProtocol = http
    .baseUrl("https://alpha.frontline:8081")
    .acceptEncodingHeader("gzip, deflate")
    .check(status.is(200))

  val scn = scenario("scenario1")
    .during(60 seconds) {
      exec(http("Home").get("/json10k"))
        .pause(10 milliseconds)
    }

  setUp(
    scn.inject(rampUsers(1000) during 10)
  )
    .protocols(httpProtocol)
}
