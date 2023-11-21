package app

import app.config.Configuration.ApiConfig
import app.config.Configuration.DbConfig
import com.typesafe.config.ConfigFactory
import zio.*
import zio.config.typesafe.TypesafeConfigProvider
import zio.http.*
import zio.jdbc.*
import zio.logging.consoleLogger
import zio.logging.slf4j.bridge.Slf4jBridge

import java.io.File

import api.{BaseRoutes, RestroomRoutes}
import database.{RestroomRepositoryLive, RestroomServiceLive}
import application.*

object Main extends ZIOAppDefault:
  private val configProvider: ConfigProvider = TypesafeConfigProvider.fromTypesafeConfig(
    ConfigFactory.defaultApplication()
  )

  override val bootstrap: TaskLayer[Unit] =
    Runtime.removeDefaultLoggers >>> Runtime.setConfigProvider(configProvider)
      >>> consoleLogger() >+> Slf4jBridge.initialize

  private val app = RestroomRoutes.app @@ apiKeyMiddleware ++ BaseRoutes.app

  private val dbConfigLayer = ZLayer(configProvider.load(DbConfig.config))
  private val connectionPoolConfigLayer = ZLayer.succeed(ZConnectionPoolConfig.default)
  private val connectionPool = ZLayer
    .service[DbConfig]
    .flatMap { cfg =>
      ZConnectionPool.postgres(
        host = cfg.get.serverName,
        port = cfg.get.portNumber,
        database = cfg.get.databaseName,
        props = Map(
          "user" -> cfg.get.user,
          "password" -> cfg.get.password
        )
      )
    }
    .orDie

  private val repoLayer = RestroomRepositoryLive.layer
  private val serviceLayer = RestroomServiceLive.layer

  private val apiConfigLayer = ZLayer(configProvider.load(ApiConfig.config))
  private val serverLayer = ZLayer
    .service[ApiConfig]
    .flatMap { cfg =>
      Server.defaultWith(_.binding(cfg.get.host, cfg.get.port))
    }
    .orDie

  private val program = Server.serve(app)

  override def run: Task[Nothing] = program.provide(
    dbConfigLayer,
    apiConfigLayer,
    serverLayer,
    repoLayer,
    serviceLayer,
    connectionPool,
    connectionPoolConfigLayer
  )
