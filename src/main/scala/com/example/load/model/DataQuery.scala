package com.example.load.model

import scala.collection.mutable.ListBuffer

case class DataQuery(
                      size: Int = 10000,
                      dataPoints: ListBuffer[DataPoint] = ListBuffer()
                    )
