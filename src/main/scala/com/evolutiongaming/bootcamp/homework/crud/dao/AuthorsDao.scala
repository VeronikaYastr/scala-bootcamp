package com.evolutiongaming.bootcamp.homework.crud.dao

import java.time.{LocalDate, Year}
import java.util.UUID

import cats.effect.IO._
import cats.effect._
import com.evolutiongaming.bootcamp.homework.crud.models.Author
import doobie._
import doobie.implicits._
import doobie.util.transactor.Transactor
import doobie.implicits.javatime._
import doobie.h2._

trait AuthorsDao {
  def getAuthors: IO[List[Author]]
  def insertAuthor(name: String, birthday: LocalDate): UUID
  def updateAuthor(id: UUID, name: String): IO[Int]
  def getAuthor(id: UUID): IO[Option[Author]]
  def deleteAuthor(id: UUID): IO[Int]
}

class AuthorsDaoImpl(xa: Transactor[IO]) extends AuthorsDao {
  implicit val uuidMeta: Meta[UUID] = Meta[String].timap(UUID.fromString)(_.toString)
  implicit val yearMeta: Meta[Year] = Meta[Int].timap(Year.of)(_.getValue)

  override def getAuthors: IO[List[Author]] = {
    val queryAuthors = sql"SELECT id, name, birthday FROM authors;"
    queryAuthors.queryWithLogHandler[Author](LogHandler.jdkLogHandler).to[List].transact(xa)
  }

  override def getAuthor(id: UUID): IO[Option[Author]] = {
    val queryAuthor = sql"SELECT id, name, birthday FROM authors WHERE id=$id;"
    queryAuthor.query[Author].option.transact(xa)
  }

  override def insertAuthor(name: String, birthday: LocalDate): UUID = {
    val id = UUID.randomUUID()
    val insertAuthorQuery = sql"INSERT INTO authors (id, name, birthday) VALUES ($id, $name ,$birthday );"
    insertAuthorQuery.update.run.transact(xa)
    id
  }

 override def updateAuthor(id: UUID, name: String): IO[Int] = {
    val updateAuthorQuery = sql"UPDATE authors SET name = $name WHERE id = $id;"
    updateAuthorQuery.update.run.transact(xa)
  }

  override def deleteAuthor(id: UUID): IO[Int] = {
    val deleteAuthorQuery = sql"DELETE FROM authors WHERE id = $id;"
    deleteAuthorQuery.update.run.transact(xa)
  }
}

