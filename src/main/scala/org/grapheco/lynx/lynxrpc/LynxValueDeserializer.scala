package org.grapheco.lynx.lynxrpc

import io.grpc.netty.shaded.io.netty.buffer.ByteBuf
import org.grapheco.lynx._
import org.grapheco.lynx.lynxrpc.BaseDeserializer

/**
 * @Author: Airzihao
 * @Description:
 * @Date: Created at 11:27 2022/4/14
 * @Modified By:
 */
class LynxValueDeserializer extends BaseDeserializer {
  def decodeLynxValue(byteBuf: ByteBuf): LynxValue = {
    val typeFlag: SerializerDataType.Value = SerializerDataType(byteBuf.readByte().toInt)
    typeFlag match {
      case SerializerDataType.LONG => LynxInteger(byteBuf.readLong())
      case SerializerDataType.DOUBLE => LynxDouble(byteBuf.readDouble())
      case SerializerDataType.STRING => LynxString(_decodeStringWithFlag(byteBuf))
      case SerializerDataType.BOOLEAN => LynxBoolean(byteBuf.readBoolean())
      case SerializerDataType.LYNXLIST => _decodeLynxList(byteBuf)
      case SerializerDataType.LYNXMAP => _decodeLynxMap(byteBuf)
    }
  }

  // [flag(Byte)],[length(Content)]
  private def _decodeLynxList(byteBuf: ByteBuf): LynxList = {
    val typeFlag: SerializerDataType.Value = SerializerDataType(byteBuf.readByte().toInt)
    val length: Int = byteBuf.readInt()
    val value: List[LynxValue] = typeFlag match {
      case SerializerDataType.ARRAY_LONG => new Array[Long](length).map(_ => LynxInteger(byteBuf.readLong())).toList
      case SerializerDataType.ARRAY_DOUBLE => new Array[Double](length).map(_ => LynxDouble(byteBuf.readDouble())).toList
      case SerializerDataType.ARRAY_STRING => new Array[String](length).map(_ => LynxString(_decodeStringWithFlag(byteBuf))).toList
      case SerializerDataType.ARRAY_BOOLEAN => new Array[Boolean](length).map(_ => LynxBoolean(byteBuf.readBoolean())).toList
      case SerializerDataType.ARRAY_ANY => new Array[Any](length).map(_ => decodeLynxValue(byteBuf)).toList
      case SerializerDataType.ARRAY_ARRAY => new Array[Int](length).map(_ => _decodeLynxList(byteBuf)).toList
    }
    LynxList(value)
  }

  private def _decodeLynxMap(byteBuf: ByteBuf): LynxMap = {
    val typeFlag: Int = byteBuf.readByte().toInt
    val length: Int = byteBuf.readInt()
    LynxMap(new Array[Int](length).map(_ => _decodeStringWithFlag(byteBuf) -> decodeLynxValue(byteBuf)).toMap)
  }

}
