package devinsideyou
package circeplayground

import cats.data._
import cats.implicits._

import io.circe.refined._
import io.circe.syntax._

object MainConfig extends App {
  printlnHyphens(100)

  val fromConfigDecodedData: EitherNec[Throwable, Data] =
    ReadJsonFrom
      .defaultHoConfigIntoAccumulating[Data]
      .tap(_.bimap(printlnBad, printlnGood))
      .tap(_.map(_.asJson).tapAs(printlnHyphens(100)).foreach(printlnGood))

  printlnHyphens(100)
}
