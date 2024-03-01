package neotype

import scala.quoted.FromExpr
import scala.util.matching.Regex

given NewtypeWithoutValidation: Newtype[String] with
  override inline def validate(string: String): Boolean = true

type RegexNewtype = RegexNewtype.Type
given RegexNewtype: Newtype[String] with
  override inline def validate(string: String): Boolean =
    string.matches("^[a-zA-Z0-9]*$")

  given Show[RegexNewtype] with
    def show(value: RegexNewtype): String = unwrap(value)

  extension (value: RegexNewtype) //
    def length: Int = unwrap(value).length

type PositiveIntNewtype = PositiveIntNewtype.Type
given PositiveIntNewtype: Newtype[Int] with
  override inline def validate(int: Int): Boolean =
    int > 0

type PositiveLongNewtype = PositiveLongNewtype.Type
given PositiveLongNewtype: Newtype[Long] with
  override inline def validate(long: Long): Boolean = long > 0

type NonEmptyStringNewtype = NonEmptyStringNewtype.Type
given NonEmptyStringNewtype: Newtype[String] with
  override inline def validate(string: String): Boolean =
    string.length > 0

// Newtype for a custom case class
final case class Person(name: String, age: Int)

object Person:
  //   def unapply(x: Expr[T])(using Quotes): Option[T]
  import scala.quoted.*

  given FromExpr[Person] with
    def unapply(x: Expr[Person])(using Quotes): Option[Person] =
      x match
        case '{ Person(${ Expr(name) }, ${ Expr(age) }) } =>
          Some(Person(name, age))
        case _ => None

given PersonNewtype: Newtype[Person] with
  override inline def validate(person: Person): Boolean =
    person.name.length > 0 && person.age > 0

given VariousStringNewtype: Newtype[String] with
  override inline def validate(string: String): Boolean =
    string.startsWith("a") && string.endsWith("z") &&
      string.length > 0 && string.contains("b") &&
      string.isUUID && string.isURL && string.isEmail

type IsUUID = IsUUID.Type
given IsUUID: Newtype[String] with
  override inline def validate(string: String): Boolean =
    string.isUUID ?? "MUST BE UUID"

type IsURL = IsURL.Type
given IsURL: Newtype[String] with
  override inline def validate(string: String): Boolean =
    string.isURL
