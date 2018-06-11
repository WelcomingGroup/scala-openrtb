package com.powerspace.openrtb.json.bidrequest

import com.google.openrtb.BidRequest.Imp
import com.google.openrtb.BidRequest.Imp.Native
import com.google.openrtb.NativeRequest
import com.google.openrtb.NativeRequest.{Asset, EventTrackers}
import com.powerspace.openrtb.json.EncoderProvider
import com.powerspace.openrtb.json.util.EncodingUtils
import io.circe.Encoder
import io.circe.generic.extras.Configuration

trait NativeDependencies {
  implicit val nativeRequestEncoder: Encoder[NativeRequest]
}

object OpenRtbNativeSerde extends EncoderProvider[Native] with NativeDependencies {
  import io.circe._
  import io.circe.syntax._
  import EncodingUtils._
  import io.circe.generic.extras.semiauto._
  import OpenRtbProtobufEnumEncoders._

  private implicit val customConfig: Configuration = Configuration.default.withSnakeCaseMemberNames

  import io.circe.generic.extras.semiauto._

  private implicit val titleEncoder: Encoder[NativeRequest.Asset.Title] = deriveEncoder[Asset.Title].transformBooleans.clean
  private implicit val imgEncoder: Encoder[NativeRequest.Asset.Image] = deriveEncoder[Asset.Image].transformBooleans.clean

  private implicit val videoEncoder = OpenRtbVideoSerde.encoder

  private implicit val assetDataEncoder: Encoder[NativeRequest.Asset.Data] = deriveEncoder[Asset.Data].transformBooleans.clean
  private implicit val assetOneOfEncoder: Encoder[NativeRequest.Asset.AssetOneof] = protobufOneofEncoder[NativeRequest.Asset.AssetOneof] {
    case NativeRequest.Asset.AssetOneof.Img(img) => img.asJson
    case NativeRequest.Asset.AssetOneof.Data(data) => data.asJson
    case NativeRequest.Asset.AssetOneof.Video(video) => video.asJson
    case NativeRequest.Asset.AssetOneof.Title(title) => title.asJson
  }

  private implicit val assetEncoder: Encoder[NativeRequest.Asset] = deriveEncoder[Asset].transformBooleans.clean
  private implicit val eventTrackersEncoder: Encoder[NativeRequest.EventTrackers] = deriveEncoder[EventTrackers].transformBooleans.clean

  private implicit val requestOneOfEncoder: Encoder[Imp.Native.RequestOneof] = protobufOneofEncoder[Imp.Native.RequestOneof] {
    case Imp.Native.RequestOneof.Request(string) => string.asJson
    case Imp.Native.RequestOneof.RequestNative(request) => request.asJson
  }

  implicit val nativeRequestEncoder: Encoder[NativeRequest] = deriveEncoder[NativeRequest].transformBooleans.clean

  def encoder: Encoder[Imp.Native] = deriveEncoder[Imp.Native].transformBooleans.clean
}
