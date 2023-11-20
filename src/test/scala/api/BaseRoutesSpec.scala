package app
package api

import zio.http.*
import zio.test.*

object BaseRoutesSpec extends ZIOSpecDefault:
  override def spec: Spec[Any, Nothing] = suite("base routes")(
    suite("listing")(
      test("root ok status") {
        for
          response <- BaseRoutes.app.runZIO(
            Request.get(URL(Root))
          )
          responseStatus = response.status
        yield assertTrue(responseStatus == Status.Ok)
      },
      test("root is png") {
        for
          response <- BaseRoutes.app.runZIO(
            Request.get(URL(Root))
          )
          responseType = response.headers.get(Header.ContentType)
        yield assertTrue(responseType match
          case Some(headerType) => headerType.mediaType == MediaType.image.png
          case _                => false
        )
      }
    )
  ).provide()
