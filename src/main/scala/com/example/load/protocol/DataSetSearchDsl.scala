package com.example.load.protocol

import com.example.load.actions.HeartAttackAndStrokeActionBuilder
import com.example.load.service.SearchService
import io.gatling.core.session.Expression

import scala.language.implicitConversions

trait DataSetSearchDsl {
  def search(): SearchProtocolBuilder = SearchProtocolBuilder()

  def hadHeartAttackAndStroke(
    searchService: Expression[SearchService]
  ): HeartAttackAndStrokeActionBuilder =
    HeartAttackAndStrokeActionBuilder(searchService)

  implicit def toSearchProtocol(protocolBuilder: SearchProtocolBuilder): SearchProtocol =
    protocolBuilder.build
}
