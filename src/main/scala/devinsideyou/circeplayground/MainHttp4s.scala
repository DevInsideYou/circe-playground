package devinsideyou
package circeplayground

import cats.data._
import cats.implicits._

import cats.effect.{ ExitCode, IO, IOApp }

import fs2.Stream

import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.HttpApp
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.CORS

object MainHttp4s extends IOApp {
  override def run(args: List[String]): IO[ExitCode] =
    for {
      fiber <- server.start
      _ <- IO(printlnHyphens(100))
      _ <- clientStream
        .evalMap(response => IO(printlnGood(response)))
        .take(2)
        .compile
        .drain
      _ <- IO(printlnHyphens(100))
      _ <- fiber.cancel // Use fiber.join instead of fiber.cancel
      // to make the server wait for your requests instead of exiting early.
      // You might want to user reStart instead of run for that.
    } yield ExitCode.Success

  val server: IO[Nothing] = {
    val httpApp: HttpApp[IO] =
      NonEmptyChain(
        CORS(GreetingRouteServer.dsl[IO].helloPost)
      ).reduceLeft(_ <+> _).orNotFound

    BlazeServerBuilder[IO]
      .bindHttp(8080)
      .withHttpApp(httpApp)
      .resource
      .use(_ => IO.never)
  }

  val clientStream: Stream[IO, GreetingRouteServer.Response] = {
    import scala.concurrent.ExecutionContext.global

    BlazeClientBuilder[IO](global).stream.flatMap { client =>
      List("Alpha", "Bravo", "Charlie", "Delta")
        .traverse(GreetingRouteClient.dsl[IO](client).helloPost)
        .pipe(Stream.evalSeq)
    }
  }
}
