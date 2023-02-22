package demo

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class SoapWorkload extends Simulation {

  val totalDuration = 600 seconds

  val httpProtocol = http
    //.shareConnections
    .baseUrl("http://localhost:8000")

  val scn = scenario("WS SOAP Invoke")
    .repeat(200000) {
      exec(http("soap_request")
        .post("/echo")
        .body(RawFileBody("bodies/soap.xml"))
        .asXml)
    }

  setUp(scn.inject(atOnceUsers(200)).protocols(httpProtocol))
    .maxDuration(totalDuration)
}
