package com.itv.bucky.example.basic

import com.itv.bucky.AmqpClient
import com.itv.bucky.Unmarshaller.StringPayloadUnmarshaller
import com.itv.bucky.decl.DeclarationLifecycle
import com.itv.lifecycle.Lifecycle
import com.typesafe.scalalogging.StrictLogging
import com.itv.bucky._
import com.itv.bucky.decl._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/*
This example aims to give a minimal structure to:
* Declare a queue
* Print any Strings to a stdout
It is not very useful by itself, but hopefully reveals the structure of how Bucky components fit together
 */

object StringConsumer extends App with StrictLogging {

  object Declarations {
    val queue = Queue(QueueName("queue.string"))
    val all = List(queue)
  }

  val amqpClientConfig: AmqpClientConfig = AmqpClientConfig("33.33.33.11", 5672, "guest", "guest")

  val stringToLogHandler =
    Handler { message: String =>
      Future {
        logger.info(message)
        Ack
      }
    }

  /**
    * A lifecycle is a monadic try/finally statement.
    * More detailed information is available here https://github.com/ITV/lifecycle
    */
  val lifecycle: Lifecycle[Unit] =
    for {
      amqpClient <- AmqpClientLifecycle(amqpClientConfig)
      _ <- DeclarationLifecycle(Declarations.all, amqpClient)
      _ <- amqpClient.consumer(Declarations.queue.name, AmqpClient.handlerOf(stringToLogHandler, StringPayloadUnmarshaller))
    }
      yield ()

  lifecycle.runUntilJvmShutdown()

}
