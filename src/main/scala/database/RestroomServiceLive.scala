package app
package database

import zio.*

import java.util.UUID

import models.*

class RestroomServiceLive(restroomRepository: RestroomRepository) extends RestroomService:
  def add(data: AddRestroomData): IO[DomainError, Restroom] =
    if (!data.location.inBounds) ZIO.fail(RequestError("location out of bounds"))
    else if (data.title.length > 256) ZIO.fail(RequestError("Title is too long"))
    else
      restroomRepository
        .byTitle(data.title)
        .flatMap(
          _.fold(
            restroomRepository
              .add(data)
              .flatMap(id => restroomRepository.byId(id))
              .map(_.get)
          )(ZIO.succeed(_))
        )

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

  override def addReview(
    restroomId: UUID,
    data: AddReviewData,
    authorId: String
  ): IO[DomainError, UUID] =
    if (data.rating > 5 || data.rating < 0) ZIO.fail(RequestError("rating out of bounds"))
    else
      ZIO.ifZIO(restroomRepository.byId(restroomId).map(_.isDefined))(
        onTrue = ZIO.ifZIO(restroomRepository.reviewByAuthor(restroomId, authorId).map(_.isEmpty))(
          onTrue = restroomRepository.addReview(restroomId, data, authorId),
          onFalse = ZIO.fail(RequestError("User already created review"))
        ),
        onFalse = ZIO.fail(NotFoundError())
      )

object RestroomServiceLive:
  val layer: URLayer[RestroomRepository, RestroomService] = ZLayer(
    for {
      restroomRepository <- ZIO.service[RestroomRepository]
    } yield RestroomServiceLive(restroomRepository)
  )
