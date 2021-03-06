/*
 * Copyright (C) 2016-2020 Lightbend Inc. <https://www.lightbend.com>
 */

package akka.stream.alpakka.google.firebase.fcm.javadsl

import akka.japi.Pair
import akka.stream.alpakka.google.firebase.fcm.impl.FcmFlows
import akka.stream.alpakka.google.firebase.fcm.{FcmNotification, FcmResponse, FcmSettings}
import akka.stream.{javadsl, scaladsl}
import akka.{Done, NotUsed}

import java.util.concurrent.CompletionStage

object GoogleFcm {

  def sendWithPassThrough[T](conf: FcmSettings): javadsl.Flow[Pair[FcmNotification, T], Pair[FcmResponse, T], NotUsed] =
    scaladsl
      .Flow[Pair[FcmNotification, T]]
      .map(_.toScala)
      .via(FcmFlows.fcmWithData[T](conf))
      .map(t => Pair(t._1, t._2))
      .asJava

  def send(conf: FcmSettings): javadsl.Flow[FcmNotification, FcmResponse, NotUsed] =
    FcmFlows.fcm(conf).asJava

  def fireAndForget(conf: FcmSettings): javadsl.Sink[FcmNotification, CompletionStage[Done]] =
    send(conf)
      .toMat(javadsl.Sink.ignore(), javadsl.Keep.right[NotUsed, CompletionStage[Done]])

}
