package app
package database

import zio.*
import zio.jdbc.*

import java.sql.SQLException
import java.util.UUID

import models.*

// schema methods seem a little broken currently, so manual it is
type UnpackedRestroom = (UUID, String, Option[String], Float, Double, Double, Option[Double])
private def tupleToRestroom(t: UnpackedRestroom): Restroom = Restroom(
  id = t._1,
  title = t._2,
  description = t._3,
  reviewAverage = t._4,
  location = Location(
    longitude = t._5,
    latitude = t._6
  ),
  distance = t._7
)

type UnpackedReview = (UUID, Float, Option[String])
private def tupleToReview(r: UnpackedReview): Review = Review(
  id = r._1,
  rating = r._2,
  body = r._3
)

class RestroomRepositoryLive(connectionPool: ZConnectionPool) extends RestroomRepository:
  import connectionPool.*

  // remember that coordinates are in (longitude, latitude) order.

  override def add(data: AddRestroomData): IO[RepositoryError, UUID] =
    val effect = transaction {
      sql"""INSERT INTO restrooms (title, description, location) VALUES (
           ${data.title}, ${data.description},
           ST_MakePoint(${data.location.longitude}, ${data.location.latitude})
           ) RETURNING id;""".insertReturning[UUID]
    }

    effect
      .refineOrDie { case e: Throwable =>
        RepositoryError(e)
      }
      // this should only ever return one value, if it's missing then something's broken
      .map(_.updatedKeys.head)

  override def list(around: Location): IO[RepositoryError, List[Restroom]] =
    val effect = transaction {
      sql"""SELECT restrooms.id, title, description,
                  COALESCE(AVG(reviews.rating), 0) AS reviewAverage,
                  ST_X(location) as longitude, ST_Y(location) as latitude,
                  ST_Distance(
                   ST_Transform(location, 3857),
                   ST_Transform(
                     ST_SetSRID(ST_MakePoint(
                       ${around.longitude},
                       ${around.latitude}
                     ),
                     4326),
                   3857)
                  ) * cosd(ST_Y(location)) * 0.0006213712 AS distance
           FROM restrooms LEFT JOIN reviews ON restrooms.id = reviews.restroom_id
           GROUP BY restrooms.id ORDER BY distance"""
        .query[UnpackedRestroom]
        .selectAll
    }

    effect
      .refineOrDie { case e: Throwable =>
        RepositoryError(e)
      }
      .map(_.toList.map(tupleToRestroom))

  override def reviews(restroomId: UUID): IO[RepositoryError, List[Review]] =
    val effect = transaction {
      sql"SELECT id, rating, body FROM reviews WHERE restroom_id = $restroomId ORDER BY created_at DESC"
        .query[UnpackedReview]
        .selectAll
    }

    effect
      .refineOrDie { case e: Throwable =>
        RepositoryError(e)
      }
      .map(_.toList.map(tupleToReview))

  override def byId(id: UUID): IO[RepositoryError, Option[Restroom]] =
    val effect = transaction {
      // this is a one-off function, but having 0 as distance is not the most ideal
      sql"""SELECT restrooms.id, title, description,
          COALESCE(AVG(reviews.rating), 0) AS reviewAverage,
          ST_X(location) as longitude, ST_Y(location) as latitude, NULL AS distance
          FROM restrooms LEFT JOIN reviews ON restrooms.id = reviews.restroom_id
          WHERE restrooms.id = $id GROUP BY restrooms.id"""
        .query[UnpackedRestroom]
        .selectOne
    }

    effect
      .refineOrDie { case e: Throwable =>
        RepositoryError(e)
      }
      .map(_.map(tupleToRestroom))

  override def byTitle(title: String): IO[RepositoryError, Option[Restroom]] =
    val effect = transaction {
      // this is a one-off function, but having 0 as distance is not the most ideal
      sql"""SELECT restrooms.id, title, description,
          COALESCE(AVG(reviews.rating), 0) AS reviewAverage,
          ST_X(location) as longitude, ST_Y(location) as latitude, NULL AS distance
          FROM restrooms LEFT JOIN reviews ON restrooms.id = reviews.restroom_id
          WHERE restrooms.title = $title GROUP BY restrooms.id"""
        .query[UnpackedRestroom]
        .selectOne
    }

    effect
      .refineOrDie { case e: Throwable =>
        RepositoryError(e)
      }
      .map(_.map(tupleToRestroom))

  override def addReview(
    restroomId: UUID,
    data: AddReviewData,
    authorId: String
  ): IO[RepositoryError, UUID] =
    val effect = transaction {
      sql"""INSERT INTO reviews (restroom_id, rating, body, created_by) VALUES (
           $restroomId, ${data.rating}, ${data.body}, $authorId
         ) RETURNING id;""".insertReturning[UUID]
    }

    effect
      .refineOrDie { case e: Throwable =>
        RepositoryError(e)
      }
      .map(_.updatedKeys.head)

  override def reviewByAuthor(
    restroomId: UUID,
    authorId: String
  ): IO[RepositoryError, Option[Review]] =
    val effect = transaction {
      sql"""SELECT id, rating, body FROM reviews WHERE restroom_id = $restroomId AND created_by = $authorId;"""
        .query[UnpackedReview]
        .selectOne
    }

    effect
      .refineOrDie { case e: Throwable =>
        RepositoryError(e)
      }
      .map(_.map(tupleToReview))

object RestroomRepositoryLive:
  val layer: URLayer[ZConnectionPool, RestroomRepository] = ZLayer {
    for {
      connectionPool <- ZIO.service[ZConnectionPool]
    } yield RestroomRepositoryLive(connectionPool)
  }
