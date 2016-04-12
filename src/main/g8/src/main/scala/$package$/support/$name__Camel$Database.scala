package $package$.support

import scala.concurrent.Await
import scala.concurrent.duration.Duration

trait $name;format="Camel"$Database extends DatabaseSupport {
  val $name;format="lower"$Db = Database.forConfig("$name;format="lower"$")

  def $name;format="lower"$Run[R](a: DBIOAction[R, NoStream, Nothing]): R = Await.result($name;format="lower"$Db.run(a), Duration.Inf)
}
