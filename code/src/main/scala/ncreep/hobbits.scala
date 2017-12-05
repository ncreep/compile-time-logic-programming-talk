package ncreep

trait Frodo

trait Bilbo

trait Primula

trait Mirabella

trait Belladonna

trait Hildbrand

trait Sigismond

trait Gerontius

trait Parent[A, B]

trait Grandparent[A, B]

trait Siblings[A, B]

trait FirstCousinOnceRemoved[A, B]

object Hobbits {
  implicit val p1: Parent[Belladonna, Bilbo] = new Parent[Belladonna, Bilbo] {}
  implicit val p2: Parent[Primula, Frodo] = new Parent[Primula, Frodo] {}
  implicit val p3: Parent[Mirabella, Primula] = new Parent[Mirabella, Primula] {}
  implicit val p4: Parent[Hildbrand, Sigismond] = new Parent[Hildbrand, Sigismond] {}
  implicit val p5: Parent[Gerontius, Mirabella] = new Parent[Gerontius, Mirabella] {}
  implicit val p6: Parent[Gerontius, Belladonna] = new Parent[Gerontius, Belladonna] {}
  implicit val p7: Parent[Gerontius, Hildbrand] = new Parent[Gerontius, Hildbrand] {}

  implicit def grandparent[A, B, C](implicit
                                    p1: Parent[A, C],
                                    p2: Parent[C, B]): Grandparent[A, B] =
    new Grandparent[A, B] {}

  implicit def grandparent2[A, B, C](implicit
                                     p1: Parent[C, B],
                                     p2: Parent[A, C]): Grandparent[A, B] =
    new Grandparent[A, B] {}

  def findGrandparent[A, B](b: B)
                           (implicit gp: Grandparent[A, B]): Grandparent[A, B] =
    gp

  def findGrandchild[A, B](a: A)
                          (implicit gp: Grandparent[A, B]): Grandparent[A, B] =
    gp

  implicit def siblings[A, B, C](implicit
                                 p1: Parent[C, A],
                                 p2: Parent[C, B],
                                 neq: A \= B): Siblings[A, B] =
    new Siblings[A, B] {}

  def findSibling[A, B](a: A)
                       (implicit s: Siblings[A, B]): Siblings[A, B] =
    s

  implicit def firstCousinFirstRemoved[A, B, C, D](implicit
                                                   gp: Grandparent[C, B],
                                                   s: Siblings[C, D],
                                                   p: Parent[D, A]): FirstCousinOnceRemoved[A, B] =
    new FirstCousinOnceRemoved[A, B] {}

  def findFirstCousinFirstRemoved[A, B](a: A)
                                       (implicit f: FirstCousinOnceRemoved[A, B]): FirstCousinOnceRemoved[A, B] =
    f

}
