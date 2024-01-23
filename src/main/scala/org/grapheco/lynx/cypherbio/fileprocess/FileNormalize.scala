package org.grapheco.lynx.cypherbio.fileprocess

/**
 * @author cai584770
 * @date 2023/12/22 15:20
 * @Version
 */
object FileNormalize {

  /** *
   * normalize sequence
   *
   * @param data remove blank for result
   * @return normalize sequence
   */
  def normalize(data: String): String = {
    val sequence: String = data.grouped(50).mkString("\n")
    sequence
  }

  /** *
   * remove blank characters for sequence file
   *
   * @param data sequence
   * @return no blank characters sequence
   */
  def remove(data: String): String = {
    val sequence: String = data.replaceAll("\\s", "")
    sequence
  }

}
