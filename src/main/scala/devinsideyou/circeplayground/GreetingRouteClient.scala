package devinsideyou
package circeplayground

import cats.effect.Sync

import io.circe.syntax._

import org.http4s.circe._
import org.http4s.implicits._

import org.http4s.client.Client
import org.http4s.dsl.io.POST
import org.http4s.client.dsl.Http4sClientDsl

trait GreetingRouteClient[F[_]] {
  def helloPost(userName: String): F[GreetingRouteServer.Response]
}

object GreetingRouteClient {
  def dsl[F[_]: Sync](client: Client[F]): GreetingRouteClient[F] =
    new GreetingRouteClient[F] with Http4sClientDsl[F] {
      final override def helloPost(
          userName: String
        ): F[GreetingRouteServer.Response] =
        POST(
          body = GreetingRouteServer.Request(userName).asJson,
          uri = uri"http://localhost:8080/hello"
        ).pipe(client.expect[GreetingRouteServer.Response])
    }
}
