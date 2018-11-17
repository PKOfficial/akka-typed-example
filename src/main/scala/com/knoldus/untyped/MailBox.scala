package com.knoldus.untyped

import akka.actor.{ Actor, Props }

object MailBox{

  case object GetMail
  case class Mails(list: List[String])

  case class SendMail(message: String)
  case object MailSent
  case object MailBoxFull

  def apply(): Props = {
    Props(new MailBox())
  }

}

class MailBox extends Actor{

  import MailBox._

  var messages: List[String] = Nil

  override def receive: Receive = {
    case SendMail(message) if messages.size <= 100 =>
      messages = messages :+ message
      sender() ! MailSent
    case SendMail(_) => sender() ! MailBoxFull
    case GetMail => sender() ! Mails(messages)
  }

}
