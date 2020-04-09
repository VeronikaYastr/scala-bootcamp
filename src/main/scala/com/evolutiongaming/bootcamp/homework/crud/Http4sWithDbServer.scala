package com.evolutiongaming.bootcamp.homework.crud

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import com.evolutiongaming.bootcamp.homework.crud.config.{Config, ServerConfig}
import com.evolutiongaming.bootcamp.homework.crud.dao.{AuthorsDaoImpl, BooksDaoImpl, DaoInit}
import com.evolutiongaming.bootcamp.homework.crud.routes.{AuthorsRoutes, BooksRoutes}
import doobie.util.transactor.Transactor
import fs2.Stream
import io.circe.generic.auto._
import org.http4s._
import org.http4s.circe.CirceEntityCodec._
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder

object Http4sWithDbServer extends IOApp {

  def makeRoutes(xa: Transactor[IO]): HttpRoutes[IO] =
    BooksRoutes.routes(new BooksDaoImpl(xa)) <+> AuthorsRoutes.routes(new AuthorsDaoImpl(xa))

  def serveStream(transactor: Transactor[IO], serverConfig: ServerConfig): Stream[IO, ExitCode] = {
    BlazeServerBuilder[IO]
      .bindHttp(serverConfig.port, serverConfig.host)
      .withHttpApp(makeRoutes(transactor).orNotFound)
      .serve
  }

  override def run(args: List[String]): IO[ExitCode] = {

    def transactor(config: Config): Transactor[IO] = {
      val tr = DaoInit.transactor(config.dbConfig)
      DaoInit.initTables(tr).unsafeRunSync()
      tr
    }

    val stream = for {
      config <- Stream.eval(Config.load())
      tr = transactor(config)
      serv <- serveStream(tr, config.serverConfig)
    } yield serv

    stream.compile.drain.as(ExitCode.Success)
  }
}
