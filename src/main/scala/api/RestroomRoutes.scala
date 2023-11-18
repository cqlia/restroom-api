package app
package api

import zio.*
import zio.http.*
import zio.json.*

import java.io.File
import scala.io.Source

import models.Restroom

object RestroomRoutes:
  val app: HttpApp[Any] = Routes(
    Method.GET / "restrooms" ->
      handler(
        Response.json(
          "[]"
        )
      ),

    // catch everything else
    RoutePattern.any -> Handler.fromFile(
      new File(
        // ZIO seems to mess up URL -> path translation when loading resources as files
        // see <https://github.com/zio/zio-http/issues/2525>
        getClass.getResource("/bathroom.png").toURI.getPath
      )
    )
  ).sandbox.toHttpApp
