import org.grapheco.lynx.lynxrpc.LynxIdImpl
import org.grapheco.lynx.{LynxId, LynxPropertyKey, LynxRelationship, LynxRelationshipType, LynxValue}

/**
 * @Author: Airzihao
 * @Description:
 * @Date: Created at 10:26 2022/4/18
 * @Modified By:
 */
class TestLynxRelationship(rId: Long, rType: String, rStartId: Long, rEndId: Long,
                           props: Map[String, LynxValue]) extends LynxRelationship {
  override val id: LynxId = new LynxIdImpl(rId)
  override val startNodeId: LynxId = new LynxIdImpl(rStartId)
  override val endNodeId: LynxId = new LynxIdImpl(rEndId)

  override def relationType: Option[LynxRelationshipType] = Some(LynxRelationshipType(rType))

  override def keys: Seq[LynxPropertyKey] = props.keys.map(LynxPropertyKey).toSeq

  override def property(propertyKey: LynxPropertyKey): Option[LynxValue] = props.get(propertyKey.value)
}
