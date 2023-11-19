package app
package api

import zio.http.*

import java.io.File

object BaseRoutes:
  val app: HttpApp[Any] = Handler
    .fromFile(
      new File(
        // ZIO seems to mess up URL -> path translation when loading resources as files
        // see <https://github.com/zio/zio-http/issues/2525>
        getClass.getResource("/bathroom.png").toURI.getPath
      )
    )
    .sandbox
    .toHttpApp
