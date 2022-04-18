package org.grapheco.lynx.lynxrpc

import io.grpc.netty.shaded.io.netty.buffer.ByteBuf
import org.grapheco.lynx.{LynxList, LynxMap, LynxNode, LynxString, LynxValue}

/**
 * @Author: Airzihao
 * @Description:
 * @Date: Created at 22:31 2022/4/17
 * @Modified By:
 */
class LynxNodeSerializer extends LynxValueSerializer {

  def encodeLynxNode(node: LynxNode, byteBuf: ByteBuf): ByteBuf = {
    val id = node.value.id
    val labels: List[LynxString] = node.value.labels.map(lynxNodeLabel => LynxString(lynxNodeLabel.value)).toList
    // Warning: The prop may not exist.
    val propsMap: Map[String, LynxValue] = node.value.keys.map(key => key.value -> node.value.property(key).get).toMap
    byteBuf.writeByte(SerializerDataType.LYNXNODE.id)
    encodeLynxValue(byteBuf, id.toLynxInteger)
    encodeLynxValue(byteBuf, LynxList(labels))
    encodeLynxValue(byteBuf, LynxMap(propsMap))
  }

}
