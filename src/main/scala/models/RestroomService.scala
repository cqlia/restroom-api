package app
package models

import zio.*

import java.util.UUID

trait RestroomService:
  def add(data: AddRestroomData): IO[DomainError, UUID]

  def list(around: Location): IO[DomainError, List[Restroom]]

object RestroomService:
  def add(data: AddRestroomData): ZIO[RestroomService, DomainError, UUID] =
    ZIO.serviceWithZIO[RestroomService](_.add(data))

  def list(around: Location): ZIO[RestroomService, DomainError, List[Restroom]] =
    ZIO.serviceWithZIO[RestroomService](_.list(around))
