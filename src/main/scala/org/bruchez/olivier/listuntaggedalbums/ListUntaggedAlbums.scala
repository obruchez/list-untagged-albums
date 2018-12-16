package org.bruchez.olivier.listuntaggedalbums

import scala.util._

object ListUntaggedAlbums {
  def main(args: Array[String]): Unit = {
    Arguments(args) match {
      case Failure(throwable) =>
        System.err.println(throwable.getMessage)
        System.err.println()
        System.err.println(Arguments.usage)
        System.exit(-1)
      case Success(arguments) =>
        check()(arguments)
    }
  }

  def check()(implicit arguments: Arguments): Unit = {
    /*
    val tags = Ffmpeg.tags(Paths.get("/Users/olivierbruchez/Downloads/test.flac"))
        println(tags)
     */

    val
  }
}
