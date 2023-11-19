package app
package models

import zio.json.*

import java.util.UUID

final case class Restroom(
  id: UUID,
  title: String,
  description: Option[String],
  location: Location,
  distance: Double,
  @jsonField("review_average")
  reviewAverage: Float
)

object Restroom:
  implicit val decoder: JsonDecoder[Restroom] = DeriveJsonDecoder.gen[Restroom]
  implicit val encoder: JsonEncoder[Restroom] = DeriveJsonEncoder.gen[Restroom]
