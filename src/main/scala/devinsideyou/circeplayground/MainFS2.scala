package devinsideyou
package circeplayground

import java.nio.file.Paths

import cats.effect.{ ExitCode, IO, IOApp }

import fs2.Stream

import io.circe.generic.auto._
import io.circe.refined._

object MainFS2 extends IOApp {
  def run(args: List[String]): IO[ExitCode] =
    for {
      _ <- IO(printlnHyphens(100))
      _ <- stream.map(printlnGood).take(2).compile.drain
      _ <- IO(printlnHyphens(100))
    } yield ExitCode.Success

  val stream: Stream[IO, Data] =
    Paths
      .get("./target/scala-2.13/classes/data-array.json")
      .pipe(ReadJsonFrom.pathIntoStreamOf[IO, Data])
}
