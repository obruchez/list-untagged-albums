package org.bruchez.olivier.listuntaggedalbums

import java.nio.file._
import scala.util._

case class Arguments(path: Path,
                     extensionsToCheck: Set[String] = Arguments.DefaultExtensionsToCheck,
                     tagsToCheck: Set[String] = Arguments.DefaultTagsToCheck,
                     filesToCheck: Set[String] = Arguments.DefaultFilesToCheck)

object Arguments {
  protected val DefaultExtensionsToCheck = Set("flac", "m4a", "mp2", "mp3", "mpc", "ogg")
  protected val DefaultTagsToCheck = Set("artist", "title")
  protected val DefaultFilesToCheck = Set[String]()

  def apply(args: Array[String]): Try[Arguments] = {
    if (args.length >= 1) {
      val path = Paths.get(args(args.length - 1)).toAbsolutePath

      val defaultArguments = Arguments(path = path)

      fromArgs(args = args.slice(0, args.length - 2).toList, arguments = defaultArguments)
    } else {
      Failure(new IllegalArgumentException("Path missing"))
    }
  }

  // scalastyle:off cyclomatic.complexity
  @annotation.tailrec
  private def fromArgs(args: List[String], arguments: Arguments): Try[Arguments] =
    args match {
      case Nil =>
        Success(arguments)
      case arg :: remainingArgs =>
        (arg match {
          case ExtensionsArgument if remainingArgs.nonEmpty =>
            val extensions = setFromString(remainingArgs.head)
            Success((arguments.copy(extensionsToCheck = extensions), remainingArgs.tail))
          case TagsArgument if remainingArgs.nonEmpty =>
            val tags = setFromString(remainingArgs.head)
            Success((arguments.copy(tagsToCheck = tags), remainingArgs.tail))
          case FilesArgument if remainingArgs.nonEmpty =>
            val files = setFromString(remainingArgs.head)
            Success((arguments.copy(filesToCheck = files), remainingArgs.tail))
          case _ =>
            Failure(new IllegalArgumentException(s"Unexpected argument: $arg"))
        }) match {
          case Success((newArguments, argsLeftToParse)) =>
            fromArgs(argsLeftToParse, newArguments)
          case Failure(throwable) =>
            Failure(throwable)
        }
    }
  // scalastyle:on cyclomatic.complexity

  private def setFromString(string: String): Set[String] =
    string.split(",").map(_.trim.toLowerCase).toSet

  val usage: String =
    s"""Usage: ListUntaggedAlbums [options] directory
      |
      |Options:
      |
      |-extensions  comma-separated list of extensions to check (default is ${DefaultExtensionsToCheck.toSeq.sorted
         .mkString(",")})
      |-tags tags   comma-separated list of tags to check (default is ${DefaultTagsToCheck.toSeq.sorted
         .mkString(",")})
      |-files files comma-separated list of files to check (e.g. cover.jpg) (default is no file)""".stripMargin

  private val ExtensionsArgument = "-extensions"
  private val TagsArgument = "-tags"
  private val FilesArgument = "-files"
}
