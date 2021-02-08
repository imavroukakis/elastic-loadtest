package com.example.load.protocol

import io.gatling.core._
import io.gatling.core.config.GatlingConfiguration
import io.gatling.core.protocol.{Protocol, ProtocolKey}

class SearchProtocol extends Protocol {}

object SearchProtocol {

  val searchProtocol: ProtocolKey[SearchProtocol, SearchComponents] = new ProtocolKey[SearchProtocol, SearchComponents] {

    override def protocolClass: Class[protocol.Protocol] = classOf[SearchProtocol].asInstanceOf[Class[protocol.Protocol]]

    override def defaultProtocolValue(configuration: GatlingConfiguration): SearchProtocol =
      throw new IllegalStateException("Can't provide a default value for SearchProtocol")

    override def newComponents(coreComponents: CoreComponents): (SearchProtocol) => SearchComponents = {
      protocol => SearchComponents(protocol)
    }

  }

  def apply(): SearchProtocol = new SearchProtocol
}
