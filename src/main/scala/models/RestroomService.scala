package app
package models

import zio.*

import java.util.UUID

trait RestroomService:
  def add(data: AddRestroomData): IO[DomainError, UUID]

  def list(around: Location): IO[DomainError, List[Restroom]]

  def reviews(restroomId: UUID): IO[DomainError, List[Review]]

object RestroomService:
  def add(data: AddRestroomData): ZIO[RestroomService, DomainError, UUID] =
    ZIO.serviceWithZIO[RestroomService](_.add(data))

  def list(around: Location): ZIO[RestroomService, DomainError, List[Restroom]] =
    ZIO.serviceWithZIO[RestroomService](_.list(around))

  def reviews(restroomId: UUID): ZIO[RestroomService, DomainError, List[Review]] =
    ZIO.serviceWithZIO[RestroomService](_.reviews(restroomId))
