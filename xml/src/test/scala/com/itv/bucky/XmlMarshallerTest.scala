package com.itv.bucky

import org.scalatest.FunSuite
import org.scalatest.Matchers._

class XmlMarshallerTest extends FunSuite {

  import com.itv.bucky.XmlSupport._

  test("Can marshall a foo") {
    val marshaller = marshallerFromElem

    val foo = <foo><bar>doo</bar></foo>


    marshaller(foo) shouldBe Payload.from(foo.toString)
  }

}
