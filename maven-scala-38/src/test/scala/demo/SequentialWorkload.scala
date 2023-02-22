package demo

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class SequentialWorkload extends Simulation {

  val httpProtocol = http
    .baseUrl("https://github.com")
    .disableWarmUp

  private val level1 = scenario("Level1").exec(http(s"Level1 Request").get("/gatling/gatling"))
  private val level2 = scenario("Level2").exec(http(s"Level2 Request").get("/gatling/gatling"))
  private val level3 = scenario("Level3").exec(http(s"Level3 Request").get("/gatling/gatling"))

  setUp(
    level1.inject(atOnceUsers(1))
      .andThen(
        level2.inject(atOnceUsers(1))
          .andThen(level3.inject(constantUsersPerSec(2).during(20)))
      )
  ).protocols(httpProtocol)
}
