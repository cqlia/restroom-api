package app
package models

import zio.jdbc.*
import zio.json.*
import zio.schema.*

import java.time.ZonedDateTime
import java.util.UUID

final case class Review(
  id: UUID,
  rating: Float,
  body: Option[String],
  @jsonField("created_at")
  createdAt: ZonedDateTime
)

object Review:
  implicit val decoder: JsonDecoder[Review] = DeriveJsonDecoder.gen[Review]
  implicit val encoder: JsonEncoder[Review] = DeriveJsonEncoder.gen[Review]

  implicit val schema: Schema[Review] = DeriveSchema.gen[Review]
  implicit val jdbcDecoder: JdbcDecoder[Review] = JdbcDecoder.fromSchema
  implicit val jdbcEncoder: JdbcEncoder[Review] = JdbcEncoder.fromSchema

final case class AddReviewData(
  rating: Float,
  body: Option[String]
)

object AddReviewData:
  implicit val decoder: JsonDecoder[AddReviewData] = DeriveJsonDecoder.gen[AddReviewData]
  implicit val encoder: JsonEncoder[AddReviewData] = DeriveJsonEncoder.gen[AddReviewData]
