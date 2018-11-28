name := "list-untagged-albums"
version := "0.1"
scalaVersion := "2.12.6"

libraryDependencies += "commons-io" % "commons-io" % "2.6"

mainClass in assembly := Some("org.bruchez.olivier.listuntaggedalbums.ListUntaggedAlbums")

assemblyJarName in assembly := "list-untagged-albums.jar"

scalafmtOnCompile in ThisBuild := true
