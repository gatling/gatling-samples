package demo

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class SequentialScenariosWorkload extends Simulation {

  val httpProtocol = http
    .baseUrl("https://google.com")
    .acceptEncodingHeader("gzip, deflate")

  val scn1 = scenario("scenario1")
      .exec(http("home").get("/"))
        .pause(500.millis)

  val scn2 = scenario("scenario2")
      .exec(http("home").get("/"))
        .pause(500.millis)


  setUp(
    // test1
    //scn1.inject(constantConcurrentUsers(2).during(30))
    //  .andThen(scn2.inject(constantConcurrentUsers(2).during(30)))
    // test2
    scn1.inject(constantUsersPerSec(2).during(30))
      .andThen(scn2.inject(constantUsersPerSec(2).during(30)))
  ).protocols(httpProtocol)
}
