package app
package database

import zio.*
import zio.test.*

object RestroomServiceSpec extends ZIOSpecDefault:
  override def spec: Spec[Any, Nothing] = suite("restroom service")()
