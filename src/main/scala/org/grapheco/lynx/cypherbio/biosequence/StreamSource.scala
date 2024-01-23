package org.grapheco.lynx.cypherbio.biosequence

import java.io.{ByteArrayInputStream, InputStream}

/**
 * @author ：Airzihao
 * @date ：Created in 2022/3/3 10:56 AM
 * @description：${description }
 * @modified By：
 * @version: $version$
 */


trait InputStreamSource {
  /**
   * note close input stream after consuming
   */
  def offerStream[T](consume: (InputStream) => T): T;
}

trait URLInputStreamSource extends InputStreamSource {
  val url: String;

  def offerStream[T](consume: InputStream => T): T;
}

class BytesInputStreamSource(bytes: Array[Byte]) extends InputStreamSource {
  override def offerStream[T](consume: (InputStream) => T): T = {
    val fis = new ByteArrayInputStream(bytes);
    val t = consume(fis);
    fis.close();
    t;
  }
}