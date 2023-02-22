package demo

import java.util.concurrent.ThreadLocalRandom

import io.gatling.core.Predef._
import io.gatling.http.Predef._

object Search {

  val search =
    group("Search") {
      exec(http("Home")
        .get("/"))
        .pause(1)
        .feed(csv("search.csv").random)
        .exec(http("Search")
          .get("/computers?f=${searchCriterion}")
          .check(substring("Error Message").notExists)
          .check(css("a:contains('${searchComputerName}')", "href").saveAs("url")))
        .pause(1)
        .exec(http("Select")
          .get("${url}"))
        .pause(1)
    }
}

object Browse {
  val browse =
    group("Browse") {
      repeat(5, "i") {
        exec(http("Page ${i}")
          .get("/computers?p=${i}"))
          .pause(1)
      }
    }
}

object Edit {
  val edit =
    group("Edit") {
      tryMax(2) {
        exec(http("Form")
          .get("/computers/new"))
          .pause(1)
          .exec(http("Post")
            .post("/computers")
            .formParam("name", "MyComputer")
            .formParam("introduced", "2015-01-01")
            .formParam("discontinued", "")
            .formParam("company", "16")
            .check(status.is(_ => 200 + ThreadLocalRandom.current().nextInt(2))))
      }
    }
}

class ComputerDatabase extends Simulation {

  val httpProtocol = http
    .baseUrl("http://computer-database.gatling.io")
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("fr,fr-FR;q=0.8,en-US;q=0.5,en;q=0.3")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.11; rv:54.0) Gecko/20100101 Firefox/54.0")

  val admins = scenario("Admins")
    .exec(Search.search, Browse.browse, Edit.edit)

  val users = scenario("Users")
    .exec(Search.search, Browse.browse)

  setUp(
    users.inject(rampUsers(30) during (10)),
    admins.inject(rampUsers(10) during (10)))
    .protocols(httpProtocol)
    .assertions(global.successfulRequests.percent.is(100))
}
