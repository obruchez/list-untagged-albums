name := "list-untagged-albums"
version := "0.1"
scalaVersion := "2.13.8"

libraryDependencies += "commons-io" % "commons-io" % "2.11.0"

assembly / mainClass := Some("org.bruchez.olivier.listuntaggedalbums.ListUntaggedAlbums")

assembly / assemblyJarName := "list-untagged-albums.jar"

ThisBuild / scalafmtOnCompile := true
