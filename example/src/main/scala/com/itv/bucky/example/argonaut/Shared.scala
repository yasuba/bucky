package com.itv.bucky.example.argonaut

import argonaut.Argonaut._
import argonaut._
import com.itv.bucky.ArgonautSupport._
import com.itv.bucky.PayloadMarshaller
import com.itv.bucky.PayloadUnmarshaller

object Shared {

  case class Person(name: String, age: Int)

  //argonaut things
  implicit val personDecodeJson: DecodeJson[Person] =
    DecodeJson(c =>
      for {
        name <- (c --\ "name").as[String]
        age <- (c --\ "age").as[Int]
      }
        yield Person(name, age)
    )

  implicit val personEncodeJson: EncodeJson[Person] =
    EncodeJson(p =>
      jObjectFields(
        "name" -> jString(p.name),
        "age" -> jNumber(p.age)
      )
    )

  //bucky things
  val personMarshaller: PayloadMarshaller[Person] =
    marshallerFromEncodeJson

  val personUnmarshaller: PayloadUnmarshaller[Person] =
    unmarshallerFromDecodeJson


}
