package com.example.load.model

case class DiagnosedHeartAttack(
                                 esField: String = "Had_Heart_Attack",
                                 queryType: QueryType,
                                 pointValue: String
                               ) extends DataPoint
