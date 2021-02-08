package com.example.load

import com.example.load.protocol.Predef._
import com.example.load.service.{SearchService, SearchServiceFeeder}
import com.typesafe.scalalogging.StrictLogging
import io.gatling.core.Predef._
import io.gatling.core.feeder.Feeder
import io.gatling.core.scenario.Simulation
import io.gatling.core.structure.PopulationBuilder

import scala.collection.mutable.ListBuffer
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.{Failure, Success, Try}

class ElasticSearchSimulation extends Simulation with StrictLogging {

  private def searchServiceFeeder: Feeder[SearchService] = {
    SearchServiceFeeder.buildSearchServiceFeeder(LoadTestRunner.config)
  }

  private val duration: FiniteDuration = LoadTestRunner.config.testDuration.toOption match {
    case Some(duration) =>
      Try(Duration(duration.replace('_', ' '))) match {
        case Success(duration) => duration.asInstanceOf[FiniteDuration]
        case Failure(exception) => throw exception
      }
    case None => {
      logger.warn("no duration provided, defaulting to 60 seconds")
      60 seconds
    }
  }

  private val usersPerSecond: Double = LoadTestRunner.config.usersPerSecond().toDouble

  private var scenarios = new ListBuffer[PopulationBuilder]()

  if (LoadTestRunner.config.heartAttackAndStrokeSearch()) {
    logger.info("Testing ElasticSearch with {} users", usersPerSecond)
    scenarios +=
      scenario("BRFSS Dataset Search")
        .feed(searchServiceFeeder)
        .exec(healthQuery("${host}"))
        .inject(
          rampUsersPerSec(1) to usersPerSecond during (1 minute),
          constantUsersPerSec(usersPerSecond) during duration
        )
  }

  setUp(scenarios.toList).protocols(search())
}
