package app
package models

import zio.json.*

case class Location(latitude: Double, longitude: Double):
  def inBounds: Boolean = longitude < 180
    && longitude > -180.0
    && latitude < 90
    && latitude > -90

object Location:
  implicit val decoder: JsonDecoder[Location] = DeriveJsonDecoder.gen[Location]
  implicit val encoder: JsonEncoder[Location] = DeriveJsonEncoder.gen[Location]
