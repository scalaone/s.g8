package $package$.support

import slick.backend.DatabaseComponent

import scala.concurrent.Await
import scala.concurrent.duration.Duration

trait DatabaseSupport extends slick.driver.MySQLDriver.API {
  def run[R](db: DatabaseComponent#DatabaseDef, a: DBIOAction[R, NoStream, Nothing]): R = Await.result(db.run(a), Duration.Inf)
}
