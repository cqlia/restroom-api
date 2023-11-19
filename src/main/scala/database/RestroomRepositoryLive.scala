package app
package database

import io.getquill.*
import io.getquill.jdbczio.Quill
import zio.*

import java.util.UUID

import models.*

class RestroomRepositoryLive(quill: Quill.Postgres[Literal]) extends RestroomRepository:
  override def add(data: AddRestroomData): IO[RepositoryError, UUID] = ZIO.fail(null)

  override def list(around: Location): IO[RepositoryError, List[Restroom]] = ZIO.succeed(List())

object RestroomRepositoryLive:
  val layer: URLayer[Quill.Postgres[Literal], RestroomRepository] = ZLayer {
    for {
      quill <- ZIO.service[Quill.Postgres[Literal]]
    } yield RestroomRepositoryLive(quill)
  }
