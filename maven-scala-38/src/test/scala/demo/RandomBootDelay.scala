package demo

import java.util.concurrent.ThreadLocalRandom

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

import com.typesafe.scalalogging.StrictLogging

class RandomBootDelay extends Simulation with StrictLogging {

  Thread.sleep(ThreadLocalRandom.current.nextInt(5) * 1000)

  val httpProtocol = http
    .baseUrl("http://alpha.frontline:8080")

  val scn = scenario("scenario1")
    .during(1 minute) {
      exec(http("json").get("/json"))
        .pause(1)
    }

  setUp(scn.inject(atOnceUsers(2))).protocols(httpProtocol)
}
