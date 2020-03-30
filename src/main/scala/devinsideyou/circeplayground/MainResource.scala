package devinsideyou
package circeplayground

import cats.implicits._

import io.circe.refined._
import io.circe.syntax._

object MainResource extends App {
  printlnHyphens(100)

  val fromResourceDecodedData: Either[Throwable, Data] =
    "data.json"
      .pipe(ReadJsonFrom.resourceInto[Data])
      .tap(_.bimap(printlnBad, printlnGood))
      .tap(_.map(_.asJson).tapAs(printlnHyphens(100)).foreach(printlnGood))

  printlnHyphens(100)
}
