package com.evolutiongaming.bootcamp.homework.crud.models

import java.time.LocalDate
import java.util.UUID

final case class Author(id: UUID, name: String, birthday: LocalDate)
