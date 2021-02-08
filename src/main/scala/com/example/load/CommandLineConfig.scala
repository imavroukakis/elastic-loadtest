package com.example.load

import org.rogach.scallop.{ScallopConf, ScallopOption}

import scala.language.postfixOps

class CommandLineConfig(arguments: Seq[String]) extends ScallopConf(arguments) {
  footer(
    "\n\nexample: loadtest --host localhost:9002 --users-per-second=4 --test-duration=5_minutes --heart-attack-and-stroke-search"
  )
  val usersPerSecond: ScallopOption[Int] =
    opt[Int](
      default = Some(5),
      noshort = true,
      descr = "the amount of users per second created, default: 5"
    )
  val elasticUrl: ScallopOption[String] =
    opt[String](noshort = true, descr = "The Elastic Search URL")
  val indexName: ScallopOption[String] =
    opt[String](noshort = true, descr = "The Elastic Search index to search")
  val testDuration: ScallopOption[String] =
    opt[String](
      default = Some("60_seconds"),
      noshort = true,
      descr =
        "the test duration in the following format: num_duration, e.g. 5_minutes. default: 60_seconds"
    )
  val username: ScallopOption[String] = opt[String](noshort = true, required = false)
  val password: ScallopOption[String] = opt[String](noshort = true, required = false)
  val reportOnly: ScallopOption[String] =
    opt[String](noshort = true, descr = "create a report from the supplied directory")
  val useEsResponseTime: ScallopOption[Boolean] =
    opt[Boolean](
      default = Some(false),
      noshort = true,
      descr = "use the elastic search response time in reports"
    )
  val heartAttackAndStrokeSearch: ScallopOption[Boolean] =
    opt[Boolean](noshort = true, descr = "Test Search for both Heart Attack and Stroke")
  mainOptions =
    Seq(elasticUrl, usersPerSecond, username, password, testDuration, heartAttackAndStrokeSearch)
  dependsOnAny(elasticUrl, List(heartAttackAndStrokeSearch))
  requireOne(reportOnly, elasticUrl)
  verify()
}
