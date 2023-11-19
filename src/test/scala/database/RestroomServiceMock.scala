package app
package database

import zio.*
import zio.mock.*

import java.util.UUID

import models.*

object RestroomServiceMock extends Mock[RestroomService]:
  object Add extends Effect[AddRestroomData, Nothing, UUID]

  object List extends Effect[Location, Nothing, List[Restroom]]

  override val compose: URLayer[Proxy, RestroomService] =
    ZLayer.fromFunction((proxy: Proxy) =>
      new RestroomService {
        override def list(around: Location): IO[RepositoryError, List[Restroom]] =
          proxy(List, around)

        override def add(data: AddRestroomData): IO[RepositoryError, UUID] = proxy(Add, data)
      }
    )
