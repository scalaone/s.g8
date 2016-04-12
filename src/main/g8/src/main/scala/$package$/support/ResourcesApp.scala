package $package$.support

import org.scalatra.ScalatraServlet
import org.scalatra.swagger.{ApiInfo, JacksonSwaggerBase, Swagger}

class ResourcesApp(implicit val swagger: Swagger) extends ScalatraServlet with JacksonSwaggerBase

object ResourceApiInfo extends ApiInfo(
  "The Resource API",
  "Docs for the Resource API",
  "http://scalatra.org",
  "apiteam@scalatra.org",
  "MIT",
  "http://opensource.org/licenses/MIT")

object ResourceSwagger extends Swagger(Swagger.SpecVersion, "1.0.0", ResourceApiInfo)
