/*
 * Copyright 2011-2021 GatlingCorp (https://gatling.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package computerdatabase.advanced;

import static io.gatling.core.javaapi.Predef.*;
import static io.gatling.http.javaapi.Predef.*;

import io.gatling.core.javaapi.*;
import io.gatling.http.javaapi.*;
import java.util.concurrent.ThreadLocalRandom;
import java.time.Duration;

public class AdvancedSimulationStep05 extends Simulation {

  private static class Search {

    static FeederBuilder feeder = csv("search.csv").random();

    static ChainBuilder search = group("Search").grouping(
        exec(http("Root").get("/"))
            .pause(1)
            .feed(feeder)
            .exec(
                http("Search")
                    .get("/computers?f=${searchCriterion}")
                    .check(
                        css("a:contains('${searchComputerName}')", "href").saveAs("computerURL")))
            .pause(1)
            .exec(http("Select").get("${computerURL}").check(status().is(200)))
            .pause(1)
        );
  }

  private static class Browse {

    // repeat is a loop resolved at RUNTIME
    static ChainBuilder browse = group("Browse").grouping(
        repeat(4, "i")
            .loop( // Note how we force the counter name so we can reuse it
                exec(http("Page ${i}").get("/computers?p=${i}")).pause(1))
        );
  }

  private static class Edit {

    // Note we should be using a feeder here
    // let's demonstrate how we can retry: let's make the request fail randomly and retry a given
    // number of times

    static ChainBuilder edit = group("Edit").grouping(
        tryMax(2)
            .trying( // let's try at max 2 times
                exec(http("Form").get("/computers/new"))
                    .pause(1)
                    .exec(
                        http("Post")
                            .post("/computers")
                            .formParam("name", "Beautiful Computer")
                            .formParam("introduced", "2012-05-30")
                            .formParam("discontinued", "")
                            .formParam("company", "37")
                            .check(
                                status()
                                    .is(
                                        session ->
                                            200
                                                + ThreadLocalRandom.current()
                                                    .nextInt(
                                                        2)))) // we do a check on a condition that's
                // been customized with a lambda. It
                // will be evaluated every time a user
                // executes the request
                )
            // if the chain didn't finally succeed, have the user exit the whole scenario
            .exitHereIfFailed()
        ); 
  }

  HttpProtocolBuilder httpProtocol =
      http()
          .baseUrl("http://computer-database.gatling.io")
          .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
          .doNotTrackHeader("1")
          .acceptLanguageHeader("en-US,en;q=0.5")
          .acceptEncodingHeader("gzip, deflate")
          .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")
          .redirectNamingStrategy(
              (uri, originalRequestName, redirectCount) ->
                    uri.getPath().equals("/computers") ? "Home" : (originalRequestName + " Redirect " + redirectCount)
            );

  ScenarioBuilder users = scenario("Users").exec(Search.search, Browse.browse);
  ScenarioBuilder admins = scenario("Admins").exec(Search.search, Browse.browse, Edit.edit);

  public AdvancedSimulationStep05() {
    setUp(
        users.injectOpen(constantUsersPerSec(1).during(Duration.ofMinutes(5))),
        admins.injectOpen(constantUsersPerSec(0.1).during(Duration.ofMinutes(5)))
    ).protocols(httpProtocol);
  }
}
