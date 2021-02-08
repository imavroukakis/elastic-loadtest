package com.example.load.actions

import com.example.load.service.SearchService
import io.gatling.core.action.Action
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.session.Expression
import io.gatling.core.structure.ScenarioContext

import java.util.concurrent.Executors
import scala.concurrent.{ExecutionContext, ExecutionContextExecutorService}

case class HealthQueryActionBuilder(searchService: Expression[SearchService]) extends ActionBuilder {

  private val executionContext: ExecutionContextExecutorService = ExecutionContext.fromExecutorService(Executors.newCachedThreadPool())

  override def build(ctx: ScenarioContext, next: Action): Action = {
    val statsEngine = ctx.coreComponents.statsEngine
    val clock = ctx.coreComponents.clock

    HealthQueryAction(searchService, statsEngine, clock, executionContext, next)
  }

}
