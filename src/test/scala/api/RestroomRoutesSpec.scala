package app
package api

import zio.Scope
import zio.test.*
import zio.http.*
import zio.http.Header.ContentType
import zio.http.endpoint.Endpoint
import zio.test.Assertion.*

object RestroomRoutesSpec extends ZIOSpecDefault:
  val specs: Spec[Any, Nothing] = suite("api"):
    suite("listing")(
      test("root ok status") {
        for
          response <- RestroomRoutes.app.runZIO(
            Request.get(URL(Root))
          )
          responseStatus = response.status
        yield assertTrue(responseStatus == Status.Ok)
      },
      test("root is png") {
        for
          response <- RestroomRoutes.app.runZIO(
            Request.get(URL(Root))
          )
          responseType = response.headers.get(Header.ContentType).get.mediaType
        yield assertTrue(responseType == MediaType.forFileExtension("png").get)
      }
    )

  override def spec: Spec[Any, Nothing] = specs.provide()
