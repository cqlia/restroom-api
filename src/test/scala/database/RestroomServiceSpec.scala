package app
package database

import zio.*
import zio.test.Assertion.*
import zio.test.*

import models.*

object RestroomServiceSpec extends ZIOSpecDefault:
  override def spec: Spec[Any, Nothing] = suite("restroom service")(
    suite("listing")(
      test("invalid location") {
        for {
          result <- RestroomService.list(Location(0.0, 1000.0)).exit
        } yield assert(result)(fails(equalTo(RequestError("location out of bounds"))))
      }.provide(RestroomServiceLive.layer, RestroomRepositoryMock.empty)
    )
  )
