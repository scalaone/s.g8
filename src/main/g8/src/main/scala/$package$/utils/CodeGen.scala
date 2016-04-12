package $package$.utils

import $package$.support.{ConfigSupport, DatabaseSupport}
import slick.driver.MySQLDriver

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object CodeGen extends App with DatabaseSupport with ConfigSupport {

  import conf._
  import slick.codegen.SourceCodeGenerator

  import scala.concurrent.ExecutionContext.Implicits.global

  val db = Database.forConfig("gen")
  val output = args(0)
  val pkg = getString("gen.pkg")
  // fetch data model
  val modelAction = MySQLDriver.createModel(Some(MySQLDriver.defaultTables))
  // you can filter specific tables here
  val modelFuture = db.run(modelAction)
  // customize code generator
  val codegenFuture = modelFuture.map(model => new SourceCodeGenerator(model) {

    // 不生成 ddl
    override val ddlEnabled = false

    // 不生成注释
    override def docWithCode(doc: String, code: String): String = code

    override def Table = new Table(_) {

      override def PlainSqlMapper = new PlainSqlMapper {
        override def enabled = false
      }

      override def TableValue = new TableValue {
        // 使用 Macro TableQuery
        override def code = s"lazy val \$name = TableQuery[\${TableClass.name}]"
      }

      override def EntityType = new EntityType {
        // 全部生成 case class
        override def code: String = {
          val args = columns.map(c =>
            c.default.map(v =>
              s"\${c.name}: \${c.exposedType} = \$v"
            ).getOrElse(
              s"\${c.name}: \${c.exposedType}"
            )
          ).mkString(", ")
          val prns = (parents.take(1).map(" extends " + _) ++ parents.drop(1).map(" with " + _)).mkString("")
          s"""case class \$name(\$args)\$prns"""
        }
      }

      override def TableClass = new TableClass {
        override def optionEnabled = false

        def mkHListNames(names: List[String]): String = names match {
          case Nil => "HNil"
          case e :: tail => s"HCons(\$e," + mkHListNames(tail) + ")"
        }

        // 列数 > 22 时,将 HCons 转换为 case class
        override def code: String = {
          val prns = parents.map(" with " + _).mkString("")
          val args = model.name.schema.map(n => s"""Some("\$n")""") ++ Seq("\"" + model.name.table + "\"")
          val types = EntityType.types
          val entityArgs = columns.map(c =>
            c.default.map(v =>
              s"\${c.name}: \${c.exposedType} = \$v"
            ).getOrElse(
              s"\${c.name}: \${c.exposedType}"
            )
          ).mkString(", ")
          val rowName = s"\${name}Row"
          val hconsName = s"\${name}HCons"
          val hconsConverter = if (hlistEnabled)
            s"""type \$hconsName = \$types

implicit def hcons2\$rowName(h: \$hconsName): \$rowName = h match {
  case \${mkHListNames(columns.map(_.name).toList)} =>
    \$rowName(\${columns.map(_.name).mkString(",")})
}

implicit def row2\$hconsName(r: \$rowName): \$hconsName = r match {
  case \$rowName(\${columns.map(_.name).mkString(",")}) =>
    \${columns.map(_.name).mkString(" :: ")} :: HNil
}

def \$hconsName(\$entityArgs): \$hconsName = {
  \${compoundValue(columns.map(_.name))}
}"""
          else ""
          val newElementType = if (hlistEnabled) hconsName else elementType
          s"""\$hconsConverter
class \$name(tag: Tag) extends Table[\$newElementType](tag, \${args.mkString(", ")})\$prns {
  \${indent(body.map(_.mkString("\n")).mkString("\n\n"))}
}
        """.trim()
        }
      }

      override def Column = new Column(_) {
        // 去掉无用的类型声明
        override def code =
          s"""val \$name = column[\$actualType]("\${model.name}"\${options.map(", " + _).mkString("")})"""
      }

      override def code: Seq[String] = definitions.flatMap(_.getEnabled).filterNot(d => d.isInstanceOf[EntityType]).map(_.docWithCode)
    }

    def entities = {
      tables.map(_.EntityType.code).mkString("\n\n")
    }

    // 把 Entity 生成为独立的 case class,以便 swagger 使用,swagger 无法拿到 inner class 的 meta
    override def packageCode(profile: String, pkg: String, container: String, parentType: Option[String]): String = {
      s"""
package \${pkg}

\${entities}

object \${container} extends {
  val p = \$profile
} with \${container}
trait \${container}\${parentType.map(t => s" extends \$t").getOrElse("")} {
  val profile: slick.driver.JdbcProfile = \${container}.p
  import profile.api._
  \${indent(code)}
}
      """.trim()
    }
  }

  )
  val codegen = Await.result(codegenFuture, Duration.Inf)
  codegen.writeToFile(
    "slick.driver.MySQLDriver", output, pkg, "Tables", "Tables.scala"
  )
}
