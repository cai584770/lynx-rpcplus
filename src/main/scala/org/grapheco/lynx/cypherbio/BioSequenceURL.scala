package org.grapheco.lynx.cypherbio

import org.opencypher.v9_0.expressions._
import org.opencypher.v9_0.util.InputPosition
import org.opencypher.v9_0.util.symbols.{CTAny, CTBoolean, CTFloat, CTList}

/**
 * @author cai584770
 * @date 2024/1/15 13:40
 * @Version
 */
trait BioSequenceURL {
  def asCanonicalString: String
}

case class ASTBioSequenceLiteral(value: BioSequenceURL)(val position: InputPosition) extends Expression {
  override def asCanonicalStringVal = value.asCanonicalString
}

case class BioSequenceFileURL(filePath: String,referenceFilePath:String) extends BioSequenceURL {
  override def asCanonicalString = filePath
}

case class BioSequenceBase64URL(base64: String) extends BioSequenceURL {
  override def asCanonicalString = base64
}

case class BioSequenceInternalURL(bioSequenceId: String) extends BioSequenceURL {
  override def asCanonicalString = bioSequenceId
}

case class BioSequenceHttpURL(url: String) extends BioSequenceURL {
  override def asCanonicalString = url
}

case class BioSequenceFtpURL(url: String) extends BioSequenceURL {
  override def asCanonicalString = url
}

case class ASTAlgoNameWithThreshold(algorithm: Option[String], threshold: Option[Double])(val position: InputPosition)
  extends Expression {
}

case class ASTCustomProperty(map: Expression, propertyKey: PropertyKeyName)(val position: InputPosition) extends LogicalProperty {
  override def asCanonicalStringVal = s"${map.asCanonicalStringVal}.${propertyKey.asCanonicalStringVal}"
}

case class ASTCustomPropertyWithVersionNum(map: Expression, verNum: VerNum)(val position: InputPosition) extends LogicalProperty {
  override def propertyKey: PropertyKeyName = PropertyKeyName("Specified Version")(position)
}

case class ASTSemanticLike(lhs: Expression, ant: Option[ASTAlgoNameWithThreshold], rhs: Expression)(val position: InputPosition)
  extends Expression with BinaryOperatorExpression {
  override val signatures = Vector(
    TypeSignature(argumentTypes = Vector(CTAny, CTAny), outputType = CTBoolean)
  )

  override def canonicalOperatorSymbol = this.getClass.getSimpleName
}

case class ASTSemanticUnlike(lhs: Expression, ant: Option[ASTAlgoNameWithThreshold], rhs: Expression)(val position: InputPosition)
  extends Expression with BinaryOperatorExpression {
  override val signatures = Vector(
    TypeSignature(argumentTypes = Vector(CTAny, CTAny), outputType = CTBoolean)
  )

  override def canonicalOperatorSymbol = this.getClass.getSimpleName
}

case class ASTSemanticCompare(lhs: Expression, ant: Option[ASTAlgoNameWithThreshold], rhs: Expression)(val position: InputPosition)
  extends Expression with BinaryOperatorExpression {
  override val signatures = Vector(
    TypeSignature(argumentTypes = Vector(CTAny, CTAny), outputType = CTFloat)
  )

  override def canonicalOperatorSymbol = this.getClass.getSimpleName
}

case class ASTSemanticSetCompare(lhs: Expression, ant: Option[ASTAlgoNameWithThreshold], rhs: Expression)(val position: InputPosition)
  extends Expression with BinaryOperatorExpression {
  override val signatures = Vector(
    TypeSignature(argumentTypes = Vector(CTAny, CTAny), outputType = CTList(CTList(CTFloat)))
  )

  override def canonicalOperatorSymbol = this.getClass.getSimpleName
}

case class ASTSemanticContain(lhs: Expression, ant: Option[ASTAlgoNameWithThreshold], rhs: Expression)(val position: InputPosition)
  extends Expression with BinaryOperatorExpression {
  override val signatures = Vector(
    TypeSignature(argumentTypes = Vector(CTAny, CTAny), outputType = CTBoolean)
  )

  override def canonicalOperatorSymbol = this.getClass.getSimpleName
}

case class ASTSemanticIn(lhs: Expression, ant: Option[ASTAlgoNameWithThreshold], rhs: Expression)(val position: InputPosition)
  extends Expression with BinaryOperatorExpression {
  override val signatures = Vector(
    TypeSignature(argumentTypes = Vector(CTAny, CTAny), outputType = CTBoolean)
  )

  override def canonicalOperatorSymbol = this.getClass.getSimpleName
}

case class ASTSemanticContainSet(lhs: Expression, ant: Option[ASTAlgoNameWithThreshold], rhs: Expression)(val position: InputPosition)
  extends Expression with BinaryOperatorExpression {
  override val signatures = Vector(
    TypeSignature(argumentTypes = Vector(CTAny, CTAny), outputType = CTBoolean)
  )

  override def canonicalOperatorSymbol = this.getClass.getSimpleName
}

case class ASTSemanticSetIn(lhs: Expression, ant: Option[ASTAlgoNameWithThreshold], rhs: Expression)(val position: InputPosition)
  extends Expression with BinaryOperatorExpression {
  override val signatures = Vector(
    TypeSignature(argumentTypes = Vector(CTAny, CTAny), outputType = CTBoolean)
  )

  override def canonicalOperatorSymbol = this.getClass.getSimpleName
}

