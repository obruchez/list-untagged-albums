package org.bruchez.olivier.listuntaggedalbums

import java.nio.file._
import scala.util._

case class Arguments(path: Path = Paths.get("."),
                     extensionsToCheck: Set[String] = Arguments.DefaultExtensionsToCheck,
                     tagsToCheck: Set[String] = Arguments.DefaultTagsToCheck,
                     filesToCheck: Set[String] = Arguments.DefaultFilesToCheck,
                     maxAncestorLevelsToCheckForFiles: Int =
                       Arguments.DefaultMaxAncestorLevelsToCheckForFiles)

object Arguments {
  protected val DefaultExtensionsToCheck = Set("flac", "m4a", "mp2", "mp3", "mpc", "ogg")
  protected val DefaultTagsToCheck = Set("artist", "title")
  protected val DefaultFilesToCheck = Set[String]()
  protected val DefaultMaxAncestorLevelsToCheckForFiles = 0

  case class ParsingState(argsToParse: List[String], arguments: Arguments, pathFound: Boolean)

  // scalastyle:off cyclomatic.complexity
  private def nextState(state: ParsingState,
                        arg: String,
                        remainingArgs: List[String]): Try[ParsingState] =
    arg match {
      case ExtensionsArgument if remainingArgs.nonEmpty =>
        val extensions = setFromString(remainingArgs.head)
        Success(
          state.copy(argsToParse = remainingArgs.tail,
                     arguments = state.arguments.copy(extensionsToCheck = extensions)))
      case TagsArgument if remainingArgs.nonEmpty =>
        val tags = setFromString(remainingArgs.head)
        Success(
          state.copy(argsToParse = remainingArgs.tail,
                     arguments = state.arguments.copy(tagsToCheck = tags)))
      case FilesArgument if remainingArgs.nonEmpty =>
        val files = setFromString(remainingArgs.head)
        Success(
          state.copy(argsToParse = remainingArgs.tail,
                     arguments = state.arguments.copy(filesToCheck = files)))
      case MaxLevelArgument if remainingArgs.nonEmpty =>
        Try(remainingArgs.head.toInt) match {
          case Success(maxLevel) =>
            Success(
              state.copy(argsToParse = remainingArgs.tail,
                         arguments =
                           state.arguments.copy(maxAncestorLevelsToCheckForFiles = maxLevel)))
          case Failure(_) =>
            Failure(
              new Exception(s"Integer expected after $MaxLevelArgument: ${remainingArgs.head}"))
        }
      case pathString if remainingArgs.isEmpty =>
        val path = Paths.get(pathString).toAbsolutePath
        Success(
          state.copy(argsToParse = remainingArgs,
                     arguments = state.arguments.copy(path = path),
                     pathFound = true))
      case _ =>
        Failure(new IllegalArgumentException(s"Unexpected argument: $arg"))
    }
  // scalastyle:on cyclomatic.complexity

  @annotation.tailrec
  def parse(state: ParsingState): Try[ParsingState] = {
    state.argsToParse match {
      case Nil =>
        Success(state)
      case arg :: remainingArgs =>
        nextState(state, arg, remainingArgs) match {
          case Failure(throwable) =>
            Failure(throwable)
          case Success(nextState) =>
            parse(nextState)
        }
    }
  }

  def apply(args: Array[String]): Try[Arguments] = {
    val initialState = ParsingState(args.toList, Arguments(), pathFound = false)

    parse(initialState) match {
      case Success(state) if !state.pathFound =>
        Failure(new IllegalArgumentException("Path missing"))
      case Success(state) =>
        Success(state.arguments)
      case Failure(throwable) =>
        Failure(throwable)
    }
  }

  private def setFromString(string: String): Set[String] =
    string.split(",").map(_.trim.toLowerCase).toSet

  private val ExtensionsArgument = "-extensions"
  private val TagsArgument = "-tags"
  private val FilesArgument = "-files"
  private val MaxLevelArgument = "-max-level"

  private val argumentDescriptions = Seq(
    ExtensionsArgument ->
      s"comma-separated list of extensions to check (default is ${DefaultExtensionsToCheck.toSeq.sorted
        .mkString(",")})",
    TagsArgument ->
      s"comma-separated list of tags to check (default is ${DefaultTagsToCheck.toSeq.sorted.mkString(",")})",
    FilesArgument ->
      s"comma-separated list of files to check (e.g. cover.jpg) (default is no file)",
    MaxLevelArgument ->
      s"maximum number of ancestor directories to check for files (default is $DefaultMaxAncestorLevelsToCheckForFiles)"
  )

  private val argumentDescriptionsString: String = {
    val longestArgumentLength = argumentDescriptions.map(_._1.length).max

    argumentDescriptions map {
      case (argument, description) =>
        argument + " " * (longestArgumentLength - argument.length + 1) + description
    } mkString "\n"
  }

  val usage: String = "Usage: ListUntaggedAlbums [options] directory\n\nOptions:\n\n" + argumentDescriptionsString
}
