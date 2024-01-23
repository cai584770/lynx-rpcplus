package org.grapheco.lynx.cypherbio

import org.grapheco.lynx._
import org.grapheco.lynx.cypherplus.{CypherRunnerPlus, GraphModelPlus, TypeSystemWithBlob}
import org.grapheco.lynx.evaluator.{DefaultExpressionEvaluator, ExpressionContext, ExpressionEvaluator}
import org.grapheco.lynx.procedure.ProcedureRegistry
import org.grapheco.lynx.types.property.{LynxBoolean, LynxFloat, LynxNull}
import org.grapheco.lynx.types.structural.HasProperty
import org.grapheco.lynx.types.{LynxValue, TypeSystem}
import org.opencypher.v9_0.expressions._
import org.opencypher.v9_0.util.symbols.CTAny

import java.util.Base64
/**
 * @author cai584770
 * @date 2024/1/15 10:19
 * @Version
 */

class BioSequenceCypherRunnerPlus(graphModel: BioSequenceGraphModelPlus,proceduresName: Seq[String] = Seq.empty)
  extends CypherRunnerPlus(graphModel, proceduresName: Seq[String]) {
  override protected lazy val expressionEvaluator: ExpressionEvaluator = new ExpressionEvaluatorPlus(graphModel, types, procedures)
  override protected lazy val types: TypeSystem = new TypeSystemWithBioSequence()

  procedures.registerAnnotatedClass(classOf[DefaultBioSequenceFunctions])
}

class TypeSystemWithBioSequence extends TypeSystemWithBlob {
  override def wrap(value: Any): LynxValue = value match {
    case bioSequence: BioSequence => LynxBioSequence(bioSequence)
    case _ => super.wrap(value)

  }
}

object LynxBioSequenceType extends LynxType {
  override def parentType: LynxType = CTAny

  override def toNeoTypeString: String = "BioSequence"
}

case class LynxBioSequence(bioSequence: BioSequence) extends LynxValue {
  override def value: Any = bioSequence

  override def lynxType: LynxType = LynxBioSequenceType

  override def sameTypeCompareTo(o: LynxValue): Int = ???
}

trait SemanticComparator {
  val defaultThreshold: Double = 0.7

  def compare(a: LynxValue, b: LynxValue): Option[Double]
}

trait BioSequenceGraphModelPlus extends GraphModelPlus {

  def getInternalBioSequence(bid: String): BioSequence

}

case class AlgorithmWithThreshold(algorithm: Option[String], threshold: Option[Double])

class ExpressionEvaluatorPlus(graphModel: BioSequenceGraphModelPlus, types: TypeSystem, procedures: ProcedureRegistry)
  extends DefaultExpressionEvaluator(graphModel, types, procedures) {
  val base64Decoder = Base64.getDecoder

  override def eval(expr: Expression)(implicit ec: ExpressionContext): LynxValue = {
    expr match {
      case ASTBioSequenceLiteral(value) =>
        value match {
          case BioSequenceFileURL(targetFilePath,referenceFilePath) =>
            LynxBioSequence(BioSequence.fromFile(targetFilePath,referenceFilePath))
          case BioSequenceBase64URL(base64) =>
            LynxBioSequence(BioSequence.fromBytes(base64Decoder.decode(base64)))
          case BioSequenceInternalURL(bid) =>
            LynxBioSequence(graphModel.getInternalBioSequence(bid))
        }

      case ASTSemanticLike(lhs, ant, rhs) =>
        safeBinaryOp(lhs, rhs, (lvalue, rvalue) => {
          val opr = graphModel.getSemanticComparator(ant.flatMap(_.algorithm))
          opr.compare(lvalue, rvalue) match {
            case None => LynxNull
            case Some(sim) => LynxBoolean(sim >= ant.flatMap(_.threshold).getOrElse(opr.defaultThreshold))
          }
        })

      case ASTSemanticUnlike(lhs, ant, rhs) =>
        safeBinaryOp(lhs, rhs, (lvalue, rvalue) => {
          val opr = graphModel.getSemanticComparator(ant.flatMap(_.algorithm))
          opr.compare(lvalue, rvalue) match {
            case None => LynxNull
            case Some(sim) => LynxBoolean(sim < ant.flatMap(_.threshold).getOrElse(opr.defaultThreshold))
          }
        })

      case ASTSemanticCompare(lhs, ant, rhs) =>
        safeBinaryOp(lhs, rhs, (lvalue, rvalue) => {
          val opr = graphModel.getSemanticComparator(ant.flatMap(_.algorithm))
          opr.compare(lvalue, rvalue) match {
            case None => LynxNull
            case Some(sim) => LynxFloat(sim)
          }
        })

      case ASTCustomProperty(map, propertyKey) =>
        graphModel.getSubProperty(eval(map), propertyKey.name)

      case ASTCustomPropertyWithVersionNum(map, versionNumber) =>
        map match {
          case ASTCustomProperty(map, PropertyKeyName(name)) => graphModel.getSubPropertyAtVersion(eval(map), name, versionNumber)
          case Property(src, PropertyKeyName(name)) => graphModel.getPropertyAtVersion(eval(src).asInstanceOf[HasProperty], name, versionNumber)
          case _ => throw new Exception("Not a subproperty.")
        }

      case _ =>
        super.eval(expr)
    }
  }

  private def safeBinaryOp(lhs: Expression, rhs: Expression, op: (LynxValue, LynxValue) => LynxValue)(implicit ec: ExpressionContext): LynxValue = {
    val l = eval(lhs)
    if(l.value==null) return LynxBoolean(false)
    val r = eval(rhs)
    if(r.value==null) return LynxBoolean(false)
    op(l, r)
  }
}
