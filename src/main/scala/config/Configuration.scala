package app
package config

import zio.*
import zio.config.*

import magnolia.*

object Configuration:
  final case class ApiConfig(host: String, port: Int, apiKey: String)

  object ApiConfig:
    val config: Config[ApiConfig] = deriveConfig[ApiConfig].nested("api")
