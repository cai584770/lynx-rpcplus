package org.grapheco.lynx.cypherbio

import org.grapheco.lynx.func.{LynxProcedure, LynxProcedureArgument}
import org.grapheco.lynx.procedure.ProcedureException
import org.grapheco.lynx.types.composite.LynxList
import org.grapheco.lynx.types.property.LynxString

/**
 * @author cai584770
 * @date 2024/1/15 10:06
 * @Version
 */
class DefaultBioSequenceFunctions {

  @LynxProcedure(name="BioSequence.fromFile", description = "generate a biosequence object from the given file")
  def fromFile(@LynxProcedureArgument(name="targetFilePath")targetLynxFilePath:LynxString ,@LynxProcedureArgument(name="referenceFilePath")referenceLynxFilePath:LynxString):BioSequence={
    val targetFilePath = targetLynxFilePath.value
    val referenceFilePath = referenceLynxFilePath.value

    if (targetFilePath == null || targetFilePath.trim.isEmpty)
      throw ProcedureException(s"invalid target file path: $targetFilePath")
    else if (referenceFilePath == null
      || referenceFilePath.trim.isEmpty)
      throw ProcedureException(s"invalid reference file path: $referenceFilePath")

    BioSequence.fromFile(targetFilePath,referenceFilePath)
  }

  @LynxProcedure(name = "BioSequence.fromBytes", description = "generate a biosequence object from the given file")
  def fromBytes(@LynxProcedureArgument(name = "bytes") byteList: LynxList): BioSequence = {
    val bytes: Array[Byte] = byteList.value.asInstanceOf[List[Byte]].toArray
    BioSequence.fromBytes(bytes);
  }

  @LynxProcedure(name = "BioSequence.empty", description = "generate an empty biosequence")
  def empty(): BioSequence = {
    BioSequence.EMPTY
  }

  @LynxProcedure(name = "BioSequence.len", description = "get length of a biosequence object")
  def getBioSequenceLength(@LynxProcedureArgument(name = "biosequence") biosequence: LynxBioSequence): Long = {
    if (biosequence == null) {
      null.asInstanceOf[Long]
    }
    else {
      biosequence.bioSequence.length
    }
  }

  @LynxProcedure(name = "BioSequence.toString", description = "cast to a string")
  def cast2String(@LynxProcedureArgument(name = "biosequence") biosequence: LynxBioSequence, @LynxProcedureArgument(name = "encoding") encoding: LynxString): String = {
    if (biosequence == null) {
      null
    }
    else {
      new String(biosequence.bioSequence.toBytes(), encoding.value);
    }
  }

  @LynxProcedure(name = "BioSequence.referenceInformation", description = "get reference information of a biosequence object")
  def getReferenceInformation(@LynxProcedureArgument(name = "biosequence") biosequence: LynxBioSequence): String = {
    if (biosequence == null) {
      null
    }
    else {
      biosequence.bioSequence.referenceInformation.toString
    }
  }

  @LynxProcedure(name = "BioSequence.targetInformation", description = "get target information of a biosequence object")
  def getTargetInformation(@LynxProcedureArgument(name = "biosequence") biosequence: LynxBioSequence): String = {
    if (biosequence == null) {
      null
    }
    else {
      biosequence.bioSequence.information.toString
    }
  }


}
