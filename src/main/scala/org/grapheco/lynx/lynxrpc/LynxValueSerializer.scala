package org.grapheco.lynx.lynxrpc

import io.grpc.netty.shaded.io.netty.buffer.ByteBuf
import org.grapheco.lynx._
import shapeless.TypeCase

/**
 * @Author: Airzihao
 * @Description:
 * @Date: Created at 17:46 2022/4/13
 * @Modified By:
 */
class LynxValueSerializer extends BaseSerializer {

  def encodeLynxValue(byteBuf: ByteBuf, value: LynxValue): ByteBuf = {
    value match {
      // The Int is regarded as long in LynxValue system.
      case lynxInteger: LynxInteger =>
        byteBuf.writeByte(SerializerDataType.LONG.id)
        byteBuf.writeLong(lynxInteger.value)
      case lynxDouble: LynxDouble =>
        byteBuf.writeByte(SerializerDataType.DOUBLE.id)
        byteBuf.writeDouble(lynxDouble.value)
      case lynxString: LynxString =>
        byteBuf.writeByte(SerializerDataType.STRING.id)
        _encodeString(lynxString.value, byteBuf)
      case lynxBoolean: LynxBoolean =>
        byteBuf.writeByte(SerializerDataType.BOOLEAN.id)
        byteBuf.writeBoolean(lynxBoolean.value)
      case lynxList: LynxList =>
        byteBuf.writeByte(SerializerDataType.LYNXLIST.id)
        _encodeLynxList(byteBuf, lynxList)
      case lynxMap: LynxMap =>
        byteBuf.writeByte(SerializerDataType.LYNXMAP.id)
        _encodeLynxMap(byteBuf, lynxMap)
      case _ => throw new Exception(s"Unexpected type of ${value}")
    }
    byteBuf
  }

  // Caution: Do not write the typeFlag of LynxList again.
  private def _encodeLynxList(byteBuf: ByteBuf, lynxList: LynxList): ByteBuf = {
    val AllLynxInteger = TypeCase[List[LynxInteger]]
    val AllLynxDouble = TypeCase[List[LynxDouble]]
    val AllLynxString = TypeCase[List[LynxString]]
    val AllLynxBoolean = TypeCase[List[LynxBoolean]]
    val AllLynxList = TypeCase[List[LynxList]]
    val AllLynxValue = TypeCase[List[LynxValue]] // Similar to List[Any]

    lynxList.value match {
      case AllLynxInteger(intList) => _encodeLongList(byteBuf, intList.map(lynxInteger => lynxInteger.value))
      case AllLynxDouble(doubleList) => _encodeDoubleList(byteBuf, doubleList.map(lynxDouble => lynxDouble.value))
      case AllLynxString(stringList) => _encodeStringList(byteBuf, stringList.map(lynxString => lynxString.value))
      case AllLynxBoolean(booleanList) => _encodeBooleanList(byteBuf, booleanList.map(lynxBoolean => lynxBoolean.value))
      case AllLynxList(listList) => {
        byteBuf.writeByte(SerializerDataType.ARRAY_ARRAY.id)
        byteBuf.writeInt(listList.length)
        listList.foreach(list => _encodeLynxList(byteBuf, list))
        byteBuf
      }

      // Caution: This case should be regarded the wildcard-case, so should be at last.
      case AllLynxValue(anyList) => {
        byteBuf.writeByte(SerializerDataType.ARRAY_ANY.id)
        byteBuf.writeInt(anyList.length)
        anyList.foreach(anyLynxValue => encodeLynxValue(byteBuf, anyLynxValue))
        byteBuf
      }
    }
  }

  // LynxMap: [String, LynxValue]
  private def _encodeLynxMap(byteBuf: ByteBuf, lynxMap: LynxMap): ByteBuf = {
    val mapLength: Int = lynxMap.value.size
    byteBuf.writeByte(SerializerDataType.LYNXMAP.id)
    byteBuf.writeInt(mapLength)
    lynxMap.value.foreach(kv => {
      _encodeString(kv._1, byteBuf)
      encodeLynxValue(byteBuf, kv._2)
    })
    byteBuf
  }

}
