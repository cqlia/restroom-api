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
import models.{Location, Restroom, RestroomService}

object RestroomRoutesSpec extends ZIOSpecDefault:
  private val restroomA = Restroom(
    id = UUID.fromString("79316cfe-3455-4f73-9a21-5ec642ad5b06"),
    title = "Restroom A",
    description = None,
    distance = 0.1,
    location = Location(25.0, 15.0),
    reviewAverage = 5.0
  )

  private val getListingMock: ULayer[RestroomService] = RestroomServiceMock.List(
    equalTo(restroomA.location),
    value(List(restroomA))
  )

  private val emptyMock: ULayer[RestroomService] = RestroomServiceMock.empty

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
    )
  )
