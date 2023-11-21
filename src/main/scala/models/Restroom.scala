package app
package models

import zio.json.*

import java.util.UUID

final case class Restroom(
  id: UUID,
  title: String,
  description: Option[String],
  location: Location,
  distance: Option[Double],
  @jsonField("review_average")
  reviewAverage: Float
)

object Restroom:
  implicit val decoder: JsonDecoder[Restroom] = DeriveJsonDecoder.gen[Restroom]
  implicit val encoder: JsonEncoder[Restroom] = DeriveJsonEncoder.gen[Restroom]

final case class AddRestroomData(title: String, description: Option[String], location: Location)
object AddRestroomData:
  implicit val decoder: JsonDecoder[AddRestroomData] = DeriveJsonDecoder.gen[AddRestroomData]
  implicit val encoder: JsonEncoder[AddRestroomData] = DeriveJsonEncoder.gen[AddRestroomData]
