package org.grapheco.lynx.cypherbio.fileprocess

import java.nio.file.{Path, Paths}
import scala.collection.mutable

/**
 * @author cai584770
 * @date 2024/1/8 9:57
 * @Version
 */
class BioFileInformation(filePath: String){

  def getInformation:mutable.ListMap[String, String]={
    val information: mutable.ListMap[String, String] = mutable.ListMap.empty
    val path: Path = Paths.get(filePath)
    // get file name
    val fileName: String = path.getFileName.toString
    val fileNamePrefix: String = {
      if (fileName.contains(".")) {
        fileName.substring(0, fileName.lastIndexOf("."))
      } else {
        fileName
      }
    }

    // get file suffix
    val fileExtension: String = {
      val fileNameStr = path.getFileName.toString
      if (fileNameStr.contains(".")) {
        fileNameStr.substring(fileNameStr.lastIndexOf(".") + 1)
      } else {
        ""
      }
    }

    val fileType: String = fileExtension match {
      case "sam" => "SAM"
      case "fa" | "fas" | "fasta" => "FASTA"
      case "fq" | "fastq" => "FASTQ"
      case "bam" => "BAM"
      case "sam" => "SAM"

      case _ => "Other"
    }

    // get parent name
    val parentFolderName: String = path.getParent.getFileName.toString

    information += ("filename" -> fileNamePrefix)
    information += ("filetype" -> fileType)
    information += ("parent" -> parentFolderName)
    information += ("filesuffix" -> fileExtension)

    information
  }

}
