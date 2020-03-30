package devinsideyou
package circeplayground

import cats.Eq

import eu.timepit.refined.types.numeric.PosInt

final case class Data(key1: Key1, key2: PosInt) {
  final override def toString: String =
    s"""|$productPrefix(
        |$content
        |)""".stripMargin

  private[this] def content: String =
    productElementNames
      .zip(productIterator)
      .map {
        case (name, value) => s"  $name = $value"
      }
      .mkString("\n")
}

object Data extends HasJsonCodecFor[Data] {
  implicit val eq: Eq[Data] = Eq.fromUniversalEquals
}
