package demo

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class AlphaFastWorkload extends Simulation {

  val httpProtocol = http
    .baseUrl("https://10.220.246.157:8000")

  val scn = scenario("scenario1_availableProcessors=" + Runtime.getRuntime.availableProcessors)
    .during(1 minute) {
      exec(
        http("json1k")
          .get("/json1k")
      )
        .exec(
          http("json1k.2")
            .get("/json1k")
        )
    }

  val scnPost =
    scenario("scenarioPost_availableProcessors=" + Runtime.getRuntime.availableProcessors)
      .during(1 minute) {
        exec(
          http("json1k")
            .post("/post")
            .header("Content-Type", "application/json")
            .body(RawFileBody("bodies/json4k.json"))
        )
      }

  setUp(
    scnPost.inject(atOnceUsers(1000))
  ).protocols(httpProtocol)
}
