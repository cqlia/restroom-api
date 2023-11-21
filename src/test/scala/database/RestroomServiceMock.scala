package app
package database

import zio.*
import zio.mock.*

import java.util.UUID

import models.*

object RestroomServiceMock extends Mock[RestroomService]:
  object Add extends Effect[AddRestroomData, Nothing, Restroom]

  object List extends Effect[Location, Nothing, List[Restroom]]

  object Reviews extends Effect[UUID, Nothing, List[Review]]

  object AddReview extends Effect[(UUID, AddReviewData, String), Nothing, UUID]

  override val compose: URLayer[Proxy, RestroomService] =
    ZLayer.fromFunction((proxy: Proxy) =>
      new RestroomService {
        override def list(around: Location): IO[DomainError, List[Restroom]] =
          proxy(List, around)

        override def add(data: AddRestroomData): IO[DomainError, Restroom] = proxy(Add, data)

        override def reviews(restroomId: UUID): IO[DomainError, List[Review]] =
          proxy(Reviews, restroomId)

        override def addReview(
          restroomId: UUID,
          data: AddReviewData,
          authorId: String
        ): IO[DomainError, UUID] =
          proxy(AddReview, (restroomId, data, authorId))
      }
    )
