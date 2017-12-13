package ncreep

import ncreep.HList._

sealed trait HList

object HList {

  case class ::[H, T <: HList](head: H, tail: T) extends HList {
    def ::[H](head: H) = HList.::(head, this)

    override def toString = s"$head :: $tail"
  }

  sealed trait HNil extends HList {
    def ::[H](head: H) = HList.::(head, this)

    override def toString = "HNil"
  }

  val HNil: HNil = new HNil {}
}

sealed trait Append[L1 <: HList, 
                    L2 <: HList, 
                    R <: HList]

object Append {
  implicit def appendHead[H, 
                          T <: HList, 
                          L1 <: HList, 
                          L2 <: HList]
  (implicit 
   append: Append[T, L1, L2]): Append[H :: T, L1, H :: L2] =
     
    new Append[H :: T, L1, H :: L2] {}

  implicit def prependEmpty[L <: HList]: Append[HNil, L, L] =
    new Append[HNil, L, L] {}
}

sealed trait Member[Elem, L <: HList]

object Member {
  implicit def memberAnywhere[Elem, H, T <: HList]
   (implicit 
    m: Member[Elem, T]): Member[Elem, H :: T] =
      
    new Member[Elem, H :: T] {}

  implicit def headMember[H, T <: HList]: Member[H, H :: T] =
    new Member[H, H :: T] {}

}
