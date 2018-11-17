package com.knoldus.typed

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ ActorRef, Behavior }

object MailBox{

  sealed trait Command

  case class GetMail(replyTo: ActorRef[Mails]) extends Command
  case class Mails(list: List[String])

  case class SendMail(message: String, replyTo: ActorRef[MailSentReply]) extends Command
  sealed trait MailSentReply
  case object MailSent extends MailSentReply
  case object MailBoxFull extends MailSentReply

  def apply(mails: List[String] = Nil): Behavior[Command] = {
    Behaviors.receiveMessage {
      case GetMail(replyTo) =>
        replyTo ! Mails(mails)
        println(mails)
        Behaviors.same
      case SendMail(mail, replyTo) if mails.size <= 100 =>
        replyTo ! MailSent
        MailBox(mails :+ mail)
      case SendMail(_, replyTo) =>
        replyTo ! MailBoxFull
        Behaviors.same
    }
  }

}
