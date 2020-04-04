package com.evolutiongaming.bootcamp.homework.crud.dao

import java.time.Year
import java.util.UUID

import cats.effect.IO._
import cats.effect._
import com.evolutiongaming.bootcamp.homework.crud.models.{Book, ShortBookInfo}
import doobie._
import doobie.implicits._
import doobie.implicits.javatime._
import doobie.h2._
import doobie.util.transactor.Transactor

trait BooksDao {
  def getBooks: IO[List[Book]]
  def getBook(id: UUID): IO[Option[Book]]
  def insertBook(book: ShortBookInfo): IO[Int]
  def updateBook(id: UUID, title: String): IO[Int]
  def deleteBook(id: UUID): IO[Int]
}

class BooksDaoImpl(xa: Transactor[IO]) extends BooksDao {
  implicit val uuidMeta: Meta[UUID] = Meta[String].timap(UUID.fromString)(_.toString)
  implicit val yearMeta: Meta[Year] = Meta[Int].timap(Year.of)(_.getValue)

  override def getBooks: IO[List[Book]] = {
    val queryBooks = Fragment.const(DaoCommon.fetchBooksCommonSql)
    queryBooks.queryWithLogHandler[Book](LogHandler.jdkLogHandler).to[List].transact(xa)
  }

  override def getBook(id: UUID): IO[Option[Book]] = {
    val queryBook = sql"SELECT b.id, a.id, a.name, a.birthday, b.title, b.year, b.genre FROM books b INNER JOIN authors a ON b.author = a.id WHERE b.id=$id"
    queryBook.query[Book].option.transact(xa)
  }

  override def insertBook(book: ShortBookInfo): IO[Int] = {
    val id = UUID.randomUUID()
    val insertBookQuery = sql"INSERT INTO books (id, author, title, year, genre) VALUES ($id, ${book.authorId}, ${book.title}, ${book.year}, ${book.genre} );"
    insertBookQuery.update.run.transact(xa)
  }

 override def updateBook(id: UUID, title: String): IO[Int] = {
    val updateBookQuery = sql"UPDATE books SET title = $title WHERE id = $id;"
    updateBookQuery.update.run.transact(xa)
  }

  override def deleteBook(id: UUID): IO[Int] = {
    val deleteBookQuery = sql"DELETE FROM books WHERE id = $id;"
    deleteBookQuery.update.run.transact(xa)
  }
}

