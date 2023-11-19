package app
package models

import zio.*

import java.util.UUID

trait RestroomService:
  def add(data: AddRestroomData): IO[RepositoryError, UUID]

  def list(around: Location): IO[RepositoryError, List[Restroom]]

object RestroomService:
  def add(data: AddRestroomData): ZIO[RestroomService, RepositoryError, UUID] =
    ZIO.serviceWithZIO[RestroomService](_.add(data))

  def list(around: Location): ZIO[RestroomService, RepositoryError, List[Restroom]] =
    ZIO.serviceWithZIO[RestroomService](_.list(around))
