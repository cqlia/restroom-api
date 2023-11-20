package app
package database

import zio.*
import zio.mock.*

import java.util.UUID

import models.*

object RestroomServiceMock extends Mock[RestroomService]:
  object Add extends Effect[AddRestroomData, Nothing, UUID]

  object List extends Effect[Location, Nothing, List[Restroom]]

  object Reviews extends Effect[UUID, Nothing, List[Review]]

  override val compose: URLayer[Proxy, RestroomService] =
    ZLayer.fromFunction((proxy: Proxy) =>
      new RestroomService {
        override def list(around: Location): IO[DomainError, List[Restroom]] =
          proxy(List, around)

        override def add(data: AddRestroomData): IO[DomainError, UUID] = proxy(Add, data)

        override def reviews(restroomId: UUID): IO[DomainError, List[Review]] =
          proxy(Reviews, restroomId)
      }
    )
