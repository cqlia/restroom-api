package app
package database

import zio.*

import java.util.UUID

import models.*

class RestroomServiceLive(restroomRepository: RestroomRepository) extends RestroomService:
  def add(data: AddRestroomData): IO[DomainError, Restroom] =
    if (!data.location.inBounds) ZIO.fail(RequestError("location out of bounds"))
    else
      for {
        id <- restroomRepository.add(data)
        restroom <- restroomRepository.byId(id)
        // if this returns null, something has gone really wrong
      } yield restroom.get

  def list(around: Location): IO[DomainError, List[Restroom]] =
    if (!around.inBounds) ZIO.fail(RequestError("location out of bounds"))
    else restroomRepository.list(around)

  override def reviews(restroomId: UUID): IO[DomainError, List[Review]] =
    ZIO.ifZIO(
      restroomRepository.byId(restroomId).map(_.isDefined)
    )(
      onTrue = restroomRepository.reviews(restroomId),
      onFalse = ZIO.fail(NotFoundError())
    )

  override def addReview(restroomId: UUID, data: AddReviewData): IO[DomainError, UUID] =
    if (data.rating > 5 || data.rating < 0) ZIO.fail(RequestError("rating out of bounds"))
    else
      ZIO.ifZIO(
        restroomRepository.byId(restroomId).map(_.isDefined)
      )(
        onTrue = restroomRepository.addReview(restroomId, data),
        onFalse = ZIO.fail(NotFoundError())
      )

object RestroomServiceLive:
  val layer: URLayer[RestroomRepository, RestroomService] = ZLayer(
    for {
      restroomRepository <- ZIO.service[RestroomRepository]
    } yield RestroomServiceLive(restroomRepository)
  )
