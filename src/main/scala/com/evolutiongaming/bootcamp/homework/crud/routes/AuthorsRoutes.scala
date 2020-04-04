package com.evolutiongaming.bootcamp.homework.crud.routes

import java.time.LocalDate

import cats.effect.IO
import com.evolutiongaming.bootcamp.homework.crud.dao.AuthorsDao
import io.circe.generic.auto._
import org.http4s.circe.CirceEntityCodec._
import org.http4s.dsl.io._
import org.http4s.{HttpRoutes, QueryParamDecoder}

object AuthorsRoutes {

  implicit val localDateQueryParamDecoder: QueryParamDecoder[LocalDate] =
    QueryParamDecoder[String].map(x => LocalDate.parse(x))

  object NameMatcher extends QueryParamDecoderMatcher[String]("name")
  object BirthdayMatcher extends QueryParamDecoderMatcher[LocalDate]("birthday")

  def routes(authorsDao: AuthorsDao): HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "authors" => authorsDao.getAuthors.flatMap(authors => Ok(authors))

    case GET -> Root / "authors" / UUIDVar(id) => for {
      maybeAuthor <- authorsDao.getAuthor(id)
      status <- maybeAuthor match {
        case Some(x) => Ok(x)
        case None => NotFound(s"Author $id is not found.")
      }
    } yield status

    case POST -> Root / "authors" :? NameMatcher(name) :? BirthdayMatcher(birthday) =>
      for {
        insertResult <- authorsDao.insertAuthor(name, birthday)
        status <- insertResult match {
          case 0 => InternalServerError("Cannot save author.")
          case _ => Ok("Success")
        }
      } yield status

    case PUT -> Root / "authors" / UUIDVar(id) :? NameMatcher(name) =>
      for {
        updateResult <- authorsDao.updateAuthor(id, name)
        status <- updateResult match {
          case 0 => InternalServerError("Cannot update author.")
          case _ => Ok("Success")
        }
      } yield status

    case DELETE -> Root / "authors" / UUIDVar(id) =>
      for {
        deleteResult <- authorsDao.deleteAuthor(id)
        status <- deleteResult match {
          case 0 => InternalServerError("Cannot delete author.")
          case _ => Ok("Success")
        }
      } yield status
  }

}
