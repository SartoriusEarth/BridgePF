package global

import org.specs2.runner._
import org.junit.runner._
import play.api._
import play.api.libs.json._
import play.api.mvc._
import play.api.test._
import scala.concurrent.duration._
import scala.concurrent.Await

@RunWith(classOf[JUnitRunner])
object GlobalWithFiltersSpec extends PlaySpecification {

  "gzip filter" should {
    "do gzip on JSON response" in new WithApplication {
      val jsonAction = GlobalWithFilters.doFilter(Action {
          Results.Ok(Json.obj("name" -> "bob", "age" -> 31))
        })
      val request = FakeRequest().withHeaders(ACCEPT_ENCODING -> "gzip")
      val result = jsonAction(request).run
      header(CONTENT_ENCODING, result) must beSome("gzip")
    }
  }

  "gzip filter" should {
    "not do gzip if gzip encoding is not accepted by the client" in new WithApplication {
      val jsonAction = GlobalWithFilters.doFilter(Action {
          Results.Ok(Json.obj("name" -> "bob", "age" -> 31))
        })
      val request = FakeRequest()
      val result = jsonAction(request).run
      header(CONTENT_ENCODING, result) must not beSome("gzip")
    }
  }

  "HTTP" should {
    "redirect HTTP to HTTPS for Heroku" in new WithApplication {
      val uri = "/fakePath?fakeQuery=fake&p=q"
      val request = FakeRequest(GET, uri).withHeaders(X_FORWARDED_PROTO -> "http")
      val result = route(request).get
      status(result) must equalTo(MOVED_PERMANENTLY)
      redirectLocation(result).get must equalTo("https://" + request.host + request.uri)
    }
  }

  "HTTP" should {
    "not redirect HTTPS " in new WithApplication {
      val request = FakeRequest(GET, "/someFakePath?fakeQuery=fake&p=q").withHeaders(X_FORWARDED_PROTO -> "https")
      val result = route(request).get
      status(result) must equalTo(NOT_FOUND)
    }
  }
}
