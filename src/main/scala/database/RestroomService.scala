package app
package database

import zio.*

import java.util.UUID

import models.*

trait RestroomService:
  def add(data: AddRestroomData): IO[DomainError, Restroom]

  def list(around: Location): IO[DomainError, List[Restroom]]

  def reviews(restroomId: UUID): IO[DomainError, List[Review]]

  def addReview(restroomId: UUID, data: AddReviewData, authorId: String): IO[DomainError, UUID]

object RestroomService:
  def add(data: AddRestroomData): ZIO[RestroomService, DomainError, Restroom] =
    ZIO.serviceWithZIO[RestroomService](_.add(data))

  def list(around: Location): ZIO[RestroomService, DomainError, List[Restroom]] =
    ZIO.serviceWithZIO[RestroomService](_.list(around))

  def reviews(restroomId: UUID): ZIO[RestroomService, DomainError, List[Review]] =
    ZIO.serviceWithZIO[RestroomService](_.reviews(restroomId))

  def addReview(
    restroomId: UUID,
    data: AddReviewData,
    authorId: String
  ): ZIO[RestroomService, DomainError, UUID] =
    ZIO.serviceWithZIO[RestroomService](_.addReview(restroomId, data, authorId))
