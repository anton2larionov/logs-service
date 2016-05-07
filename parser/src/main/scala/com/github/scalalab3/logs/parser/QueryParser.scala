package com.github.scalalab3.logs.parser

import com.github.scalalab3.logs.query.Query

import scala.util.Try

trait QueryParser {
  def parse(query: String): Try[Query]
}