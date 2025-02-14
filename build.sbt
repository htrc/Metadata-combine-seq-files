import Dependencies.*

showCurrentGitBranch

inThisBuild(Seq(
  organization := "org.hathitrust.htrc",
  organizationName := "HathiTrust Research Center",
  organizationHomepage := Some(url("https://www.hathitrust.org/htrc")),
  scalaVersion := "2.13.14",
  scalacOptions ++= Seq(
    "-feature",
    "-deprecation",
    "-language:postfixOps",
    "-language:implicitConversions"
  ),
  resolvers ++= Seq(
    Resolver.mavenLocal,
    "HTRC Nexus Repository" at "https://nexus.htrc.illinois.edu/repository/maven-public"
  ),
  externalResolvers := Resolver.combineDefaultResolvers(resolvers.value.toVector, mavenCentral = false),
  Compile / packageBin / packageOptions += Package.ManifestAttributes(
    ("Git-Sha", git.gitHeadCommit.value.getOrElse("N/A")),
    ("Git-Branch", git.gitCurrentBranch.value),
    ("Git-Version", git.gitDescribedVersion.value.getOrElse("N/A")),
    ("Git-Dirty", git.gitUncommittedChanges.value.toString),
    ("Build-Date", new java.util.Date().toString)
  ),
  versionScheme := Some("semver-spec"),
  credentials += Credentials(
    "Sonatype Nexus Repository Manager", // realm
    "nexus.htrc.illinois.edu", // host
    "drhtrc", // user
    sys.env.getOrElse("HTRC_NEXUS_DRHTRC_PWD", "abc123") // password
  )
))

lazy val ammoniteSettings = Seq(
  libraryDependencies +=
    {
      val version = scalaBinaryVersion.value match {
        case "2.10" => "1.0.3"
        case "2.11" => "1.6.7"
        case _ ⇒  "3.0.0-M1-24-26133e66"
      }
      "com.lihaoyi" % "ammonite" % version % Test cross CrossVersion.full
    },
  Test / sourceGenerators += Def.task {
    val file = (Test / sourceManaged).value / "amm.scala"
    IO.write(file, """object amm extends App { ammonite.AmmoniteMain.main(args) }""")
    Seq(file)
  }.taskValue,
  connectInput := true,
  outputStrategy := Some(StdoutOutput)
)

lazy val `combine-seq-files` = (project in file("."))
  .enablePlugins(GitVersioning, GitBranchPrompt, JavaAppPackaging)
  .settings(ammoniteSettings)
//  .settings(spark("3.5.1"))
  .settings(spark_dev("3.5.1"))
  .settings(
    name := "combine-seq-files",
    description := "Allows the combination of two sequence files, with the latter overwriting any matching keys from the former",
    licenses += "Apache2" -> url("http://www.apache.org/licenses/LICENSE-2.0"),
    maintainer := "capitanu@illinois.edu",
    libraryDependencies ++= Seq(
      "org.rogach"                    %% "scallop"                  % "5.1.0",
      "org.hathitrust.htrc"           %% "scala-utils"              % "2.15.0",
      "org.hathitrust.htrc"           %% "spark-utils"              % "1.6.0",
      "com.github.nscala-time"        %% "nscala-time"              % "2.32.0",
      "ch.qos.logback"                %  "logback-classic"          % "1.5.6",
      "org.codehaus.janino"           %  "janino"                   % "3.1.12",
      "org.scalacheck"                %% "scalacheck"               % "1.18.0"   % Test,
      "org.scalatest"                 %% "scalatest"                % "3.2.18"   % Test,
      "org.scalatestplus"             %% "scalacheck-1-15"          % "3.2.11.0" % Test
    ),
    Universal / javaOptions ++= Seq(
      "base/java.lang", "base/java.lang.invoke", "base/java.lang.reflect", "base/java.io", "base/java.net", "base/java.nio",
      "base/java.util", "base/java.util.concurrent", "base/java.util.concurrent.atomic",
      "base/sun.nio.ch", "base/sun.nio.cs", "base/sun.security.action",
      "base/sun.util.calendar", "security.jgss/sun.security.krb5",
    ).map("-J--add-opens=java." + _ + "=ALL-UNNAMED"),
    Test / parallelExecution := false,
    Test / fork := true,
    evictionErrorLevel := Level.Info
  )
