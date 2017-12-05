package ncreep

import akka.NotUsed
import akka.stream.scaladsl.GraphDSL.Implicits._
import akka.stream.scaladsl._
import akka.stream.{Inlet, Outlet, ClosedShape => AkkaClosedShape}
import ncreep.HList._

import scala.annotation.implicitNotFound

sealed trait Port

object Port {

  trait `1` extends Port

  trait `2` extends Port

}

trait Stage {
  type Self <: Stage

  type CreateIn[P <: Port, A] = In[Self, P, A]
  type CreateOut[P <: Port, A] = Out[Self, P, A]

  def createIn[P <: Port, A](inlet: Inlet[A]) = In[Self, P, A](inlet)

  def createOut[P <: Port, A](outlet: Outlet[A]) = Out[Self, P, A](outlet)

  type Ports <: HList
}

// The contravariance is needed so that implicit resolution doesn't try to infer huge upper-bound types
case class In[-S <: Stage, -P <: Port, A](inlet: Inlet[A])

case class Out[-S <: Stage, -P <: Port, A](outlet: Outlet[A])

case class Connect[O <: Out[_, _, A], I <: In[_, _, A], A](o: O, i: I) {
  def in: In[_, _, A] = i

  def out: Out[_, _, A] = o
}

sealed trait AllPorts[Stages <: HList, Ports <: HList]

object AllPorts {
  implicit val portsOfEmpty: AllPorts[HNil, HNil] =
    new AllPorts[HNil, HNil] {}

  implicit def portsOfList[S <: Stage, 
                           StagesTail <: HList, 
                           PortsTail <: HList, 
                           Result <: HList]
    (implicit 
     tailPorts: AllPorts[StagesTail, PortsTail],
     append: Append[S#Ports, PortsTail, Result]): AllPorts[S :: StagesTail, Result] =
    new AllPorts[S :: StagesTail, Result] {}
}

sealed trait ClosedShape[Ports <: HList, AllPorts <: HList] {
  def connections: List[Connect[_, _, _]]
}

object ClosedShape {
  implicit def emptyConnected[AllPorts <: HList]: ClosedShape[HNil, AllPorts] =
    new ClosedShape[HNil, AllPorts] {
      def connections = Nil
    }

  implicit def outFirst[O <: Out[S, P, _], 
                        I <: In[_, _, _], 
                        S <: Stage, 
                        P <: Port, 
                        PortsTail <: HList, 
                        AllPorts <: HList]
  (implicit 
   headConnection: Connect[O, I, _],
   member: Member[I, AllPorts],
   tailClosedShape: ClosedShape[PortsTail, AllPorts]): ClosedShape[O :: PortsTail, AllPorts] =

    new ClosedShape[O :: PortsTail, AllPorts] {
      def connections = headConnection :: tailClosedShape.connections
    }

  implicit def inFirst[I <: In[S, P, _], 
                       O <: Out[_, _, _], 
                       S <: Stage, 
                       P <: Port, 
                       PortsTail <: HList, 
                       AllPorts <: HList]
    (implicit 
     headConnection: Connect[O, I, _],
     member: Member[O, AllPorts],
     tailColsedShape: ClosedShape[PortsTail, AllPorts]): ClosedShape[I :: PortsTail, AllPorts] =

    new ClosedShape[I :: PortsTail, AllPorts] {
      def connections = headConnection :: tailColsedShape.connections
    }

  def build[Stages <: HList, 
            Ports <: HList](stages: Stages)
    (implicit 
     allPorts: AllPorts[Stages, Ports],
     closedShape: ClosedShape[Ports, Ports],
     builder: GraphDSL.Builder[NotUsed]): AkkaClosedShape = {

    def makeConnection[A](c: Connect[_, _, A]): Unit =
      c.out.outlet ~> c.in.inlet

    // have to convert to a Set since each Connect is added twice, once per port
    val connections = closedShape.connections.toSet
    connections.foreach(c => makeConnection(c))

    AkkaClosedShape
  }
}
