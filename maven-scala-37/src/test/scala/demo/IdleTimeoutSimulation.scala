package demo

import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class IdleTimeoutSimulation extends Simulation {

  val httpProtocol = http
    .baseUrl("http://localhost:8000")
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("fr,fr-FR;q=0.8,en-US;q=0.5,en;q=0.3")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.11; rv:54.0) Gecko/20100101 Firefox/54.0")

  val users = scenario("Users")
    .repeat(10) {
      exec(http("get").get("/json1k"))
        .pause(3)
    }

  setUp(
    users.inject(rampUsers(2000) during (10))).protocols(httpProtocol)
}
