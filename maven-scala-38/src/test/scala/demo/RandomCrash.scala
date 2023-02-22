package demo

import java.util.concurrent.ThreadLocalRandom

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

import com.typesafe.scalalogging.StrictLogging

class RandomCrash extends Simulation with StrictLogging {

  val httpProtocol = http
    .baseUrl("http://alpha.frontline:8080")

  val scn = scenario("scenario1")
    .during(1 minute) {
      exec(http("json").get("/json"))
        .exec { session =>
          if (ThreadLocalRandom.current.nextInt(7) == 0) {
            logger.error("random failure", new Exception("foo"))
            System.exit(1)
          }

          session
        }.pause(1)
    }

  setUp(scn.inject(atOnceUsers(2))).protocols(httpProtocol)
}
