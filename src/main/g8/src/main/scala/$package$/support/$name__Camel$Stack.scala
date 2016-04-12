package $package$.support

import grizzled.slf4j.Logging
import org.joda.time.DateTime
import org.json4s.{DefaultFormats, Formats}
import org.scalatra._
import org.scalatra.json.JacksonJsonSupport
import org.scalatra.swagger._
import org.scalatra.swagger.reflect.Reflector
import org.scalatra.swagger.runtime.annotations.ApiModel
import slick.jdbc.{PositionedParameters, SetParameter}

case class Message(message: String, success: Boolean)

trait $name;format="Camel"$Stack extends ScalatraServlet with JacksonJsonSupport with SwaggerSupport with DatabaseSupport with Logging {
  protected def applicationDescription = "$name;format="Camel"$"

  implicit val swagger = ResourceSwagger

  implicit protected def jsonFormats: Formats = DefaultFormats

  implicit object SetDateTime extends SetParameter[DateTime] {
    def apply(v: DateTime, pp: PositionedParameters) {
      pp.setDate(new java.sql.Date(v.getMillis))
    }
  }

  before() {
    contentType = formats("json")
  }

  def api[R: Manifest, P](name: String)(implicit p: Manifest[P]) = {
    val apiOper = apiOperation[R](name).summary(name)
    if (p != manifest[Unit])
      operation(apiOper.parameter(bodyParam[P]))
    else
      operation(apiOper)
  }

  def success(msg: String) = ActionResult(ResponseStatus(200), Message(msg, true), Map[String, String]())

  def failure(msg: String) = ActionResult(ResponseStatus(500), Message(msg, false), Map[String, String]())

  def result(r: Boolean) = if (r) success("success") else failure("failure")

  // add ApiModel parent properties to Model
  override protected def registerModel(model: Model) = {
    val mergeModel = try {
      val clz = Class.forName(model.qualifiedName.get)
      val klass = Reflector.scalaTypeOf(clz)
      val apiModel = Option(klass.erasure.getAnnotation(classOf[ApiModel]))
      apiModel match {
        case Some(am) =>
          val parent = am.parent()
          if (parent != classOf[Void]) {
            val pModel = Swagger.modelToSwagger(Reflector.scalaTypeOf(parent)).get
            val props = model.properties
            val pProps = pModel.properties.filterNot(p => props.map(_._1).contains(p._1))
            model.copy(properties = props ::: pProps)
          } else {
            model
          }
        case None => model
      }
    } catch {
      case e: Exception => model
    }
    super.registerModel(mergeModel)
  }

  notFound {
    // remove content type in case it was set through an action
    contentType = null
    serveStaticResource() getOrElse resourceNotFound()
  }
}
