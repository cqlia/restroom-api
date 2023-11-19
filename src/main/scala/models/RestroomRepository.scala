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
    * @return
    */
  def list(
    around: Location
  ): IO[RepositoryError, List[Restroom]]

object RestroomRepository:
  def add(data: AddRestroomData): ZIO[RestroomRepository, RepositoryError, UUID] =
    ZIO.serviceWithZIO[RestroomRepository](_.add(data))

  def list(
    around: Location
  ): ZIO[RestroomRepository, RepositoryError, List[Restroom]] =
    ZIO.serviceWithZIO[RestroomRepository](_.list(around))
