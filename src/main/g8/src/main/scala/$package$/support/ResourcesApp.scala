package $package$.support

import org.json4s.JsonDSL._
import org.json4s.{JField, JObject, JValue}
import org.scalatra.ScalatraServlet
import org.scalatra.swagger.DataType.{ContainerDataType, ValueDataType}
import org.scalatra.swagger._

class ResourcesApp(implicit val swagger: Swagger) extends ScalatraServlet with JacksonSwaggerBase {
  private[this] def generateDataType(dataType: DataType): List[JField] = {
    dataType match {
      case t: ValueDataType if t.qualifiedName.isDefined =>
        List(("\$ref" -> s"#/definitions/\${t.name}"))
      case t: ValueDataType =>
        List(("type" -> t.name), ("format" -> t.format))
      case t: ContainerDataType =>
        List(("type" -> "array"), ("items" -> generateDataType(t.typeArg.get)))
    }
  }

  private[this] def generateProp(property: ModelProperty): List[JField] = {
    val desc: JField = "description" -> property.description
    desc :: generateDataType(property.`type`)
  }

  override protected def renderSwagger2(docs: List[ApiType]): JValue = {
    ("swagger" -> "2.0") ~
      ("info" ->
        ("title" -> swagger.apiInfo.title) ~
          ("version" -> swagger.apiVersion) ~
          ("description" -> swagger.apiInfo.description) ~
          ("termsOfService" -> swagger.apiInfo.termsOfServiceUrl) ~
          ("contact" -> (
            ("name" -> swagger.apiInfo.contact))) ~
          ("license" -> (
            ("name" -> swagger.apiInfo.license) ~
              ("url" -> swagger.apiInfo.licenseUrl)))) ~
      ("paths" ->
        (docs.filter(_.apis.nonEmpty).flatMap {
          doc =>
            doc.apis.collect { case api: Endpoint =>
              (api.path -> api.operations.map { operation =>
                (operation.method.toString.toLowerCase -> (
                  ("tags" -> List(doc.description.get + " : " + doc.resourcePath)) ~
                    ("operationId" -> operation.nickname) ~
                    ("summary" -> operation.summary) ~!
                    ("schemes" -> operation.protocols) ~!
                    ("consumes" -> operation.consumes) ~!
                    ("produces" -> operation.produces) ~
                    ("parameters" -> operation.parameters.map { parameter =>
                      ("name" -> parameter.name) ~
                        ("description" -> parameter.description) ~
                        ("required" -> parameter.required) ~
                        ("in" -> parameter.paramType.toString.toLowerCase) ~~
                        (if (parameter.paramType.toString.toLowerCase == "body") {
                          List(JField("schema", JObject(JField("\$ref", s"#/definitions/\${parameter.`type`.name}"))))
                        } else {
                          generateDataType(parameter.`type`)
                        })
                    }) ~
                    ("responses" ->
                      ("200" ->
                        (if (operation.responseClass.name == "void") {
                          JField("description", "No response")
                        } else {
                          JField("schema", generateDataType(operation.responseClass))
                        })) ~~
                        operation.responseMessages.map { response =>
                          (response.code.toString ->
                            ("description", response.message) ~~
                              response.responseModel.map { model =>
                                List(JField("schema", JObject(JField("\$ref", s"#/definitions/\${model}"))))
                              }.getOrElse(Nil))
                        }.toMap
                      ) ~!
                    ("security" -> (operation.authorizations.flatMap { requirement =>
                      swagger.authorizations.find(_.`type` == requirement).map { auth =>
                        auth match {
                          case a: OAuth => (requirement -> a.scopes)
                          case _ => (requirement -> List.empty)
                        }
                      }
                    }).toMap)
                  )
                  )
              }.toMap)
            }.toMap
        }.toMap)) ~
      ("definitions" -> docs.flatMap { doc =>
        doc.models.map { case (name, model) =>
          (name ->
            ("properties" -> model.properties.map { case (name, property) =>
              (name -> generateProp(property))
            }.toMap))
        }
      }.toMap) ~
      ("securityDefinitions" -> (swagger.authorizations.flatMap { auth =>
        (auth match {
          case a: OAuth => a.grantTypes.headOption.map { grantType =>
            grantType match {
              case g: ImplicitGrant => ("oauth2" -> JObject(
                JField("type", "oauth2"),
                JField("flow", "implicit"),
                JField("authorizationUrl", g.loginEndpoint.url),
                JField("scopes", a.scopes.map(scope => JField(scope, scope)))))
              case g: AuthorizationCodeGrant => ("oauth2" -> JObject(
                JField("type", "oauth2"),
                JField("flow", "implicit"),
                JField("authorizationUrl", g.tokenRequestEndpoint.url),
                JField("tokenUrl", g.tokenEndpoint.url),
                JField("scopes", a.scopes.map(scope => JField(scope, scope)))))
            }
          }
          case a: ApiKey => Some(("api_key" -> JObject(
            JField("type", "apiKey"),
            JField("name", a.keyname),
            JField("in", a.passAs))))
        })
      }).toMap)
  }
}


object ResourceApiInfo extends ApiInfo(
  "The Resource API",
  "Docs for the Resource API",
  "http://scalatra.org",
  "apiteam@scalatra.org",
  "MIT",
  "http://opensource.org/licenses/MIT")

object ResourceSwagger extends Swagger("2.0", "1.0.0", ResourceApiInfo)
