package com.evolutiongaming.bootcamp.homework.crud.routes

import cats.effect.IO
import com.evolutiongaming.bootcamp.homework.crud.dao.BooksDao
import com.evolutiongaming.bootcamp.homework.crud.models.ShortBookInfo
import io.circe.generic.auto._
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec._
import org.http4s.dsl.io._

object BooksRoutes {

  object TitleMatcher extends QueryParamDecoderMatcher[String]("title")

  def routes(booksDao: BooksDao): HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "books" => booksDao.getBooks.flatMap(b => Ok(b))

    case GET -> Root / "books" / UUIDVar(id) => for {
      maybeBook <- booksDao.getBook(id)
      status <- maybeBook match {
        case Some(x) => Ok(x)
        case None => NotFound(s"Book $id is not found.")
      }
    } yield status

    case req@POST -> Root / "books" =>
      req.as[ShortBookInfo].flatMap(book => Ok(booksDao.insertBook(book)))

    case PUT -> Root / "books" / UUIDVar(id) :? TitleMatcher(title) =>
      for {
        updateResult <- booksDao.updateBook(id, title)
        status <- updateResult match {
          case 0 => InternalServerError("Cannot update book.")
          case _ => Ok("Success")
        }
      } yield status

    case DELETE -> Root / "books" / UUIDVar(id) =>
      for {
        deleteResult <- booksDao.deleteBook(id)
        status <- deleteResult match {
          case 0 => InternalServerError("Cannot delete book.")
          case _ => Ok("Success")
        }
      } yield status
  }

}
