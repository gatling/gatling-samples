package demo

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class GroupsSample extends Simulation {

  val httpProtocol = http
    .baseUrl("https://gatling.io")
    .acceptEncodingHeader("gzip, deflate")

  val scn = scenario("scenario")
    .exec(http("reqNoGroup").get("/"))
    .group("Group1") {
       exec(http("reqGroup1").get("/"))
       .group("Group2") {
          exec(http("reqGroup2").get("/"))
          .group("Group3") {
            exec(http("reqGroup3").get("/"))
            .group("Group4") {
               exec(http("reqGroup4").get("/"))
             }
          }
       }
    }


  setUp(scn.inject(atOnceUsers(1)))
    .protocols(httpProtocol)
}
