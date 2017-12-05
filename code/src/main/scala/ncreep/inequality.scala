package ncreep

// Copied from here: https://stackoverflow.com/a/24519628/1274237
trait \=[A, B]

object \= {
  implicit def neq[A, B]: A \= B = new \=[A, B] {}

  implicit def neqAmbig1[A]: A \= A = ???

  implicit def neqAmbig2[A]: A \= A = ???
}
