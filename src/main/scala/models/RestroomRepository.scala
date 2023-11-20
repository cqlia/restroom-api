package app
package models

import zio._

import java.util.UUID

final case class AddRestroomData(title: String, description: Option[String], location: Location)

trait RestroomRepository:
  /** Adds a new restroom to the repository.
    *
    * @param data
    *   Information describing the restroom to be added
    * @return
    *   UUID of the restroom
    */
  def add(data: AddRestroomData): IO[RepositoryError, UUID]

  /** Returns a list of all restrooms, sorted by lowest distance to `around`.
    *
    * @param around
    *   Location used for ordering
    */
  def list(
    around: Location
  ): IO[RepositoryError, List[Restroom]]

  /** Fetches a restroom from its UUID.
    * @param id
    *   Reference UUID for restroom
    */
  def byId(id: UUID): IO[RepositoryError, Option[Restroom]]

  /** Returns a list of all reviews for some restroom, ordered by creation time.
    * @param restroomId
    *   Reference UUID for restroom
    */
  def reviews(restroomId: UUID): IO[RepositoryError, List[Review]]

object RestroomRepository:
  def add(data: AddRestroomData): ZIO[RestroomRepository, RepositoryError, UUID] =
    ZIO.serviceWithZIO[RestroomRepository](_.add(data))

  def list(
    around: Location
  ): ZIO[RestroomRepository, RepositoryError, List[Restroom]] =
    ZIO.serviceWithZIO[RestroomRepository](_.list(around))

  def byId(id: UUID): ZIO[RestroomRepository, RepositoryError, Option[Restroom]] =
    ZIO.serviceWithZIO[RestroomRepository](_.byId(id))

  def reviews(restroomId: UUID): ZIO[RestroomRepository, RepositoryError, List[Review]] =
    ZIO.serviceWithZIO[RestroomRepository](_.reviews(restroomId))
