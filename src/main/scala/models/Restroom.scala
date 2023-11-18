package app
package models

import zio.json.*

case class Restroom(title: String)

object Restroom:
  implicit val decoder: JsonDecoder[Restroom] = DeriveJsonDecoder.gen[Restroom]
  implicit val encoder: JsonEncoder[Restroom] = DeriveJsonEncoder.gen[Restroom]
