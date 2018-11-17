package com.knoldus.untyped

import akka.actor.{ Actor, ActorRef, ActorSystem, Props, Terminated }

object Mailer {

  def apply(message: String, to: ActorRef): Props = {
    Props(new Mailer(message, to))
  }

}

class Mailer(message: String, to: ActorRef) extends Actor {

  to ! MailBox.SendMail(message)

  override def receive: Receive = {
    case MailBox.MailSent =>
      println(s"Message { $message } sent !")
      context.stop(self)
    case MailBox.MailBoxFull =>
      println(s"Unable to send { $message } because of mail box is full!")
      context.stop(self)
  }

}

object MailerApp {

  final class Main extends Actor {
    private val to = context.actorOf(MailBox())
    private val mailer = context.actorOf(Mailer("Hello World", to))
    context.watch(mailer)

    override def receive: Receive = {
      case Terminated => context.system.terminate()
    }
  }

  def main(args: Array[String]): Unit = {
    val system = ActorSystem("mailer-app")
    system.actorOf(Props(new Main), "main")
  }

}