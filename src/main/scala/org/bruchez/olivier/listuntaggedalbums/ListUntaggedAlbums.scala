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
        ListUntaggedAlbums(arguments).check()
    }
  }
}

case class ListUntaggedAlbums(arguments: Arguments) {
  def check(): Unit = {
    val filesToCheckByParentDirectory = FileUtils
      .allFilesInPath(arguments.path, recursive = true)
      .filter { path =>
        arguments.extensionsToCheck.exists(path.toString.toLowerCase.endsWith)
      } groupBy {
      _.getParent
    }

    val directoriesWithMissingTags = this.directoriesWithMissingTags(filesToCheckByParentDirectory)

    println(s"Directories with missing tags: ${directoriesWithMissingTags.size}")

    directoriesWithMissingTags.toSeq.sortBy(_.toAbsolutePath.toString) foreach { path =>
      println(s" - ${path.toString}")
    }

    val directoriesWithMissingFiles =
      this.directoriesWithMissingFiles(filesToCheckByParentDirectory.keySet)

    println(s"Directories with missing files: ${directoriesWithMissingFiles.size}")

    directoriesWithMissingFiles.toSeq.sortBy(_.toAbsolutePath.toString) foreach { path =>
      println(s" - ${path.toString}")
    }
  }

  // @todo do not check if no tag in arguments
  private def directoriesWithMissingTags(
      filesToCheckByParentDirectory: Map[Path, Seq[Path]]): Set[Path] =
    filesToCheckByParentDirectory.filter(_._2.exists(hasMissingTags)).keySet

  // @todo do not check if no file in arguments (default)
  private def directoriesWithMissingFiles(parentDirectories: Set[Path]): Set[Path] =
    parentDirectories.filter(hasMissingFiles)

  private def hasMissingTags(path: Path): Boolean =
    Ffmpeg.tags(path) match {
      case Success(tags) =>
        val tagSet = tags.map(_._1).toSet
        !arguments.tagsToCheck.forall(tagSet.contains)
      case Failure(throwable) =>
        System.err.println(
          s"Could not retrieve tags for '${path.toString}' (${throwable.getMessage})")
        false
    }

  private def hasMissingFiles(path: Path): Boolean = {
    // @todo check 0 or more levels depending on maxAncestorLevelsToCheckForFiles
    false
  }
}
