package $package$.web

import org.scalatra._
import $package$.support._
/*
import $package$.data._
import $package$.data.Tables._
*/

case class User(@p(description = "User Id") id: Long,
                @p(description = "User Name") name: String)

class $name;format="Camel"$Swagger extends $name;format="Camel"$Stack with $name;format="Camel"$Database {
  post("/user", api[List[User], Unit]("List users")) {
    List(
      User(1, "haha"),
      User(2, "hehe")
    )
  }
  /*
  get("/test", api[List[TestRow], Unit]("list tests")) {
    $name;format="lower"$Run(Test.result)
  }
  post("/test", api[String, TestRow]("add test")) {
    result($name;format="lower"$Run(Test += parsedBody.extract[TestRow]) > 0)
  }
  put("/test", api[String, TestRow]("update test")) {
    val row = parsedBody.extract[TestRow]
    result($name;format="lower"$Run(Test.filter(_.id === row.id).map(_.name).update(row.name)) > 0)
  }
  delete("/test", api[String, Long]("delete test")) {
    result($name;format="lower"$Run(Test.filter(_.id === parsedBody.extract[Long]).delete) > 0)
  }
  */
}
