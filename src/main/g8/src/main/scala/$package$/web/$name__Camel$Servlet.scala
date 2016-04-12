package $package$.web

import org.scalatra._
import $package$.support._

class $name;format="Camel"$Servlet extends $name;format="Camel"$Stack {

  get("/") {
  	contentType = null
    <html>
      <body>
        <h1>Hello, world!</h1>
        <a href="/swagger">Hello Swagger</a>
      </body>
    </html>
  }
}
