package app
package models

import zio.*
import zio.mock.*

import java.util.UUID

import application.RestroomRepository

object RestroomRepositoryMock extends Mock[RestroomRepository]:
  object Add extends Effect[AddRestroomData, Nothing, UUID]
  object List extends Effect[Location, Nothing, List[Restroom]]
  object ById extends Effect[UUID, Nothing, Option[Restroom]]
  object ByTitle extends Effect[String, Nothing, Option[Restroom]]
  object Reviews extends Effect[UUID, Nothing, List[Review]]
  object AddReview extends Effect[(UUID, AddReviewData, String), Nothing, UUID]
  object ReviewByAuthor extends Effect[(UUID, String), Nothing, Option[Review]]

  override val compose: URLayer[Proxy, RestroomRepository] =
    ZLayer.fromFunction((proxy: Proxy) =>
      new RestroomRepository {
        override def list(around: Location): IO[RepositoryError, List[Restroom]] =
          proxy(List, around)

        override def add(data: AddRestroomData): IO[RepositoryError, UUID] = proxy(Add, data)

        override def reviews(restroomId: UUID): IO[RepositoryError, List[Review]] =
          proxy(Reviews, restroomId)

        override def byId(id: UUID): IO[RepositoryError, Option[Restroom]] = proxy(ById, id)

        override def byTitle(title: String): IO[RepositoryError, Option[Restroom]] =
          proxy(ByTitle, title)

        override def addReview(
          restroomId: UUID,
          data: AddReviewData,
          authorId: String
        ): IO[RepositoryError, UUID] =
          proxy(AddReview, (restroomId, data, authorId))

        override def reviewByAuthor(
          restroomId: UUID,
          authorId: String
        ): IO[RepositoryError, Option[Review]] =
          proxy(ReviewByAuthor, (restroomId, authorId))
      }
    )
