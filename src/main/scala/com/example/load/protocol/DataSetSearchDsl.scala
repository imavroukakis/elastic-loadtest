package com.example.load.protocol

import com.example.load.actions.HealthQueryActionBuilder
import com.example.load.service.SearchService
import io.gatling.core.session.Expression

import scala.language.implicitConversions

trait DataSetSearchDsl {
  def search(): SearchProtocolBuilder = SearchProtocolBuilder()

  def healthQuery(searchService: Expression[SearchService]): HealthQueryActionBuilder =
    HealthQueryActionBuilder(searchService)

  implicit def toSearchProtocol(protocolBuilder: SearchProtocolBuilder): SearchProtocol =
    protocolBuilder.build
}
