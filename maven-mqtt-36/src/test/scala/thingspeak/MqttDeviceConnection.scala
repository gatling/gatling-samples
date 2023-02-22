package thingspeak

import io.gatling.core.Predef._
import io.gatling.mqtt.Predef._
import scala.concurrent.duration._

class MqttDeviceConnection extends Simulation {

  java.security.Security.setProperty("networkaddress.cache.ttl", "0")

  val broker = "staging-mqtt3.thingspeak.com"
  val broker_port = 8883

  private val mqttProtocol = mqtt
    .broker(broker,broker_port)
    .correlateBy(jsonPath("$.correlationId"))
    .clientId("${clientId}")
    .credentials("${username}", "${password}")
    .useTls(true)
    .mqttVersion_3_1_1
    .cleanSession(true)

  private val connectionScenario = scenario("MQTT Test")
    .feed(csv("credentials.csv"))
    .exec(mqtt("Connecting").connect)

  setUp(
    connectionScenario
      .inject(atOnceUsers(1))
      .protocols(mqttProtocol)
  )
}
