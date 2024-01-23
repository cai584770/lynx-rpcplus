package org.grapheco.lynx.cypherbio

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, DataInputStream, DataOutputStream}

/**
 * Created by bluejoe on 2018/11/3.
 */

object StreamUtils {
  def convertLong2ByteArray(value: Long): Array[Byte] = {
    val baos = new ByteArrayOutputStream()
    new DataOutputStream(baos).writeLong(value)
    baos.toByteArray
  }

  def convertLongArray2ByteArray(values: Array[Long]): Array[Byte] = {
    val baos = new ByteArrayOutputStream()
    val dos = new DataOutputStream(baos)
    values.foreach(dos.writeLong(_))
    baos.toByteArray
  }

  def convertByteArray2LongArray(value: Array[Byte]): Array[Long] = {
    val baos = new DataInputStream(new ByteArrayInputStream(value))
    (1 to value.length / 8).map(x => baos.readLong()).toArray
  }
}
