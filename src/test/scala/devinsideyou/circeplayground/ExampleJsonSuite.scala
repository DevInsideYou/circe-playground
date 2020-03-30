package devinsideyou
package circeplayground

import eu.timepit.refined.scalacheck.all._

import io.circe.refined._

final class ExampleJsonSuite extends JsonTestSuite {
  checkAll("Codec[Data]", CodecTests[Data].codec) // unnecessary
  checkAll("GoldenCodec[Data]", GoldenCodecTests[Data].goldenCodec)
}
