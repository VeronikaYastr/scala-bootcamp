package com.evolutiongaming.bootcamp.homework.crud.models

import java.time.Year
import java.util.UUID

final case class ShortBookInfo(authorId: UUID, title: String, year: Year, genre: String) {
  override def toString: String = s"$title ($year) with genre $genre"
}
