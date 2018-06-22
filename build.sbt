name := "LocalStackTest"

lazy val commonSettings = Seq(
  version := "0.1.0",
  scalaVersion := "2.12.6",
  libraryDependencies ++= Seq(
    "com.typesafe" % "config" % "1.3.2",
    "junit" % "junit" % "4.12" % Test,
    "com.amazonaws" % "aws-java-sdk" % "1.11.337",
    "com.amazonaws" % "aws-java-sdk-s3" % "1.11.337",
    "com.amazonaws" % "aws-java-sdk-sqs" % "1.11.337",
    "com.amazonaws" % "aws-lambda-java-core" % "1.2.0",
    "cloud.localstack" % "localstack-utils" % "0.1.13",
    "org.scalactic" %% "scalactic" % "3.0.5",
    "org.scalatest" %% "scalatest" % "3.0.5" % "test"
  ),
  cancelable in Global := true,
  fork in run := true,
  crossPaths := false
)

lazy val lamdba = (project in file("lambda"))
  .settings(commonSettings)

lazy val server =
  (project in file(".")).settings(commonSettings)
