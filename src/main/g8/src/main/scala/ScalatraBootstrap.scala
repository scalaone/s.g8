import $package$.web._
import $package$.support.{ResourceSwagger, ResourcesApp}
import org.scalatra._
import javax.servlet.ServletContext

class ScalatraBootstrap extends LifeCycle {
  override def init(context: ServletContext) {
  	implicit val swagger = ResourceSwagger
    context.mount(new ResourcesApp, "/api-docs")
    context.mount(new $name;format="Camel"$Swagger, "/user/*")
    context.mount(new $name;format="Camel"$Servlet, "/*")
  }
}
