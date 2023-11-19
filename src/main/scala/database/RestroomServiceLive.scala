package app
package database

import zio.*

import java.util.UUID

import models.*

class RestroomServiceLive(restroomRepository: RestroomRepository) extends RestroomService:
  def add(data: AddRestroomData): IO[RepositoryError, UUID] =
    restroomRepository.add(data)

  def list(around: Location): IO[RepositoryError, List[Restroom]] =
    restroomRepository.list(around)

object RestroomServiceLive:
  val layer: URLayer[RestroomRepository, RestroomService] = ZLayer(
    for {
      restroomRepository <- ZIO.service[RestroomRepository]
    } yield RestroomServiceLive(restroomRepository)
  )
