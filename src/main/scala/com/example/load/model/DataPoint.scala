package com.example.load.model

trait DataPoint {
  def esField: String

  def queryType: QueryType

  def pointValue: String
}
