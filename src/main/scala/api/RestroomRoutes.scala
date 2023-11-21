package app
package api

import zio.*
import zio.http.*
import zio.json.*

import java.util.UUID

import application.*
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
      },
    Method.POST / "restrooms" -> deviceKeyMiddleware ->
      Handler.fromFunctionZIO[(DeviceKeyContext, Request)] { case (context, request) =>
        val restroomData = parseBody[AddRestroomData](request.body)
        val effect = restroomData.flatMap(RestroomService.add)
        effect.foldZIO(
          handleError,
          r => ZIO.succeed(Response.json(r.toJson))
        )
      },
    Method.GET / "restrooms" / string("restroomId") / "reviews" ->
      handler { (restroomId: String, _: Request) =>
        parseUUID(restroomId) match
          case Some(restroomUuid) =>
            val effect = RestroomService.reviews(restroomUuid)
            effect.foldZIO(
              handleError,
              r => ZIO.succeed(Response.json(r.toJson))
            )
          case None => handleError(NotFoundError())
      },
    Method.POST / "restrooms" / string("restroomId") / "reviews" -> deviceKeyMiddleware ->
      Handler.fromFunctionZIO[(String, DeviceKeyContext, Request)] {
        case (restroomId, context, request) =>
          parseUUID(restroomId) match
            case Some(restroomUuid) =>
              val reviewData = parseBody[AddReviewData](request.body)
              val effect = reviewData.flatMap(d =>
                RestroomService.addReview(
                  restroomUuid,
                  d,
                  context.key
                )
              )

              effect.foldZIO(
                handleError,
                _ => ZIO.succeed(Response.status(Status.NoContent))
              )
            case None => handleError(NotFoundError())
      }
  ).sandbox.toHttpApp

  private def parseBody[T: JsonDecoder](body: Body): IO[RequestError, T] = body.asString
    .flatMap(
      _.fromJson[T].fold(
        _ => ZIO.fail(Throwable()),
        data => ZIO.succeed(data)
      )
    )
    .refineOrDie { (e: Throwable) =>
      RequestError("invalid body")
    }

  private def parseUUID(value: String): Option[UUID] = try Some(UUID.fromString(value))
  catch case iae: IllegalArgumentException => None

  private def handleError(err: DomainError): UIO[Response] = err match {
    case RepositoryError(cause) =>
      ZIO.logErrorCause(cause.getMessage, Cause.fail(cause)) *>
        ZIO.succeed(
          Response.text("internal server error :(").status(Status.InternalServerError)
        )
    case RequestError(message) =>
      ZIO.succeed(Response.text(message).status(Status.BadRequest))
    case NotFoundError() =>
      ZIO.succeed(Response.text("item not found").status(Status.NotFound))
  }
