package $package$.web

import org.scalatra._
import $package$.support._
import $package$.data._
import $package$.data.Tables._

class $controller;format="Camel"$Controller extends $appName;format="Camel"$Stack with $appName;format="Camel"$Database {
  get("/$controller;format="lower"$", api[List[$controller;format="Camel"$Row], Unit]("list $controller;format=\"lower\"$")) {
    $appName;format="lower"$Run($controller;format="Camel"$.result)
  }
  post("/$controller;format="lower"$", api[String, $controller;format="Camel"$Row]("add $controller;format=\"lower\"$")) {
    result($appName;format="lower"$Run($controller;format="Camel"$ += parsedBody.extract[$controller;format="Camel"$Row]) > 0)
  }
  put("/$controller;format="lower"$", api[String, $controller;format="Camel"$Row]("update $controller;format=\"lower\"$")) {
    val row = parsedBody.extract[$controller;format="Camel"$Row]
    result($appName;format="lower"$Run($controller;format="Camel"$.filter(_.id === row.id).map(_.name).update(row.name)) > 0)
  }
  delete("/$controller;format="lower"$", api[String, Long]("delete $controller;format=\"lower\"$")) {
    result($appName;format="lower"$Run($controller;format="Camel"$.filter(_.id === parsedBody.extract[Long]).delete) > 0)
  }
}
