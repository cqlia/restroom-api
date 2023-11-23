package app
package application

import zio.*
import zio.config.*
import zio.config.magnolia.*

object Configuration:
  final case class ApiConfig(host: String, port: Int, apiKey: String)

  object ApiConfig:
    val config: Config[ApiConfig] = deriveConfig[ApiConfig].nested("api")

  final case class DbConfig(
    user: String,
    databaseName: String,
    password: String,
    portNumber: Int,
    serverName: String
  )

  object DbConfig:
    val config: Config[DbConfig] = deriveConfig[DbConfig]
      .nested("db")
