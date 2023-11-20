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
