package demo

import java.util.concurrent.ConcurrentHashMap

import scala.jdk.CollectionConverters._

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class ShardBatchedFeederWorkload extends Simulation {

  private val ids = new ConcurrentHashMap[String, String]()

  def idRange = {
    val allIds = ids.elements().asScala.map(_.toInt).toArray
    (allIds.min, allIds.max)
  }

  val httpProtocol = http
    .baseUrl("http://alpha.frontline:8080")
    .acceptEncodingHeader("gzip, deflate")

  val scn = scenario("scenario")
    .repeat(200) {
      feed(csv("data/ids.csv").batch(100).shard.random)
        .exec(http("json").get("/json1k"))
        .exec { session =>
          val id = session("id").as[String]
          ids.put(id, id)
          session
        }
    }.exec(http(_ => "idRange=" + idRange).get("/json1k"))

  setUp(scn.inject(atOnceUsers(10))).protocols(httpProtocol)
}
