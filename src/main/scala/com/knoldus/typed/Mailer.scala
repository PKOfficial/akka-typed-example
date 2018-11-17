package com.knoldus.typed

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ ActorRef, Behavior }

object Mailer {

  import MailBox._

  sealed trait Command

  private final case object MailboxFullHandler extends Command

  private final case object MailSentHandler extends Command

  def apply(message: String, to: ActorRef[SendMail]): Behavior[Command] = {
    Behaviors.setup { context =>
      to ! SendMail(message, context.messageAdapter({
        case MailBoxFull => MailboxFullHandler
        case MailSent => MailSentHandler
      }))

      Behaviors.receiveMessagePartial {
        case MailboxFullHandler =>
          println(s"Unable to send { $message } because of mail box is full!")
          Behaviors.stopped
        case MailSentHandler =>
          println(s"Message { $message } sent !")
          Behaviors.stopped
      }

    }
  }

}
