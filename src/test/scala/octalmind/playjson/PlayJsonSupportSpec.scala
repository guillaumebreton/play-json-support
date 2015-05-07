package octalmind.playjson

import java.lang.StringBuilder

import akka.http.scaladsl.marshalling.ToEntityMarshaller
import akka.http.scaladsl.unmarshalling.FromEntityUnmarshaller
import akka.http.scaladsl.model.{ HttpCharsets, HttpEntity, MediaTypes }
import akka.http.scaladsl.testkit.ScalatestRouteTest

import org.scalatest.{ Matchers, WordSpec }
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import com.fasterxml.jackson.databind._
import play.api.libs.json.JsResultException
import play.api.data.validation.ValidationError

class PlayJsonSupportSpec extends WordSpec with Matchers with ScalatestRouteTest {
  object EmployeeJsonProtocol {

    case class Employee(fname: String, name: String, age: Int, id: Long, boardMember: Boolean)

    object Employee {
      val simple = Employee("Frank", "Smith", 42, 12345, false)
      val json = """{"fname":"Frank","name":"Smith","age":42,"id":12345,"boardMember":false}"""

      val utf8 = Employee("Fränk", "Smi√", 42, 12345, false)
      val utf8json =
        """{
        "fname": "Fränk",
        "name": "Smi√",
        "age": 42,
        "id": 12345,
        "boardMember": false
      }""".getBytes(HttpCharsets.`UTF-8`.nioCharset)

      val illegalEmployeeJson = """{"fname":"Little Boy","name":"Smith","age":7,"id":12345,"boardMember":true}"""
    }
    implicit val employee: Format[Employee] = (
      (JsPath \ "fname").format[String] and
      (JsPath \ "name").format[String] and
      (JsPath \ "age").format[Int](min(40)) and
      (JsPath \ "id").format[Long] and
      (JsPath \ "boardMember").format[Boolean])(Employee.apply _, unlift(Employee.unapply))

  }
  import EmployeeJsonProtocol._

  implicit val printer: PlayJsonSupport.Printer = (value: JsValue) ⇒ Json.stringify(value)
  implicit def marshaller: ToEntityMarshaller[Employee] = PlayJsonSupport.playJsonMarshaller[Employee]
  implicit def unmarshaller: FromEntityUnmarshaller[Employee] = PlayJsonSupport.playJsonUnmarshaller[Employee]

  "The play json support" should {
    "provide unmarshalling support for a case class" in {
      HttpEntity(MediaTypes.`application/json`, Employee.json) should unmarshalToValue(Employee.simple)
    }
    "provide marshalling support for a case class" in {
      val marshalled = marshal(Employee.simple)

      marshalled.data.utf8String shouldEqual
        """{"fname":"Frank","name":"Smith","age":42,"id":12345,"boardMember":false}"""
    }
    "use UTF-8 as the default charset for JSON source decoding" in {
      HttpEntity(MediaTypes.`application/json`, Employee.utf8json) should unmarshalToValue(Employee.utf8)
    }
    "provide proper error messages for requirement errors" in {
      val result = unmarshal(HttpEntity(MediaTypes.`application/json`, Employee.illegalEmployeeJson))

      result.isFailure shouldEqual true
      val ex = result.failed.get
      val expectedException = JsResultException(
        List(
          (JsPath \ "age", List(ValidationError("error.min", 40)))))
      ex shouldEqual expectedException
    }
  }

}
