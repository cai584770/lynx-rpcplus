package org.grapheco.lynx.cypherbio

import org.apache.commons.io.IOUtils
import org.apache.http.impl.client.HttpClientBuilder
import org.grapheco.lynx.cypherbio.biosequence.BioSequenceId
import org.grapheco.lynx.cypherbio.fileprocess.{ReferenceFileReader, TargetFileReader}
import org.grapheco.lynx.cypherbio.sccg.SCCG

import java.io._
import java.net.URL
import scala.collection.mutable

/**
 * @author cai584770
 * @date 2024/1/10 14:57
 * @Version
 */
object BioSequence {
  val httpClient = HttpClientBuilder.create().build();

  private class BioSequenceImpl(val length:Long,val sequence:String,val referenceInformation: mutable.ListMap[String, String],val information: mutable.ListMap[String, String])
    extends BioSequence {
    def withId(id: BioSequenceId): BioSequenceWithId = new BioSequenceWithIdImpl( length, sequence, referenceInformation, information, id)

  }

  private class BioSequenceWithIdImpl(override val length:Long,override val sequence:String,override val referenceInformation: mutable.ListMap[String, String],override val information: mutable.ListMap[String, String], override val id: BioSequenceId)
    extends BioSequenceWithId {
  }

  def setId(bioSequence: BioSequence, id: BioSequenceId): BioSequenceWithId =
    new BioSequenceWithIdImpl(bioSequence.length, bioSequence.sequence, bioSequence.referenceInformation, bioSequence.information, id)

  def makeBioSequence( length:Long, sequence:String, reference: mutable.ListMap[String, String], information: mutable.ListMap[String, String]): BioSequence =
    new BioSequenceImpl(length, sequence, reference, information);


  def fromBytes(bytes: Array[Byte]): BioSequence = {
    try {
      val byteArrayInputStream = new ByteArrayInputStream(bytes)
      val bioSequenceInputStream = new ObjectInputStream(byteArrayInputStream)
      val bioSequence = bioSequenceInputStream.readObject().asInstanceOf[BioSequence]
      bioSequenceInputStream.close()
      bioSequence
    } catch {
      case eofException: EOFException =>
        EMPTY
      case ex: Exception =>
        throw ex
    }
  }

  val EMPTY: BioSequence = fromBytes(Array[Byte]());

  /***
   *
   * @param targetFilePath target file path
   * @param referenceFilePath reference file path
   * @return BioSequence(length, bioType, sequence(position code sequence), reference, parent, information)
   */
  def fromFile(targetFilePath: String,referenceFilePath:String):BioSequence={
    val sccg = new SCCG

    // compress & get sequence
//    val sequence = sccg.compress(targetFilePath,referenceFilePath)

    // test in windows & no 7za
     val sequence = "AAAAAAAAAAAAAAAAAAAAA"

    val length = sequence.length


    new BioSequenceImpl(length,sequence,new ReferenceFileReader(referenceFilePath).getReferenceInformation,new TargetFileReader(targetFilePath).getFileInformation)
  }

  def fromURL(tarurl: String,refurl:String): BioSequence = {
    val p = "(?i)(http|https|file|ftp|ftps):\\/\\/[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?^=%&:/~\\+#]*[\\w\\-\\@?^=%&/~\\+#])?".r
    val uri = p.findFirstIn(tarurl).getOrElse(tarurl)

    val lower = uri.toLowerCase();
   if (lower.startsWith("file://")) {
      fromFile(tarurl,refurl);
    }
    else {
      fromBytes(IOUtils.toByteArray(new URL(uri)));
    }
  }

}

trait BioSequence extends Comparable[BioSequence] with Serializable{
  val length:Long
  val sequence:String
  val referenceInformation:mutable.ListMap[String, String] // reference information
  val information:mutable.ListMap[String, String]

  override def compareTo(bio: BioSequence) = this.length.compareTo(bio.length);

  override def toString = s"biosequence(length=${length},referenceInformation=${referenceInformation},information=${information}";

  def toBytes(): Array[Byte] = {
    val byteArrayOutputStream = new ByteArrayOutputStream()
    val objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)
    objectOutputStream.writeObject(this)
    objectOutputStream.close()
    byteArrayOutputStream.toByteArray
  }

  override def equals(obj: Any): Boolean = obj match {
    case biosequence: BioSequence => {
      length == biosequence.length &&
        sequence == biosequence.sequence &&
        referenceInformation == biosequence.referenceInformation &&
        information == biosequence.information
    }
    case _ => false
  }

}

trait BioSequenceWithId extends BioSequence{
  val id:BioSequenceId
}
