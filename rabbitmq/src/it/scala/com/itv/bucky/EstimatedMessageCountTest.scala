package com.itv.bucky

import com.itv.bucky.decl.DeclarationLifecycle
import com.itv.lifecycle.Lifecycle
import org.scalatest.FunSuite
import org.scalatest.Matchers._
import org.scalatest.concurrent.Eventually

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Random, Success}
import scala.concurrent.ExecutionContext.Implicits.global

class EstimatedMessageCountTest extends FunSuite {

  test("A new queue with no messages published should have an estimated count of 0") {
    estimatedMessageCountTest(0)
  }

  test("A new queue with 1 message published should have an estimated count of 1") {
    estimatedMessageCountTest(1)
  }

  test("A new queue with n messages published should have an estimated count of n") {
    estimatedMessageCountTest(Random.nextInt(10))
  }

  implicit val patianceConfig: Eventually.PatienceConfig = Eventually.PatienceConfig(timeout = 5.seconds, 1.second)

  def estimatedMessageCountTest(messagesToPublish: Int): Unit = {
    val randomQueueName = QueueName("estimatedmessagecount" + Random.nextInt())

    val clientLifecycle = for {
      client <- AmqpClientLifecycle(IntegrationUtils.config)
      _ <- DeclarationLifecycle(IntegrationUtils.defaultDeclaration(randomQueueName), client)
      publisher <- client.publisher()
      _ = Future.sequence((1 to messagesToPublish).map(_ => publisher(PublishCommand(ExchangeName(""), RoutingKey(randomQueueName.value), MessageProperties.persistentBasic, Payload.from("hello")))))
    }
      yield client

    Lifecycle.using(clientLifecycle) { client =>
      Eventually.eventually {
        client.estimatedMessageCount(randomQueueName) shouldBe Success(messagesToPublish)
      }
    }
  }
}
