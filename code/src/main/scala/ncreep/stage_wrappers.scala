package ncreep

import akka.{Done, NotUsed}
import akka.stream.scaladsl._
import ncreep.HList._
import ncreep.Port._
import scala.concurrent.Future

abstract class GenericSource[A](value: Source[A, NotUsed])
                               (implicit builder: GraphDSL.Builder[NotUsed]) extends Stage {
  type Out1 = CreateOut[`1`, A]
  type Ports = Out1 :: HNil

  private val shape = builder.add(value)

  val out1 = createOut[`1`, A](shape.out)
}

abstract class GenericSink[A](value: Sink[A, Future[Done]])
                             (implicit builder: GraphDSL.Builder[NotUsed]) extends Stage {
  type In1 = CreateIn[`1`, A]

  type Ports = In1 :: HNil

  private val shape = builder.add(value)

  val in1 = createIn[`1`, A](shape.in)
}

abstract class GenericBroadcast[A](makeBroadcast: Int => Broadcast[A])
                                  (implicit builder: GraphDSL.Builder[NotUsed]) extends Stage {
  type In1 = CreateIn[`1`, A]
  type Out1 = CreateOut[`1`, A]
  type Out2 = CreateOut[`2`, A]

  type Ports = In1 :: Out1 :: Out2 :: HNil

  private val shape = builder.add(makeBroadcast(2))

  val in1 = createIn[`1`, A](shape.in)
  val out1 = createOut[`1`, A](shape.out(0))
  val out2 = createOut[`2`, A](shape.out(1))
}

abstract class GenericMerge[A](makeMerge: Int => Merge[A])
                              (implicit builder: GraphDSL.Builder[NotUsed]) extends Stage {
  type In1 = CreateIn[`1`, A]
  type In2 = CreateIn[`2`, A]
  type Out1 = CreateOut[`1`, A]

  type Ports = In1 :: In2 :: Out1 :: HNil

  private val shape = builder.add(makeMerge(2))

  val in1 = createIn[`1`, A](shape.in(0))
  val in2 = createIn[`2`, A](shape.in(1))
  val out1 = createOut[`1`, A](shape.out)
}

abstract class GenericFlow[A, B](value: Flow[A, B, NotUsed])
                                (implicit builder: GraphDSL.Builder[NotUsed]) extends Stage {
  type In1 = CreateIn[`1`, A]
  type Out1 = CreateOut[`1`, B]

  type Ports = In1 :: Out1 :: HNil

  private val shape = builder.add(value)

  val in1 = createIn[`1`, A](shape.in)
  val out1 = createOut[`1`, B](shape.out)
}
