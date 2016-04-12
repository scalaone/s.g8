package $package$.support

import courier._

import scala.concurrent.ExecutionContext

trait MailSupport extends ConfigSupport {

  import conf._

  val mailer = Mailer(getString("report.domain"), 25).auth(true)
    .as(getString("report.from"), getString("report.password"))()
  implicit val executionContext = ExecutionContext.Implicits.global
}
