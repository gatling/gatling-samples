package demo

import java.{ lang => jl }

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class BenchmarkAppWorkload extends Simulation {

  private val host = sys.props.getOrElse("host", "localhost")
  private val https = jl.Boolean.getBoolean("https")
  private val gzip = jl.Boolean.getBoolean("gzip")
  private val users = jl.Integer.getInteger("users", 1000)
  private val loops = jl.Integer.getInteger("loops", 5000).toInt
  private val path = sys.props.getOrElse("path", "/json/1k.json")
  private val check = jl.Boolean.getBoolean("check")

  private val baseUrl = if (https) s"https://$host:8001" else s"http://$host:8000"

  private val httpProtocol = {
    val base =
      http
        .baseUrl(baseUrl)
        .check(status.is(200))

    if (gzip) base.acceptEncodingHeader("gzip, deflate") else base
  }

  val request = {
    val base = http(path).get(path)
    if (check) {
      path match {
        case "/txt/hello.txt" => base.check(substring("Hello").is(0))
        case "/json/100.json" => base.check(jsonPath("$.flavors[*]").count.is(1))
        case "/json/250.json" => base.check(jsonPath("$.flavors[*]").count.is(2))
        case "/json/500.json" => base.check(jsonPath("$.flavors[*]").count.is(3))
        case "/json/1k" => base.check(jsonPath("$.flavors[*]").count.is(5))
        case "/json/10k" => base.check(jsonPath("$.flavors[*]").count.is(19))
        case "/html/46k.html" => base.check(css("li.masterTooltip", "title").count.is(3))
        case "/html/232k.html" => base.check(css("a.media__link", "href").count.is(47))
        case _ => base
      }
    } else {
      base
    }
  }

  val scn = scenario(s"scenario ${sys.props("java.version")}")
    .repeat(loops) {
      exec(request)
    }

  setUp(scn.inject(atOnceUsers(users))).protocols(httpProtocol)
}
