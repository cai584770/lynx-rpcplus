package org.grapheco.lynx.cypherbio.fileprocess

import java.io.{File, PrintWriter}
import java.nio.file.{Files, Path, Paths}
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.io.Source
import org.grapheco.lynx.cypherbio.fileprocess.FileNormalize.{normalize,remove}

/**
 * @author cai584770
 * @date 2023/12/22 15:39
 * @Version
 */
class ReferenceFileReader(filePath:String) {

  def getReferenceInformation:mutable.ListMap[String, String]={
    val information = new BioFileInformation(filePath).getInformation

    information
  }




  //-------------------------File Process------------------------------------


  /** *
   * process reference sequence
   * 1、get information and sequence
   * 2、process reference sequence to a file(normlize)
   *
   * @return ArrayBuffer[Map[Int, String]] -> ref(0):information ref(1):sequence
   */
  def processRef: ArrayBuffer[Map[Int, String]] = {
    val source = Source.fromFile(filePath)
    val arrayOfMaps = ArrayBuffer[Map[Int, String]]()
    var lineNumber = 0
    try {
      for (line <- source.getLines()) {
        val keyValueMap = Map(lineNumber -> line)
        arrayOfMaps += keyValueMap

        lineNumber += 1
      }
    } finally {
      source.close()
    }

    var concatenatedValue = arrayOfMaps(1)(1)

    if ((arrayOfMaps.length - 1) > 2) {
      for (i <- 2 until arrayOfMaps.length) {
        concatenatedValue = concatenatedValue + arrayOfMaps(i)(i)
      }
      arrayOfMaps(1) = Map(1 -> concatenatedValue)
      arrayOfMaps.remove(2, arrayOfMaps.length - 2)
    }

    // process reference to a temp file
    val path: Path = Paths.get(filePath)
    val folderPath: Path = path.getParent
    val fileNameWithExtension: String = path.getFileName.toString
    val fileNameWithoutExtension: String = {
      val dotIndex = fileNameWithExtension.lastIndexOf(".")
      if (dotIndex > 0) fileNameWithExtension.substring(0, dotIndex)
      else fileNameWithExtension
    }
    val fileExtension: String = {
      val dotIndex = fileNameWithExtension.lastIndexOf(".")
      if (dotIndex > 0) fileNameWithExtension.substring(dotIndex + 1)
      else ""
    }

    val result_path = folderPath + "/" + fileNameWithoutExtension + "_reference." + fileExtension

    // reference file exists do nothing, reference file not exist -> create
    if (fileExists(filePath)) {

    } else {
      var sequence = arrayOfMaps(1).headOption.map(_._2).getOrElse("Sequence is empty")
      var information = arrayOfMaps(0).headOption.map(_._2).getOrElse("Information is empty")

      sequence = normalize(remove(sequence))
      val result = information + "\n" + sequence

      val writer = new PrintWriter(new File(result_path))
      try {
        writer.write(result)
      } finally {
        writer.close()
      }
    }

    val referncePath = Map(3 -> result_path)
    arrayOfMaps += referncePath

    arrayOfMaps
  }

  /***
   * read file from path
   * @return
   */
  def readFromFile:String={
    if(fileExists(filePath)){
      val source = Source.fromFile(filePath)

    }else{

    }

    ""
  }

  def fileExists(path: String): Boolean = {
    Files.exists(Paths.get(path))
  }
}
