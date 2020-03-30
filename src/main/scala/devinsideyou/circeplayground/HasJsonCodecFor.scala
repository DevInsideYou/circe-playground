package devinsideyou
package circeplayground

import io.circe.Codec
import io.circe.generic.codec.DerivedAsObjectCodec

import shapeless.Lazy

trait HasJsonCodecFor[A] {
  final implicit def codec[A](
      implicit
      c: Lazy[DerivedAsObjectCodec[A]]
    ): Codec.AsObject[A] =
    c.value
}
