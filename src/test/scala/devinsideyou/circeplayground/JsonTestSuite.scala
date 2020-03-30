package devinsideyou
package circeplayground

import io.circe.testing.ArbitraryInstances

trait JsonTestSuite extends TestSuite with ArbitraryInstances {
  final protected val CodecTests =
    io.circe.testing.CodecTests

  final protected val GoldenCodecTests =
    io.circe.testing.golden.GoldenCodecTests
}
