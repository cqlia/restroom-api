package app
package models

import zio.*
import zio.mock.*

import java.util.UUID

object RestroomRepositoryMock extends Mock[RestroomRepository]:
  object Add extends Effect[AddRestroomData, Nothing, UUID]
  object List extends Effect[Location, Nothing, List[Restroom]]
  object ById extends Effect[UUID, Nothing, Option[Restroom]]
  object Reviews extends Effect[UUID, Nothing, List[Review]]

  override val compose: URLayer[Proxy, RestroomRepository] =
    ZLayer.fromFunction((proxy: Proxy) =>
      new RestroomRepository {
        override def list(around: Location): IO[RepositoryError, List[Restroom]] =
          proxy(List, around)

        override def add(data: AddRestroomData): IO[RepositoryError, UUID] = proxy(Add, data)

        override def reviews(restroomId: UUID): IO[RepositoryError, List[Review]] =
          proxy(Reviews, restroomId)

        override def byId(id: UUID): IO[RepositoryError, Option[Restroom]] = proxy(ById, id)
      }
    )
