package devinsideyou
package circeplayground

import java.net.URL
import java.nio.file.{ Path, Paths }

import scala.io.{ BufferedSource, Source }

import cats.data._
import cats.implicits._

import cats.effect.{ Blocker, ContextShift, Sync }

import com.typesafe.config.{ Config, ConfigFactory }

import fs2.{ text, Stream }

import fs2.io.{ file }

import io.circe.Decoder
import io.circe.parser.decode

import io.circe.fs2.{ decoder, stringArrayParser }

object ReadJsonFrom {
  def urlInto[A: Decoder](url: String): Either[Throwable, A] =
    getUrl(url).flatMap(urlInto[A])

  private[this] def getUrl(url: String): Either[Throwable, URL] =
    Either.catchNonFatal(new URL(url))

  def urlInto[A: Decoder](url: URL): Either[Throwable, A] =
    fromURL(url)
      .map(_.getLines.mkString)
      .flatMap(decode[A])

  private[this] def fromURL(url: URL): Either[Throwable, BufferedSource] =
    Either.catchNonFatal(Source.fromURL(url))

  def resourceInto[A: Decoder](resourceName: String): Either[Throwable, A] =
    getResource(resourceName).flatMap(urlInto[A])

  private[this] def getResource(resourceName: String): Either[Throwable, URL] =
    Either.catchNonFatal(getClass.getClassLoader.getResource(resourceName))

  def defaultHoConfigIntoAccumulating[A: Decoder]: EitherNec[Throwable, A] =
    Either
      .catchNonFatal(ConfigFactory.load())
      .toEitherNec
      .flatMap(hoConfigIntoAccumulating[A])

  def hoConfigIntoAccumulating[A: Decoder](
      config: Config
    ): EitherNec[Throwable, A] =
    io.circe
      .config
      .parser
      .decodeAccumulating[A](config)
      .toEither
      .leftMap(NonEmptyChain.fromNonEmptyList)

  def pathIntoStreamOf[F[_]: ContextShift: Sync, A: Decoder](
      path: String
    ): Either[Throwable, Stream[F, A]] =
    getPath(path).map(pathIntoStreamOf[F, A])

  private[this] def getPath(path: String): Either[Throwable, Path] =
    Either.catchNonFatal(Paths.get(path))

  def pathIntoStreamOf[F[_]: ContextShift: Sync, A: Decoder](
      path: Path
    ): Stream[F, A] =
    Stream.resource(Blocker[F]).flatMap { blocker =>
      file
        .readAll(path, blocker, 4096)
        .through(text.utf8Decode)
        .through(stringArrayParser)
        .through(decoder[F, A])
    }
}
