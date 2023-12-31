package app
package application

import zio.*
import zio.http.Middleware.*
import zio.http.*

import Configuration.ApiConfig

private val API_KEY_HEADER = "X-Api-Key"

object Middleware:
  def apiKeyMiddleware: HandlerAspect[ApiConfig, Unit] = customAuthZIO(r =>
    for {
      apiKey <- ZIO.serviceWith[ApiConfig](_.apiKey)
      // is this the correct way to read a custom header?
    } yield r.header(Header.Custom(API_KEY_HEADER, "").headerType) match
      case Some(value) => value.renderedValue == apiKey
      case _           => false
  )

  final case class DeviceKeyContext(key: String)
  def deviceKeyMiddleware: HandlerAspect[Any, DeviceKeyContext] =
    HandlerAspect.customAuthProviding[DeviceKeyContext] { r =>
      r.headers.get(Header.Authorization).flatMap {
        case Header.Authorization.Bearer(token) => Some(DeviceKeyContext(token))
        case _                                  => None
      }
    }
