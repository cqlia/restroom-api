package app
package models

import zio.jdbc.*
import zio.json.*
import zio.schema.*

import java.util.UUID

final case class Restroom(
  id: UUID,
  title: String,
  description: Option[String],
  location: Location,
  distance: Option[Double],
  @jsonField("review_average")
  reviewAverage: Float,
  @jsonField("review_count")
  reviewCount: Int
)

object Restroom:
  implicit val decoder: JsonDecoder[Restroom] = DeriveJsonDecoder.gen[Restroom]
  implicit val encoder: JsonEncoder[Restroom] = DeriveJsonEncoder.gen[Restroom]

  implicit val schema: Schema[Restroom] = DeriveSchema.gen[Restroom]
  implicit val jdbcDecoder: JdbcDecoder[Restroom] = JdbcDecoder.fromSchema
  implicit val jdbcEncoder: JdbcEncoder[Restroom] = JdbcEncoder.fromSchema

final case class AddRestroomData(title: String, description: Option[String], location: Location)
object AddRestroomData:
  implicit val decoder: JsonDecoder[AddRestroomData] = DeriveJsonDecoder.gen[AddRestroomData]
  implicit val encoder: JsonEncoder[AddRestroomData] = DeriveJsonEncoder.gen[AddRestroomData]
