package app

import app.config.Configuration.ApiConfig
import com.typesafe.config.ConfigFactory
import io.getquill.*
import io.getquill.jdbczio.Quill
import zio.*
import zio.config.typesafe.TypesafeConfigProvider
import zio.http.*
import zio.logging.consoleLogger
import zio.logging.slf4j.bridge.Slf4jBridge

import java.io.File

import api.{BaseRoutes, RestroomRoutes}
import database.{RestroomRepositoryLive, RestroomServiceLive}

object Main extends ZIOAppDefault:
  private val configProvider: ConfigProvider = TypesafeConfigProvider.fromTypesafeConfig(
    ConfigFactory.defaultApplication()
  )

  override val bootstrap: TaskLayer[Unit] =
    Runtime.removeDefaultLoggers >>> Runtime.setConfigProvider(configProvider)
      >>> consoleLogger() >+> Slf4jBridge.initialize

  private val app = RestroomRoutes.app ++ BaseRoutes.app

  private val dataSourceLayer = Quill.DataSource.fromPrefix("db")
  private val postgresLayer = Quill.Postgres.fromNamingStrategy(Literal)
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
    apiConfigLayer,
    serverLayer,
    repoLayer,
    serviceLayer,
    postgresLayer,
    dataSourceLayer
  )
