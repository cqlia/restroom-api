package app
package models

import zio.*
import zio.mock.*

import java.util.UUID

object RestroomRepositoryMock extends Mock[RestroomRepository]:
  object Add extends Effect[AddRestroomData, Nothing, UUID]
  object List extends Effect[Location, Nothing, List[Restroom]]

  override val compose: URLayer[Proxy, RestroomRepository] =
    ZLayer.fromFunction((proxy: Proxy) =>
      new RestroomRepository {
        override def list(around: Location): IO[RepositoryError, List[Restroom]] =
          proxy(List, around)

        override def add(data: AddRestroomData): IO[RepositoryError, UUID] = proxy(Add, data)
      }
    )
