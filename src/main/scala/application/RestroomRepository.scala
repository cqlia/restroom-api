package app
package application

import zio.*

import java.util.UUID

import models.*

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

  /** Fetches a restroom from its case sensitive title.
    *
    * @param title
    *   Reference title for restroom
    */
  def byTitle(title: String): IO[RepositoryError, Option[Restroom]]

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

  /** Adds a new review for a restroom in the repository.
    * @param restroomId
    *   Reference UUID for restroom
    * @param data
    *   Information describing the review to be added
    * @param authorId
    *   Device key of the review author.
    * @return
    *   UUID of the created review
    */
  def addReview(restroomId: UUID, data: AddReviewData, authorId: String): IO[RepositoryError, UUID]

  /** Fetches a review by its author and associated restroom.
    * @param restroomId
    *   Reference UUID for restroom
    * @param authorId
    *   Device key of review author
    */
  def reviewByAuthor(restroomId: UUID, authorId: String): IO[RepositoryError, Option[Review]]

object RestroomRepository:
  def add(data: AddRestroomData): ZIO[RestroomRepository, RepositoryError, UUID] =
    ZIO.serviceWithZIO[RestroomRepository](_.add(data))

  def list(
    around: Location
  ): ZIO[RestroomRepository, RepositoryError, List[Restroom]] =
    ZIO.serviceWithZIO[RestroomRepository](_.list(around))

  def byId(id: UUID): ZIO[RestroomRepository, RepositoryError, Option[Restroom]] =
    ZIO.serviceWithZIO[RestroomRepository](_.byId(id))

  def byTitle(title: String): ZIO[RestroomRepository, RepositoryError, Option[Restroom]] =
    ZIO.serviceWithZIO[RestroomRepository](_.byTitle(title))

  def reviews(restroomId: UUID): ZIO[RestroomRepository, RepositoryError, List[Review]] =
    ZIO.serviceWithZIO[RestroomRepository](_.reviews(restroomId))

  def addReview(
    restroomId: UUID,
    data: AddReviewData,
    authorId: String
  ): ZIO[RestroomRepository, RepositoryError, UUID] =
    ZIO.serviceWithZIO[RestroomRepository](_.addReview(restroomId, data, authorId))

  def reviewByAuthor(
    restroomId: UUID,
    authorId: String
  ): ZIO[RestroomRepository, RepositoryError, Option[Review]] =
    ZIO.serviceWithZIO[RestroomRepository](_.reviewByAuthor(restroomId, authorId))
