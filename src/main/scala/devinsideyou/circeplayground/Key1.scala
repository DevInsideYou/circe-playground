package devinsideyou
package circeplayground

import eu.timepit.refined.types.string.NonEmptyString

import io.circe.{ Decoder, Encoder }
import io.circe.refined._

final case class Key1(value: NonEmptyString)

object Key1 {
  implicit val encoder: Encoder[Key1] =
    Encoder[NonEmptyString].contramap(_.value)

  implicit val decoder: Decoder[Key1] =
    Decoder[NonEmptyString].map(apply)
}
