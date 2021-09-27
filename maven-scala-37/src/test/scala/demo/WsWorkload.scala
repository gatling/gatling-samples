package demo

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._

class WsWorkload extends Simulation {

  val httpProtocol = http
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")
    .wsBaseUrl("wss://echo.websocket.org")
    .wsReconnect
    .wsMaxReconnects(3)

  val scn = scenario("WebSocket")
    .exec(ws("Connect WS").connect("/?encoding=text")
      .onConnected(
        exec(ws("Perform auth")
          .sendText("Test message 1")
          .await(30 seconds)(
            ws.checkTextMessage("Auth check").check(regex(".*").is("Test message 1"))
          ))
      ))
    .pause(1)
    .exec(ws("Message1")
      .sendText("Hello, I'm test and this is test message!")
      .await(30 seconds)(
        ws.checkTextMessage("checkName").check(regex(".*").is("Hello, I'm test and this is test message!"))
      ))
    .exec(ws("Close").close)

  setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}
