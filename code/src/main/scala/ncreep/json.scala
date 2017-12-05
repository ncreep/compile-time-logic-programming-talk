package ncreep
import Json._

trait Json

object Json {
  case object JsNull extends Json {
    override def toString = "null"
  }

  case class JsString(value: String) extends Json {
    override def toString = s""""$value""""
  }

  case class JsNumber(value: BigDecimal) extends Json {
    override def toString = value.toString
  }

  case class JsBoolean(value: Boolean) extends Json {
    override def toString = value.toString
  }

  case class JsObject(values: List[(String, Json)]) extends Json {
    override def toString =
      values.map {
        case (name, value) =>
          s""""$name": ${value.toString}"""
      }.mkString("{", ", ", "}")
  }

  case class JsArray(values: List[Json]) extends Json {
    override def toString = values.map(_.toString).mkString("[", ", ", "]")
  }

  def write[A](value: A)(implicit writer: Writer[A]): Json = writer.write(value)
}

trait Writer[A] {
  def write(value: A): Json
}

object Writer {
  implicit val stringWriter: Writer[String] = new Writer[String] {
    def write(value: String) = JsString(value)
  }

  implicit val intWriter: Writer[Int] = new Writer[Int] {
    def write(value: Int) = JsNumber(BigDecimal(value))
  }

  implicit val booleanWriter: Writer[Boolean] = new Writer[Boolean] {
    def write(value: Boolean) = JsBoolean(value)
  }

  implicit def listWriter[A](implicit aWriter: Writer[A]): Writer[List[A]] =
    new Writer[List[A]] {
      def write(values: List[A]) = JsArray {
        values.map(aValue => aWriter.write(aValue))
      }
    }

  implicit def optionWriter[A](implicit aWriter: Writer[A]): Writer[Option[A]] =
    new Writer[Option[A]] {
      def write(value: Option[A]): Json =
        value.map(aWriter.write)
          .getOrElse(JsNull)
    }

  implicit def tupleWriter[A, B](implicit aWriter: Writer[A],
                                 bWriter: Writer[B]): Writer[(A, B)] =
    new Writer[(A, B)] {
      def write(value: (A, B)): Json = JsArray {
        List(
          aWriter.write(value._1),
          bWriter.write(value._2)
        )
      }
    }

  implicit def eitherWriter[A, B](implicit aWriter: Writer[A],
                                  bWriter: Writer[B]): Writer[Either[A, B]] =
    new Writer[Either[A, B]] {
      def write(value: Either[A, B]): Json =
        value.fold(
          a => aWriter.write(a),
          b => bWriter.write(b))
    }
}

