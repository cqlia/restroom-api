package app
package models

import zio.json.*

case class Location(latitude: Double, longitude: Double)

object Location:
  implicit val decoder: JsonDecoder[Location] = DeriveJsonDecoder.gen[Location]
  implicit val encoder: JsonEncoder[Location] = DeriveJsonEncoder.gen[Location]
