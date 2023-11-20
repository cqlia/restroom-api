package app
package api

import zio.http.*
import zio.stream.ZStream

import java.io.File

object BaseRoutes:
  val app: HttpApp[Any] = handler(
    Response(
      status = Status.Ok,
      body = Body.fromStream(ZStream.fromResource("bathroom.png")),
      headers = Headers(
        // hardcoded values, but it's okay
        Header.ContentType(MediaType.image.png),
        Header.ContentLength(45097L)
      )
    )
  ).sandbox.toHttpApp
