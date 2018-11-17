package com.knoldus.typed

import akka.actor.typed.{ ActorSystem, Behavior, Terminated }
import akka.actor.typed.scaladsl.Behaviors

object MailerApp {

  sealed trait Command

  def main(args: Array[String]): Unit =
    ActorSystem(MailerApp(), "mailer-main")

  def apply(): Behavior[Command] =
    Behaviors.setup { context =>
      val to     = context.spawn(MailBox(), "to")
      //      (1 to 102).map{ number =>context.spawn(Mailer( number.toString, to), s"mailer-$number")}
      val sendMail = context.spawn(Mailer("Hello World", to), "mailer")
      context.watch(sendMail)
      Behaviors.receiveSignal { case (_, Terminated(`sendMail`)) => Behaviors.stopped }
    }
}
