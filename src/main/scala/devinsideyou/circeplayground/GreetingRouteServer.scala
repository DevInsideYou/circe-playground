package devinsideyou.circeplayground

import cats.implicits._

import cats.effect.Sync

import io.circe.syntax._

import org.http4s.dsl._

import org.http4s.{ EntityDecoder, EntityEncoder, HttpRoutes }
import org.http4s.circe._

trait GreetingRouteServer[F[_]] {
  def helloPost: HttpRoutes[F]
}

object GreetingRouteServer {
  def dsl[F[_]: Sync]: GreetingRouteServer[F] =
    new GreetingRouteServer[F] with Http4sDsl[F] {
      final override val helloPost: HttpRoutes[F] =
        HttpRoutes.of {
          case request @ POST -> Root / "hello" =>
            request.as[Request].flatMap { request =>
              Ok(Response(s"Hello ${request.userName}!").asJson)
            }
        }
    }

  case class Request(userName: String)
  object Request extends HasJsonCodecFor[Request] {
    implicit def entityDecoder[F[_]: Sync]: EntityDecoder[F, Request] = jsonOf
    implicit def entityEncoder[F[_]]: EntityEncoder[F, Request] = jsonEncoderOf
  }

  case class Response(greeting: String)
  object Response extends HasJsonCodecFor[Response] {
    implicit def entityDecoder[F[_]: Sync]: EntityDecoder[F, Response] = jsonOf
    implicit def entityEncoder[F[_]]: EntityEncoder[F, Response] = jsonEncoderOf
  }
}
