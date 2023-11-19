package app
package api

import zio.*
import zio.http.*
import zio.json.*

import models.*

object RestroomRoutes:
  val app: HttpApp[RestroomService] = Routes(
    Method.GET / "restrooms" ->
      handler { (req: Request) =>
        val latitude = req.url.queryParams.get("latitude").flatMap(_.toDoubleOption)
        val longitude = req.url.queryParams.get("longitude").flatMap(_.toDoubleOption)

        (latitude, longitude) match
          case (Some(latitude), Some(longitude)) =>
            val effect = RestroomService.list(Location(latitude, longitude))
            effect.foldZIO(
              handleError,
              r => ZIO.succeed(Response.json(r.toJson))
            )
          case _ => handleError(RequestError("missing parameters"))
      }
  ).sandbox.toHttpApp

  private def handleError(err: DomainError): UIO[Response] = err match {
    case RepositoryError(cause) =>
      ZIO.logErrorCause(cause.getMessage, Cause.fail(cause)) *>
        ZIO.succeed(
          Response.text("internal server error :(").status(Status.InternalServerError)
        )
    case RequestError(message) =>
      ZIO.succeed(Response.text(message).status(Status.BadRequest))
  }
