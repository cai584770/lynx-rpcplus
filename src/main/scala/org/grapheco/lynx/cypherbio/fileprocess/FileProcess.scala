package org.grapheco.lynx.cypherbio.fileprocess

import org.grapheco.lynx.cypherbio.fileprocess.FileNormalize.{normalize,remove}

import scala.collection.mutable
import scala.io.Source

/** todo
 * @author cai584770
 * @date 2023/12/25 15:13
 * @Version
 */
class FileProcess(filePath:String) {

  def divide:Unit={
    val temp: Array[String] = readToString(filePath).split("(?=>)")
    temp.foreach{
      str => println(str)
      println(str.length)
    }


  }

  def readToString(filePath:String):String = {
    val datasources = Source.fromFile(filePath)
    val datas = datasources.mkString
    datasources.close()
    datas
  }


  /***
   * divide target file if has some sequence
   * @return File order, information and sequence
   */
  def divide01: mutable.ListMap[Int, (String, String)]={
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




}

