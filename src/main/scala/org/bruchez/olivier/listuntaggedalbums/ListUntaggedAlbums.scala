package org.bruchez.olivier.listuntaggedalbums

import java.nio.file.Path

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
    val filesToCheckByAlbum = FileUtils.allFilesInPath(arguments.path, recursive = true).filter {
      path =>
        arguments.extensionsToCheck.exists(path.toString.toLowerCase.endsWith)
    } groupBy {
      albumFolder
    }

    // @todo
  }

  private def albumFolder(path: Path): Path = {
    // @todo
    path
  }
}
