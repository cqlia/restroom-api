package app
package models

import zio.json.*

import java.util.UUID

final case class Review(
  id: UUID,
  rating: Float,
  body: Option[String]
)

object Review:
  implicit val decoder: JsonDecoder[Review] = DeriveJsonDecoder.gen[Review]
  implicit val encoder: JsonEncoder[Review] = DeriveJsonEncoder.gen[Review]
