package app
package database

import zio.*

import java.util.UUID

import models.*

class RestroomServiceLive(restroomRepository: RestroomRepository) extends RestroomService:
  def add(data: AddRestroomData): IO[DomainError, UUID] =
    restroomRepository.add(data)

  def list(around: Location): IO[DomainError, List[Restroom]] =
    if (
      around.longitude > 180
      || around.longitude < -180.0
      || around.latitude > 90
      || around.latitude < -90
    ) ZIO.fail(RequestError("location out of bounds"))
    else restroomRepository.list(around)

object RestroomServiceLive:
  val layer: URLayer[RestroomRepository, RestroomService] = ZLayer(
    for {
      restroomRepository <- ZIO.service[RestroomRepository]
    } yield RestroomServiceLive(restroomRepository)
  )
