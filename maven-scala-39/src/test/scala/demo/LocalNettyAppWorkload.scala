package demo

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class LocalNettyAppWorkload extends Simulation {

  val host = sys.props.get("target").getOrElse("localhost")

  val httpProtocol = http
    .baseUrl(s"http://$host:8000")
    .acceptEncodingHeader("gzip, deflate")
    .disableUrlEncoding
    .disableCaching
    .check(status.is(200))

  val scn = scenario("scenario1")
    .during(1 minute) {
      exec(http("json1k").get("/json1k"))
        .pause(1)
    }

  setUp(
    scn.inject(atOnceUsers(10))
      .protocols(httpProtocol)
  )
}
