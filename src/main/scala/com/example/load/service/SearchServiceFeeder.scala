package com.example.load.service

import com.example.load.CommandLineConfig
import io.gatling.core.Predef.{array2FeederBuilder, configuration}
import io.gatling.core.feeder.Feeder
import org.apache.http.HttpHost
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder
import org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback
import org.elasticsearch.client.{RestClient, RestHighLevelClient}

object SearchServiceFeeder {
  def buildSearchServiceFeeder(commandLineConfig: CommandLineConfig): Feeder[SearchService] = {
    val builder = RestClient.builder(HttpHost.create(commandLineConfig.elasticUrl()))
    val services = Array(
      Map(
        "host" -> new ElasticSearchService(
          new RestHighLevelClient(
            if (commandLineConfig.username.isSupplied && commandLineConfig.password.isSupplied) {
              import org.apache.http.auth.{AuthScope, UsernamePasswordCredentials}
              import org.apache.http.impl.client.BasicCredentialsProvider
              val credentialsProvider = new BasicCredentialsProvider
              credentialsProvider.setCredentials(
                AuthScope.ANY,
                new UsernamePasswordCredentials(
                  commandLineConfig.username(),
                  commandLineConfig.password()
                )
              )
              builder.setHttpClientConfigCallback(new HttpClientConfigCallback() {
                override def customizeHttpClient(
                  httpClientBuilder: HttpAsyncClientBuilder
                ): HttpAsyncClientBuilder = {
                  httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
                }
              })
            } else builder
          )
        )
      )
    )
      .circular()
      .asInstanceOf[Feeder[SearchService]]
    services
  }
}
