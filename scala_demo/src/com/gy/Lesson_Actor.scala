package com.gy

import scala.actors.Actor

case class Meg(actor: Actor, message: String);

class MyActor1 extends Actor {
  override def act(): Unit = {
    while (true) {
      receive {
        case s: String => println(s);
        case msg: Meg => {
          println(msg.message)
          msg.actor ! "收到了啊，激动个毛线啊！！！"
        }
        case _ => println("defualt ...");
      }
    }
  }
}

class MyActor2(actor: Actor) extends Actor {
  actor ! new Meg(this, "通讯搞起来！！！搞起来！！！ 收到请回答!");

  override def act(): Unit = {
    while (true) {
      receive {
        case s: String => {
          println(s)
          actor ! new Meg(this, "你这什么态度啊，不想聊了。 挂了吧！")
        }
        case _ => println("defualt ...");
      }
    }
  }
}

object Lesson_Actor {

  def main(args: Array[String]): Unit = {
    val actor1 = new MyActor1()
    val actor2 = new MyActor2(actor1)
    actor1.start()
    actor2.start()
  }

}
