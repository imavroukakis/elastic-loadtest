package com.example.load.model

case class DiagnosedStroke(esField: String = "Had_Stroke", queryType: QueryType, pointValue: String)
    extends DataPoint
