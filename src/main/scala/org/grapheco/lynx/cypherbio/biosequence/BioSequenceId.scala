package org.grapheco.lynx.cypherbio.biosequence

import org.apache.commons.codec.binary.Hex
import org.grapheco.lynx.cypherbio.StreamUtils

import java.io.{ByteArrayInputStream, DataInputStream, InputStream}

/**
 * @author cai584770
 * @date 2024/1/10 14:56
 * @Version
 */
case class BioSequenceId(id1: Long) {

  def asByteArray(): Array[Byte] = {
    StreamUtils.convertLong2ByteArray(id1);
  }

  def asLiteralString(): String = {
    Hex.encodeHexString(asByteArray());
  }

  override def equals(obj: Any): Boolean = obj match {
    case BioSequenceId(id2) => id1 == id2
    case _ => false
  }
}

object BioSequenceId {
  val EMPTY = new BioSequenceId(0)

  def fromBytes(bytes: Array[Byte]): BioSequenceId = {
    val is = new DataInputStream(new ByteArrayInputStream(bytes))
    new BioSequenceId(is.readLong());
  }

  def readFromStream(is: InputStream): BioSequenceId = {
    val bytes = new Array[Byte](16)
    new DataInputStream(is).readFully(bytes)
    fromBytes(bytes)
  }

  def fromPlainId(plainId: Long): BioSequenceId = {
    new BioSequenceId(plainId)
  }

}



