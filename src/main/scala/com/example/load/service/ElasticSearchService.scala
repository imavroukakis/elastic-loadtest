package com.example.load.service

import com.example.load.model._
import org.elasticsearch.action.search.SearchRequest
import org.elasticsearch.client.{RequestOptions, RestHighLevelClient}
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.search.builder.SearchSourceBuilder

class ElasticSearchService(client: RestHighLevelClient) extends SearchService {
  override def query(searchRequest: SearchRequest, index: String): Long = {
    searchRequest.indices(index)
    val response = client.search(searchRequest, RequestOptions.DEFAULT)
    response.getTook.millis
  }

  override def hadHeartAttackAndStroke(): SearchRequest = {
    val dataQuery = DataQuery()
    dataQuery.dataPoints += DiagnosedHeartAttack(queryType = And(), pointValue = "1")
    dataQuery.dataPoints += DiagnosedStroke(queryType = And(), pointValue = "1")
    toSearchRequest(dataQuery)
  }

  private def toSearchRequest(dataQuery: DataQuery) = {
    val searchRequest       = new SearchRequest()
    val searchSourceBuilder = new SearchSourceBuilder
    searchSourceBuilder.from(0)
    searchSourceBuilder.size(dataQuery.size)
    searchSourceBuilder.fetchSource(false)
    val queryBuilder =
      dataQuery.dataPoints.foldLeft(QueryBuilders.boolQuery)((queryBuilder, dataPoint) =>
        dataPoint.queryType match {
          case _: And =>
            queryBuilder
              .must(QueryBuilders.termQuery(dataPoint.esField, dataPoint.pointValue))
          case _: Or =>
            queryBuilder
              .should(QueryBuilders.termQuery(dataPoint.esField, dataPoint.pointValue))
          case _: Not =>
            queryBuilder
              .mustNot(QueryBuilders.termQuery(dataPoint.esField, dataPoint.pointValue))
        }
      )
    searchSourceBuilder.query(queryBuilder)
    searchRequest.source(searchSourceBuilder)
  }
}
