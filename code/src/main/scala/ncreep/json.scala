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

  def write[A](value: A)(implicit writer: CanWrite[A]): Json = writer.write(value)
}

trait CanWrite[A] {
  def write(value: A): Json
}

object CanWrite {
  implicit val canWriteString: CanWrite[String] = new CanWrite[String] {
    def write(value: String) = JsString(value)
  }

  implicit val canWriteInt: CanWrite[Int] = new CanWrite[Int] {
    def write(value: Int) = JsNumber(BigDecimal(value))
  }

  implicit val canWriteBoolean: CanWrite[Boolean] = new CanWrite[Boolean] {
    def write(value: Boolean) = JsBoolean(value)
  }

  implicit def canWriteList[A](implicit aWriter: CanWrite[A]): CanWrite[List[A]] =
    new CanWrite[List[A]] {
      def write(values: List[A]) = JsArray {
        values.map(aValue => aWriter.write(aValue))
      }
    }

  implicit def canWriteOption[A](implicit aWriter: CanWrite[A]): CanWrite[Option[A]] =
    new CanWrite[Option[A]] {
      def write(value: Option[A]): Json =
        value.map(aWriter.write)
          .getOrElse(JsNull)
    }

  implicit def canWriteTuple[A, B](implicit aWriter: CanWrite[A],
                                   bWriter: CanWrite[B]): CanWrite[(A, B)] =
    new CanWrite[(A, B)] {
      def write(value: (A, B)): Json = JsArray {
        List(
          aWriter.write(value._1),
          bWriter.write(value._2)
        )
      }
    }

  implicit def canWriteEither[A, B](implicit aWriter: CanWrite[A],
                                    bWriter: CanWrite[B]): CanWrite[Either[A, B]] =
    new CanWrite[Either[A, B]] {
      def write(value: Either[A, B]): Json =
        value.fold(
          a => aWriter.write(a),
          b => bWriter.write(b))
    }
}

