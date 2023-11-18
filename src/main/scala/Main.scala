package app

import zio.*
import zio.http.*
import zio.logging.consoleLogger

import java.io.File

import api.RestroomRoutes
import config.Configuration.ApiConfig

object Main extends ZIOAppDefault:
  override val bootstrap: TaskLayer[Unit] = Runtime.removeDefaultLoggers >>> consoleLogger()

  private val app = RestroomRoutes.app

  private val serverLayer =
    ZLayer
      .service[ApiConfig]
      .flatMap { cfg =>
        Server.defaultWith(_.binding(cfg.get.host, cfg.get.port))
      }
      .orDie

  private val program = Server.serve(app)

  override def run: Task[Nothing] = program.provide(ApiConfig.layer, serverLayer)
