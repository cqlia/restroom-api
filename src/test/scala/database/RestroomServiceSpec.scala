package app
package database

import zio.*
import zio.mock.Expectation.*
import zio.test.Assertion.*
import zio.test.*

import java.util.UUID

import models.*

object RestroomServiceSpec extends ZIOSpecDefault:
  private val getMissingMock: ULayer[RestroomRepository] = RestroomRepositoryMock.ById(
    anything,
    value(None)
  )

  override def spec: Spec[Any, Nothing] = suite("restroom service")(
    suite("listing")(
      test("invalid location") {
        for {
          result <- RestroomService.list(Location(0.0, 1000.0)).exit
        } yield assert(result)(fails(equalTo(RequestError("location out of bounds"))))
      }.provide(RestroomServiceLive.layer, RestroomRepositoryMock.empty)
    ),
    suite("review listing")(
      test("missing restroom") {
        for {
          result <- RestroomService
            .reviews(UUID.fromString("fef3f44a-75bb-45e9-a2e1-2720e07fbcc3"))
            .exit
        } yield assert(result)(fails(equalTo(NotFoundError())))
      }.provide(RestroomServiceLive.layer, getMissingMock)
    )
  )
