package app
package config

import com.typesafe.config.ConfigFactory
import zio.*
import zio.config.*
import zio.config.typesafe.TypesafeConfigProvider

import typesafe.*
import magnolia.*

object Configuration:
  final case class ApiConfig(host: String, port: Int)

  object ApiConfig:
    val layer: TaskLayer[ApiConfig] = ZLayer(
      read(
        deriveConfig[ApiConfig].nested("api") from TypesafeConfigProvider.fromTypesafeConfig(
          ConfigFactory.defaultApplication()
        )
      )
    )
