package com.github.scalalab3.logs.tests

import org.specs2.matcher.{JsonType, Matcher, JsonMatchers}
import org.specs2.mutable.Specification

trait LogJsonSpecification extends Specification with JsonMatchers {

  def aLogWith(name: Matcher[JsonType],
               id: Matcher[JsonType],
               dateTime: Matcher[JsonType]): Matcher[String] =
    /("name").andHave(name) and /("id").andHave(id) and /("dateTime").andHave(dateTime)

  def haveLogs(logsMatcher: Matcher[String]*): Matcher[String] = have(allOf(logsMatcher:_*))

}
