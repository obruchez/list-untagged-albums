package org.bruchez.olivier.listuntaggedalbums

import java.nio.file._
import scala.util._

case class Arguments(path: Path,
                     tagsToCheck: Set[String] = Arguments.DefaultTagsToCheck,
                     filesToCheck: Set[String] = Arguments.DefaultFilesToCheck)

object Arguments {
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

  @annotation.tailrec
  private def fromArgs(args: List[String], arguments: Arguments): Try[Arguments] =
    args match {
      case Nil =>
        Success(arguments)
      case arg :: remainingArgs =>
        (arg match {
          case TagsArgument if remainingArgs.nonEmpty =>
            val tags = remainingArgs.head.split(",").map(_.trim.toLowerCase).toSet
            Success((arguments.copy(tagsToCheck = tags), remainingArgs.tail))
          case FilesArgument if remainingArgs.nonEmpty =>
            val files = remainingArgs.head.split(",").map(_.trim.toLowerCase).toSet
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

  val usage: String =
    s"""Usage: ListUntaggedAlbums [options] directory
      |
      |Options:
      |
      |-tags tags   comma-separated list of tags to check (default is ${DefaultTagsToCheck.toSeq.sorted
         .mkString(",")})
      |-files files comma-separated list of files to check (e.g. cover.jpg) (default is no file)""".stripMargin

  private val TagsArgument = "-tags"
  private val FilesArgument = "-files"
}
