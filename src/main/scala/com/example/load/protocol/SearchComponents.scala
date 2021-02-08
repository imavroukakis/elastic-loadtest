package com.example.load.protocol

import io.gatling.core.protocol.ProtocolComponents
import io.gatling.core.session.Session

case class SearchComponents(protocol: SearchProtocol) extends ProtocolComponents {

  override def onStart: Session => Session = Session.Identity

  override def onExit: Session => Unit = ProtocolComponents.NoopOnExit

}
