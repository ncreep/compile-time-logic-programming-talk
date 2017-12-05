package ncreep

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Broadcast => AkkaBroadcast, Flow => AkkaFlow, Merge => AkkaMerge, Sink => AkkaSink, Source => AkkaSource, _}
import ncreep.HList._

object ClosedShapeApp extends App {
  implicit val system = ActorSystem("ClosedShape")
  implicit val materializer = ActorMaterializer()

  val graph = RunnableGraph.fromGraph {
    GraphDSL.create() { implicit builder =>
      
      case class Source() extends GenericSource[Int](AkkaSource(1 to 10)) { type Self = Source }

      case class Sink() extends GenericSink[String](AkkaSink.foreach(println)) { type Self = Sink }

      case class Broadcast() extends GenericBroadcast[Int](i => AkkaBroadcast[Int](i)) { type Self = Broadcast }

      case class Merge() extends GenericMerge[String](i => AkkaMerge[String](i)) { type Self = Merge }

      case class Flow1() extends GenericFlow[Int, Int](AkkaFlow[Int].map(_ * 2)) { type Self = Flow1 }
      case class Flow2() extends GenericFlow[Int, String](AkkaFlow[Int].map(_.toString ++ "!")) { type Self = Flow2 }
      case class Flow3() extends GenericFlow[String, String](AkkaFlow[String].map(s => "*" ++ s ++ "*")) { type Self = Flow3 }
      case class Flow4() extends GenericFlow[Int, String](AkkaFlow[Int].map(_.toString ++ "?")) { type Self = Flow4 }

      val source = Source()
      val sink = Sink()
      val broadcast = Broadcast()
      val merge = Merge()
      val flow1 = Flow1()
      val flow2 = Flow2()
      val flow3 = Flow3()
      val flow4 = Flow4()

      implicit val c1: Connect[Source#Out1, Flow1#In1, Int] = Connect(source.out1, flow1.in1)
      implicit val c2: Connect[Flow1#Out1, Broadcast#In1, Int] = Connect(flow1.out1, broadcast.in1)
      implicit val c3: Connect[Broadcast#Out1, Flow2#In1, Int] = Connect(broadcast.out1, flow2.in1)
      implicit val c4: Connect[Broadcast#Out2, Flow4#In1, Int] = Connect(broadcast.out2, flow4.in1)
      implicit val c5: Connect[Flow2#Out1, Merge#In1, String] = Connect(flow2.out1, merge.in1)
      implicit val c6: Connect[Flow4#Out1, Merge#In2, String] = Connect(flow4.out1, merge.in2)
      implicit val c7: Connect[Merge#Out1, Flow3#In1, String] = Connect(merge.out1, flow3.in1)
      implicit val c8: Connect[Flow3#Out1, Sink#In1, String] = Connect(flow3.out1, sink.in1)

      val allStages =
        source :: sink :: broadcast :: merge :: flow1 :: flow2 :: flow3 :: flow4 :: HNil

      ClosedShape.build(allStages)
    }
  }

  graph.run()
}
