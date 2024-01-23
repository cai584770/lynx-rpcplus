package org.grapheco.lynx.cypherbio.fileprocess

import java.io.{FileWriter, IOException}
import java.nio.file.{Files, Path, Paths}
import scala.collection.mutable
import scala.io.Source
import org.grapheco.lynx.cypherbio.fileprocess.FileNormalize.{normalize,remove}

/**
 * @author cai584770
 * @date 2023/12/25 14:27
 * @Version
 */
class TargetFileReader(filePath: String) {

  /** * get file information
   *
   * @return
   * ListMap
   */
  def getFileInformation: mutable.ListMap[String, String] = new BioFileInformation(filePath).getInformation


//-------------------------File Process------------------------------------
  /**
   *
   * @return
   * mutable.ListMap[Int, (String,String)] Int -> sequence index, (String,
   * String) -> the first String is sequence information, the second String
   * is sequence
   */
  def targetFileDivide: mutable.ListMap[Int, (String, String)] = {
    val datasource = Source.fromFile(filePath)
    var datas: Array[String] = Array.empty[String]
    val emptyListMap: mutable.ListMap[Int, (String, String)] =
      mutable.ListMap.empty
    var inf: String = ""
    var sequence: String = ""
    var temptuple: (String, String) = null
    var count = 0

    try {
      datas = datasource.getLines().toArray // get data for file
    } finally {
      datasource.close()
    }

    for (data <- datas) { // process datas to a ListMap
      if (data.startsWith(">")) {
        if (sequence != "") { // store
          sequence = normalize(remove(sequence))
          temptuple = (inf, sequence)
//          var filePath =
//          Files.write(Paths.get(filePath), content.getBytes(StandardCharsets.UTF_8))
          emptyListMap += (count -> temptuple)
          count += 1
        }

        inf = data // init key and value
        sequence = ""

      } else if (!data.equals(datas(datas.length - 1))) { // process sequence
        sequence = sequence + data
      } else { // process last line
        sequence = sequence + data
        sequence = normalize(remove(sequence))
        temptuple = (inf, sequence)
        emptyListMap += (count -> temptuple)
      }
    }


    emptyListMap
  }


  /** *
   *
   * write target sequence to temp file
   *
   */
  def processTarFile(sequence: String, information: String, path: String): String = {
    val parentPath: Path = Paths.get(filePath).getParent
    val parentPathString: String = parentPath.toString

    val tempDict = parentPathString + "/temp"

    try {
      if (!Files.exists(Paths.get(path))) {
        Files.createFile(Paths.get(path))
      }
      val fileWriter = new FileWriter(path)
      fileWriter.write(information)
      fileWriter.write("\n")
      fileWriter.write(sequence)
      fileWriter.close()

      if (!Files.exists(Paths.get(tempDict))) {
        Files.createDirectories(Paths.get(tempDict))
      }
    } catch {
      case e: IOException =>
        e.printStackTrace()
    }


    tempDict
  }

  /** *
   * get target temp file path
   *
   * @param index file block index
   * @return temp file path
   */
  def tempTargetFilePath(index: Int): String = {
    // file path
    val parentPath: Path = Paths.get(filePath).getParent
    val parentPathString: String = parentPath.toString

    // file name
    val fileName: String = Paths.get(filePath).getFileName.toString

    // file pre
    val pattern = """([^/]+)\..+""".r
    val extractedPart: String = fileName match {
      case pattern(baseName) => baseName
      case _ => ""
    }

    // file suffix
    val parts: Array[String] = fileName.split("\\.")
    val extension: String = if (parts.length > 1) parts.last else ""

    val tempFilePath = parentPathString + "/" + extractedPart + s"_${index}." + extension

    tempFilePath
  }

}
