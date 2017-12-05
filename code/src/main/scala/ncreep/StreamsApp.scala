package ncreep

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl.GraphDSL.Implicits._
import akka.stream.scaladsl._

object StreamsApp extends App {
  implicit val system = ActorSystem("Test")
  implicit val materializer = ActorMaterializer()

  val graph = RunnableGraph.fromGraph {
    GraphDSL.create() { implicit builder: GraphDSL.Builder[NotUsed] =>
      val in = Source(1 to 10)
      val out = Sink.foreach(println)

      val bcast = builder.add(Broadcast[Int](2))
      val merge = builder.add(Merge[String](2))

      val f1 = Flow[Int].map(_ * 2)
      val f2 = Flow[Int].map(_.toString ++ "!")
      val f3 = Flow[String].map(s => "*" ++ s ++ "*")
      val f4 = Flow[Int].map(_.toString ++ "?")

      in ~> f1 ~> bcast ~> f2 ~> merge ~> f3 ~> out
      bcast ~> f4 ~> merge

      akka.stream.ClosedShape
    }
  }

  graph.run()
}
