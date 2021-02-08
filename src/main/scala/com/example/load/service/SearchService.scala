package com.example.load.service

import org.elasticsearch.action.search.SearchRequest

trait SearchService {

  def query(searchRequest: SearchRequest, index: String): Long

  def hadHeartAttackAndStroke(): SearchRequest

}
