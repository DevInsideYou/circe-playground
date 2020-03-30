package devinsideyou

import cats.implicits._

import io.circe.{ Decoder, Encoder }
import io.circe.syntax._

package object circeplayground {
  final implicit class PipeAndTap[A](private val a: A) extends AnyVal {
    @inline final def pipe[B](ab: A => B): B = ab(a)
    @inline final def tap[U](au: A => U): A = { au(a); a }
    @inline final def tapAs[U](u: => U): A = { u; a }
  }

  def printlnGood(in: Any): Unit =
    println(Console.GREEN + in + Console.RESET)

  def printlnBad(in: Any): Unit =
    Console.err.println(Console.RED + in + Console.RESET)

  def printlnHyphens(n: Int): Unit =
    println("─" * n)

  def roundTrip[A: Encoder: Decoder](a: A): Decoder.Result[A] =
    a.asJson
      .tap(printlnGood)
      .tapAs(printlnHyphens(100))
      .pipe(_.as[A])
      .tap(_.bimap(printlnBad, printlnGood))

  implicit def EitherDecoder[L: Decoder, R: Decoder]: Decoder[Either[L, R]] =
    Decoder[R].map(Right.apply) or Decoder[L].map(Left.apply)

  implicit def EitherEncoder[L: Encoder, R: Encoder]: Encoder[Either[L, R]] = {
    case Left(l)  => Encoder[L].apply(l)
    case Right(r) => Encoder[R].apply(r)
  }
}
