package demo

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class Reproducer extends Simulation {

  val httpProtocol = http
    .baseUrl("https://github.com")
    .acceptEncodingHeader("gzip, deflate")

  val scn1 = scenario("scenario1")
    .group("group1") {
      group("group1.1") {
        exec(http("request1").get("/"))
      }
    }

  val scn2 = scenario("scenario2")
    .group("group2") {
      uniformRandomSwitch(
        group("group2.1") {
          exec(http("request2").get("/"))
        },
        group("group2.2") {
          exec(http("request3").get("/"))
        }
      )
    }


  setUp(
    scn1.inject(constantUsersPerSec(0.6).during(25.minutes)),
    scn2.inject(constantUsersPerSec(0.4).during(25.minutes)),
  ).protocols(httpProtocol)
}
