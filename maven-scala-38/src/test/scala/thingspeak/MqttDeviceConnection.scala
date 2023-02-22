package thingspeak

import io.gatling.core.Predef._
import io.gatling.mqtt.Predef._
import scala.concurrent.duration._
import io.gatling.http.Predef._

class MqttDeviceConnection extends Simulation {

    java.security.Security.setProperty("networkaddress.cache.ttl", "0")

    
    val broker = "staging-mqtt3.thingspeak.com"
    val broker_port = 8883

    val mqttUsersPerSec = 1
    val mqttConnectionSimDuration = 10

    private val connectionScenario = scenario("MQTT Test")
            .exec(mqtt("Connecting").connect)
            .exec(mqtt("Publishing").publish("channels/1701339/publish/fields/field1").message(StringBody("10")))
            
    private val mqttProtocol = mqtt
    .broker(broker,broker_port)
    .clientId("KQ8VLToJCQMwNjsMEAAOED0")
    .credentials("KQ8VLToJCQMwNjsMEAAOED0", "UBh/K4A2INQGnTp/QKQvpXvI")
    .useTls(true)
    .mqttVersion_3_1_1
    .cleanSession(true)
    .reconnectDelay(0)
    .reconnectAttemptsMax(0)
    .reconnectBackoffMultiplier(0)
    .qosAtMostOnce

  setUp(
    connectionScenario.inject(
      atOnceUsers(1)
      //constantUsersPerSec(mqttUsersPerSec.toDouble) during(mqttConnectionSimDuration)
    ).protocols(mqttProtocol))
}
