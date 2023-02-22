package demo

import java.util.concurrent.ThreadLocalRandom

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class DelayWorkload extends Simulation {

  val httpProtocol = http
    .baseUrl("http://alpha.frontline:8000")
    .acceptEncodingHeader("gzip, deflate")
    .shareConnections
    .disableUrlEncoding
    .check(status.is(200))

  val scn = scenario("scenario1")
    .repeat(10) {
        exec(http("hello").get("/hello")
          .header("X-Delay", _ => ThreadLocalRandom.current.nextInt(500, 5000).toString))
        .pause(1)
    }

  setUp(scn.inject(atOnceUsers(5))).protocols(httpProtocol)
}
