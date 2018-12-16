package org.bruchez.olivier.listuntaggedalbums

import java.io.File
import java.nio.file.Path
import scala.io.Source
import scala.sys.process._
import scala.util._

object Ffmpeg {
  def tags(path: Path): Try[Seq[(String, String)]] = {
    val outputFile = File.createTempFile("list-untagged-albums-", ".txt")

    try {
      val cmd =
        Seq("ffmpeg", "-y", "-i", path.toString, "-f", "ffmetadata", outputFile.getAbsolutePath)

      val stringProcessLogger = newStringProcessLogger

      Try(cmd.!!(stringProcessLogger)) match {
        case Success(_) =>
          tagsFromMetadataFile(outputFile.toPath)
        case Failure(throwable) =>
          // ffmpeg outputs everything to the stderr, but merge both stdout and stderr just in case
          val outputAndErrorStrings = stringProcessLogger.outputString + stringProcessLogger.errorString

          // Include all ffmpeg output for debug purpose
          Failure(new Exception(s"ffmpeg error: ${throwable.getMessage}\n" + outputAndErrorStrings))
      }
    } finally {
      outputFile.delete()
    }
  }

  trait OutputAndErrorStrings {
    def outputString: String
    def errorString: String
  }

  private def newStringProcessLogger: ProcessLogger with OutputAndErrorStrings =
    new ProcessLogger with OutputAndErrorStrings {
      private val outputStringBuilder = new StringBuilder
      private val errorStringBuilder = new StringBuilder

      override def outputString: String = outputStringBuilder.toString
      override def errorString: String = errorStringBuilder.toString

      override def out(s: => String): Unit = synchronized {
        outputStringBuilder.append(s + "\n"); ()
      }
      override def err(s: => String): Unit = synchronized {
        errorStringBuilder.append(s + "\n"); ()
      }
      override def buffer[T](f: => T): T = f
    }

  def tagsFromMetadataFile(path: Path): Try[Seq[(String, String)]] = Try {
    for {
      line <- Source.fromFile(path.toFile).getLines.toList
      trimmedLine = line.trim
      if !(trimmedLine.startsWith(";") || trimmedLine.startsWith("[") || trimmedLine.isEmpty)
      index = trimmedLine.indexOf('=')
      if index >= 0
    } yield (trimmedLine.substring(0, index).trim.toLowerCase, trimmedLine.substring(index + 1))
  }
}
