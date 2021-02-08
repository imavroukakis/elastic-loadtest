package com.example.load

import io.gatling.app.Gatling
import io.gatling.core.config.GatlingPropertiesBuilder

import java.text.SimpleDateFormat
import java.util.Calendar
import scala.language.postfixOps
import scala.sys.exit

object LoadTestRunner {

  var config: CommandLineConfig = _

  def main(args: Array[String]) {
    config = new CommandLineConfig(args)
    val propertiesBuilder = new GatlingPropertiesBuilder
    propertiesBuilder.runDescription("ElasticSearch Load Test")
    if (config.reportOnly.isDefined) {
      propertiesBuilder.reportsOnly(config.reportOnly())
    } else {
      val simClass = classOf[ElasticSearchSimulation].getName
      propertiesBuilder.simulationClass(simClass)
      val now = Calendar.getInstance().getTime
      val dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH_mm_ss")
      propertiesBuilder.resultsDirectory(s"results/${dateFormat.format(now)}")
    }
    Gatling.fromMap(propertiesBuilder.build)
    exit
  }
}
