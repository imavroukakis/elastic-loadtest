package com.example.load.model

sealed abstract class QueryType

case class And() extends QueryType

case class Or() extends QueryType

case class Not() extends QueryType
