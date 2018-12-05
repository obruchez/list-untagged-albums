package org.bruchez.olivier.listuntaggedalbums

import java.nio.file._

import scala.util._

object ListUntaggedAlbums {
  def main(args: Array[String]): Unit = {
    //FileUtils.dumpExtensionStatistics(java.nio.file.Paths.get(args(0)))
    //System.exit(0)

    Arguments(args) match {
      case Failure(throwable) =>
        System.err.println(throwable.getMessage)
        System.err.println()
        System.err.println(Arguments.usage)
        System.exit(-1)
      case Success(arguments) =>
      //convert()(arguments)
    }
  }
}

/*
ffmpeg -i 01\ It\'s\ About\ That\ Time\ -\ The\ Mask.mp3 -f ffmetadata - 2>/dev/null

;FFMETADATA1
album=Sky Garden
artist=Yo Miles!
disc=1/2
title=It's About That Time / The Mask
track=1/5
genre=Jazz
date=2004
REPLAYGAIN_REFERENCE_LOUDNESS=89.0 dB
REPLAYGAIN_TRACK_GAIN=-1.40 dB
REPLAYGAIN_TRACK_PEAK=1.00000000
REPLAYGAIN_ALBUM_GAIN=-1.30 dB
REPLAYGAIN_ALBUM_PEAK=1.00000000
encoder=Lavf57.83.100

ffmpeg -i 01\ Go\ Ahead\ John.flac -f ffmetadata - 2>/dev/null
;FFMETADATA1
TITLE=Go Ahead John
ALBUM=Upriver
ARTIST=Yo Miles!
disc=1/2
track=1/4
GENRE=Jazz
DATE=2005
REPLAYGAIN_REFERENCE_LOUDNESS=89.0 dB
REPLAYGAIN_TRACK_GAIN=-1.02 dB
REPLAYGAIN_TRACK_PEAK=1.00000000
REPLAYGAIN_ALBUM_GAIN=-2.12 dB
REPLAYGAIN_ALBUM_PEAK=1.00000000
encoder=Lavf57.83.100

+ check presence of cover.jpg, etc.
 */
