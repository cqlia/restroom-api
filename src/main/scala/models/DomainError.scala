package app
package models

sealed trait DomainError(message: String) extends Throwable

/** represents an error originating from the repository layer
  * @param cause
  *   repository error that began the chain
  */
final case class RepositoryError(cause: Throwable) extends DomainError(message = cause.getMessage)
final case class RequestError(message: String) extends DomainError(message = message)
final case class NotFoundError() extends DomainError(message = "item not found")

object RequestError:
  val InvalidLocation: RequestError = RequestError("Location out of bounds")
  val TitleLength: RequestError = RequestError("Title is too long")
  val InvalidRating: RequestError = RequestError("Rating is not valid")
  val DuplicateReview: RequestError = RequestError("User cannot create more than one review")
  val InvalidBody: RequestError = RequestError("Body could not be read")
  val MissingLocation: RequestError = RequestError("Location parameter is missing")
