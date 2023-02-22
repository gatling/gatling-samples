package demo

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.action.HttpActionBuilder

class MixedWorkload extends Simulation {

  private def httpActionsCount(chain: ChainBuilder): Int =
    chain.actionBuilders.filter(_.isInstanceOf[HttpActionBuilder]).size

  private val usersPerScenario = 100
  private val pauseTime = 100 milliseconds

  val httpProtocol = http
    .disableCaching
    .asyncNameResolution()
    .perUserNameResolution
    .check(status.is(200))

  val chain =
    during(2 minutes) {
      group("group1") {
        exec(http("local-netty-hello")
          .get("http://localhost:8000/hello")
          .check(bodyString.is("Hello, World!")))
          .pause(pauseTime)
      }.group("group2") {
        exec(http("local-netty-json1k")
          .get("http://localhost:8000/json1k")
          .check(jsonPath("$..flavors[*].id").is("1")))
          .pause(pauseTime)
          .exec(http("local-netty-json10k")
            .get("http://localhost:8000/json10k")
            .check(jsonPath("$..flavors[*].id").is("2")))
          .exec(http("local-netty-json500")
            .get("http://localhost:8000/json500")
            .check(jsonPath("$..flavors[*].id").is("1")))
          .pause(pauseTime)
      }.group("group3") {
        group("groupHttps") {
          exec(http("https-github")
            .get("https://github.com/gatling/gatling")
            .check(substring("gatling/gatling: Async Scala-Akka-Netty based Load Test Tool").count.is(1))).pause(pauseTime)
            .exec(http("http-netty")
              .get("https://netty.io/")
              .check(substring("asynchronous event-driven network application framework"))).pause(pauseTime)
        }.exec(http("http-gatling.io")
          .get("https://gatling.io")
          .check(css("title").is("Gatling Open-Source Load Testing – For DevOps and CI/CD"))).pause(pauseTime)
      }.repeat(5, "i") {
        exec(http("local-netty-hello").get("http://localhost:8000/hello")
          .check(bodyString.is("Hello, World!")))
      }
    }

  val plain = scenario("plain").exec(chain)
  val gzip = scenario("gzip").exec(chain)

  val additional = scenario("additional")
    .group("group3") {
      group("groupHttps") {
        exec(http("http-netty")
          .get("https://netty.io/"))
          .exec(http("http-netty2")
            .get("https://netty.io/"))
      }
    }
    .group("group4") {
      exec(http("http-gatling.io")
        .get("https://gatling.io"))
    }

  setUp(
    plain.inject(rampUsers(usersPerScenario) during (20)).protocols(httpProtocol),
    gzip.inject(rampUsers(usersPerScenario) during (20)).protocols(httpProtocol.acceptEncodingHeader("gzip, deflate")),
    additional.inject(rampUsers(usersPerScenario) during (20)).protocols(httpProtocol)
  ).assertions(
      global.allRequests.count.is(usersPerScenario * 3 * (httpActionsCount(chain) + 5)), // last is end, not request
      global.failedRequests.count.is(0)
    )
}
