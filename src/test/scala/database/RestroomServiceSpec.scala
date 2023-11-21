package app
package database

import zio.*
import zio.mock.Expectation.*
import zio.test.Assertion.*
import zio.test.*

import java.util.UUID

import models.*

object RestroomServiceSpec extends ZIOSpecDefault:
  private val reviewA = Review(
    id = UUID.fromString("fef3f44a-75bb-45e9-a2e1-2720e07fbcc3"),
    rating = 5.0,
    body = None
  )

  private val restroomA = Restroom(
    id = UUID.fromString("fef3f44a-75bb-45e9-a2e1-2720e07fbcc3"),
    title = "Restroom A",
    description = None,
    distance = Some(0.1),
    location = Location(25.0, 15.0),
    reviewAverage = 5.0
  )

  private val getMissingMock: ULayer[RestroomRepository] = RestroomRepositoryMock.ById(
    anything,
    value(None)
  )

  private val duplicateReviewMock: ULayer[RestroomRepository] = RestroomRepositoryMock.ById(
    equalTo(restroomA.id),
    value(Some(restroomA))
  ) ++ RestroomRepositoryMock.ReviewByAuthor(anything, value(Some(reviewA)))

  private val addReviewMock: ULayer[RestroomRepository] = RestroomRepositoryMock.ById(
    equalTo(restroomA.id),
    value(Some(restroomA))
  ) ++ RestroomRepositoryMock.ReviewByAuthor(anything, value(None))
    ++ RestroomRepositoryMock.AddReview(anything, value(reviewA.id))

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
    ),
    suite("review creation")(
      test("duplicate review") {
        for {
          result <- RestroomService
            .addReview(restroomA.id, AddReviewData(5.0, None), "example-id")
            .exit
        } yield assert(result)(fails(equalTo(RequestError("User already created review"))))
      }.provide(RestroomServiceLive.layer, duplicateReviewMock),
      test("invalid restroom") {
        for {
          result <- RestroomService
            .addReview(restroomA.id, AddReviewData(5.0, None), "example-id")
            .exit
        } yield assert(result)(fails(equalTo(NotFoundError())))
      }.provide(RestroomServiceLive.layer, getMissingMock),
      test("out of bounds rating") {
        for {
          result <- RestroomService
            .addReview(restroomA.id, AddReviewData(10.0, None), "example-id")
            .exit
        } yield assert(result)(fails(equalTo(RequestError("rating out of bounds"))))
      }.provide(RestroomServiceLive.layer, RestroomRepositoryMock.empty),
      test("add review successful") {
        for {
          result <- RestroomService
            .addReview(restroomA.id, AddReviewData(5.0, None), "example-id")
            .either
        } yield assertTrue(result == Right(restroomA.id))
      }.provide(RestroomServiceLive.layer, addReviewMock)
    )
  )
