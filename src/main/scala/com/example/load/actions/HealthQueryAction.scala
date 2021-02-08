package com.example.load.actions

import com.example.load.LoadTestRunner
import com.example.load.service.SearchService
import io.gatling.commons.stats.{KO, OK}
import io.gatling.commons.util.Clock
import io.gatling.commons.validation.{Failure, Success}
import io.gatling.core.action.{Action, ChainableAction}
import io.gatling.core.session.{Expression, Session}
import io.gatling.core.stats.StatsEngine
import io.gatling.core.util.NameGen
import org.elasticsearch.action.search.SearchRequest

import scala.concurrent.{ExecutionContextExecutorService, Future}

case class HealthQueryAction(
                              searchService: Expression[SearchService],
                              statsEngine: StatsEngine,
                              clock: Clock,
                              implicit val executionContext: ExecutionContextExecutorService,
                              next: Action
                            ) extends ChainableAction
  with NameGen {

  override def execute(session: Session): Unit = {

    val searchSvc: SearchService = searchService(session) match {
      case Success(searchService: SearchService) => searchService
      case Failure(error) => throw new IllegalArgumentException(error)
    }
    val searchRequest: SearchRequest = searchSvc.hadHeartAttackAndStroke()
    val start: Long = clock.nowMillis
    val f = Future {
      val esResponseTime: Long = searchSvc.query(searchRequest, LoadTestRunner.config.indexName())
      val clockEnd: Long = clock.nowMillis
      val end: Long =
        if (LoadTestRunner.config.useEsResponseTime()) start + esResponseTime else clockEnd
      statsEngine.logResponse(
        session.scenario,
        session.groups,
        name,
        start,
        end,
        OK,
        None,
        Option.empty
      )
    }
    f.onComplete {
      case scala.util.Success(_) => next ! session
      case scala.util.Failure(e) => {
        statsEngine.logResponse(
          session.scenario,
          session.groups,
          name,
          start,
          clock.nowMillis,
          KO,
          None,
          Option.apply(e.getMessage)
        )
        logger.warn("Action failure", e)
        next ! session.markAsFailed
      }
    }
  }

  override def name: String = "Had heart attack and stroke"
}
