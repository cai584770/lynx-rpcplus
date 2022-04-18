import org.grapheco.lynx.{LynxId, LynxInteger, LynxNode, LynxNodeLabel, LynxPropertyKey, LynxValue}

/**
 * @Author: Airzihao
 * @Description:
 * @Date: Created at 08:33 2022/4/18
 * @Modified By:
 */
class TestLynxNode(nid: Long, nlabels: Seq[String], props: Map[String, LynxValue]) extends LynxNode{
  override val id: LynxId = new LynxId {
    override val value: Any = nid

    override def toLynxInteger: LynxInteger = LynxInteger(nid)
  }

  override def labels: Seq[LynxNodeLabel] = nlabels.map(string => LynxNodeLabel(string))

  override def keys: Seq[LynxPropertyKey] = props.keys.map(key => LynxPropertyKey(key)).toSeq

  override def property(propertyKey: LynxPropertyKey): Option[LynxValue] = props.get(propertyKey.value)
}
