package app
package database

import io.getquill.*
import io.getquill.jdbczio.Quill
import zio.*

import java.sql.SQLException
import java.util.UUID

import models.*

case class DbRestroom(
  id: String,
  title: String,
  description: Option[String],
  reviewAverage: Float,
  longitude: Double,
  latitude: Double,
  distance: Double
)

object DbRestroom:
  def asRestroom(x: DbRestroom): Restroom = Restroom(
    id = UUID.fromString(x.id),
    title = x.title,
    description = x.description,
    location = Location(
      latitude = x.latitude,
      longitude = x.longitude
    ),
    distance = x.distance,
    reviewAverage = x.reviewAverage
  )

class RestroomRepositoryLive(quill: Quill.Postgres[Literal]) extends RestroomRepository:
  import quill.*

  // given the usage of PostGIS, Quill cannot be trusted to generate correct queries
  // remember that coordinates are in (longitude, latitude) order.

  // INSERT INTO restrooms (title, location) VALUES ('test a', ST_MakePoint(long, lat));
  override def add(data: AddRestroomData): IO[RepositoryError, UUID] = ZIO.fail(null)

  override def list(around: Location): IO[RepositoryError, List[Restroom]] =
    val effect: IO[SQLException, List[DbRestroom]] = run {
      quote {
        //
        sql"""SELECT restrooms.id, title, description,
                    COALESCE(AVG(reviews.rating), 0) AS reviewAverage,
                    ST_X(location) as longitude, ST_Y(location) as latitude,
                    ST_Distance(
                     ST_Transform(location, 3857),
                     ST_Transform(
                       ST_SetSRID(ST_MakePoint(
                         ${lift(around.longitude)},
                         ${lift(around.latitude)}
                       ),
                       4326),
                     3857)
                    ) * cosd(ST_Y(location)) * 0.0006213712 AS distance
             FROM restrooms LEFT JOIN reviews ON restrooms.id = reviews.restroom_id
             GROUP BY restrooms.id ORDER BY distance""".as[Query[DbRestroom]]
      }
    }

    effect
      .refineOrDie { case e: SQLException =>
        RepositoryError(e)
      }
      .map(_.map(DbRestroom.asRestroom))

object RestroomRepositoryLive:
  val layer: URLayer[Quill.Postgres[Literal], RestroomRepository] = ZLayer {
    for {
      quill <- ZIO.service[Quill.Postgres[Literal]]
    } yield RestroomRepositoryLive(quill)
  }
