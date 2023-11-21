package app
package api

import zio.*
import zio.http.Header.ContentType
import zio.http.*
import zio.http.endpoint.Endpoint
import zio.json.*
import zio.mock.Expectation.*
import zio.test.Assertion.*
import zio.test.*

import java.util.UUID

import database.RestroomServiceMock
import models.*

object RestroomRoutesSpec extends ZIOSpecDefault:
  private val restroomA = Restroom(
    id = UUID.fromString("79316cfe-3455-4f73-9a21-5ec642ad5b06"),
    title = "Restroom A",
    description = None,
    distance = Some(0.1),
    location = Location(25.0, 15.0),
    reviewAverage = 5.0
  )

  private val reviewA = Review(
    id = UUID.fromString("fef3f44a-75bb-45e9-a2e1-2720e07fbcc3"),
    rating = 5.0,
    body = None
  )

  private val restroomDataA = AddRestroomData(
    title = "value",
    description = None,
    location = Location(0.0, 0.0)
  )

  private val getListingMock: ULayer[RestroomService] = RestroomServiceMock.List(
    equalTo(restroomA.location),
    value(List(restroomA))
  )

  private val emptyMock: ULayer[RestroomService] = RestroomServiceMock.empty

  private val getReviewsMock: ULayer[RestroomService] = RestroomServiceMock.Reviews(
    equalTo(restroomA.id),
    value(List(reviewA))
  )

  private val addRestroomMock: ULayer[RestroomService] = RestroomServiceMock.Add(
    equalTo(restroomDataA),
    value(restroomA)
  )

  private val addReviewA = AddReviewData(rating = 5.0, body = None)

  private val addReviewMock: ULayer[RestroomService] = RestroomServiceMock.AddReview(
    equalTo(restroomA.id, addReviewA, "test"),
    value(reviewA.id)
  )

  override def spec: Spec[Any, Throwable] = suite("restroom routes")(
    suite("listing")(
      test("location parsing") {
        for {
          responseA <- RestroomRoutes.app.runZIO(
            Request.get(
              URL(
                Root / "restrooms",
                queryParams = QueryParams(
                  "latitude" -> restroomA.location.latitude.toString,
                  "longitude" -> restroomA.location.longitude.toString
                )
              )
            )
          )

          bodyA <- responseA.body.asString
          expectedA = List(restroomA).toJson
        } yield assertTrue(bodyA == expectedA)
      }.provide(getListingMock),
      test("missing parameter") {
        for {
          response <- RestroomRoutes.app.runZIO(
            Request.get(
              URL(
                Root / "restrooms",
                queryParams = QueryParams(
                  "latitude" -> 100.0.toString
                )
              )
            )
          )

          responseStatus = response.status
        } yield assertTrue(responseStatus == Status.BadRequest)
      }.provide(emptyMock)
    ),
    suite("review listing")(
      test("success path") {
        for {
          response <- RestroomRoutes.app.runZIO(
            Request.get(URL(Root / "restrooms" / restroomA.id.toString / "reviews"))
          )

          body <- response.body.asString
          expected = List(reviewA).toJson
        } yield assertTrue(body == expected)
      }.provide(getReviewsMock),
      test("invalid id reviews") {
        for {
          response <- RestroomRoutes.app.runZIO(
            Request.get(URL(Root / "restrooms" / "invalid" / "reviews"))
          )

          status = response.status
        } yield assertTrue(status == Status.NotFound)
      }.provide(emptyMock)
    ),
    suite("restroom posting")(
      test("missing auth") {
        for {
          response <- RestroomRoutes.app.runZIO(
            Request.post(URL(Root / "restrooms"), Body.empty)
          )

          responseStatus = response.status
        } yield assertTrue(responseStatus == Status.Unauthorized)
      }.provide(emptyMock),
      test("success path") {
        val sentBody = restroomDataA.toJson

        for {
          response <- RestroomRoutes.app.runZIO(
            Request
              .post(URL(Root / "restrooms"), Body.fromString(sentBody))
              .addHeaders(Headers(Header.Authorization.Bearer("test")))
          )

          body <- response.body.asString
          expectedBody = restroomA.toJson
        } yield assertTrue(body == expectedBody)
      }.provide(addRestroomMock),
      test("invalid body") {
        for {
          response <- RestroomRoutes.app.runZIO(
            Request
              .post(URL(Root / "restrooms"), Body.empty)
              .addHeaders(Headers(Header.Authorization.Bearer("test")))
          )

          status = response.status
        } yield assertTrue(status == Status.BadRequest)
      }.provide(emptyMock)
    ),
    suite("review posting")(
      test("invalid id reviews") {
        for {
          response <- RestroomRoutes.app.runZIO(
            Request
              .post(URL(Root / "restrooms" / "invalid" / "reviews"), Body.empty)
              .addHeaders(Headers(Header.Authorization.Bearer("test")))
          )

          status = response.status
        } yield assertTrue(status == Status.NotFound)
      }.provide(emptyMock),
      test("invalid body") {
        for {
          response <- RestroomRoutes.app.runZIO(
            Request
              .post(URL(Root / "restrooms" / restroomA.id.toString / "reviews"), Body.empty)
              .addHeaders(Headers(Header.Authorization.Bearer("test")))
          )

          status = response.status
        } yield assertTrue(status == Status.BadRequest)
      }.provide(emptyMock),
      test("success path") {
        val body = addReviewA.toJson

        for {
          response <- RestroomRoutes.app.runZIO(
            Request
              .post(
                URL(Root / "restrooms" / restroomA.id.toString / "reviews"),
                Body.fromString(body)
              )
              .addHeaders(Headers(Header.Authorization.Bearer("test")))
          )

          status = response.status
        } yield assertTrue(status == Status.NoContent)
      }.provide(addReviewMock)
    )
  )
