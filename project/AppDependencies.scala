import play.core.PlayVersion.current
import play.sbt.PlayImport._
import sbt.Keys.libraryDependencies
import sbt._

object AppDependencies {
  lazy val scalaCheckVersion = "1.17.0"
  lazy val enumeratumVersion = "1.8.0"
  lazy val jacksonVersion = "2.16.1"
  lazy val bootstrapVersion = "8.4.0"
  lazy val playFrontendVersion = "8.3.0"

  val compile = Seq(
    "uk.gov.hmrc"                       %% "bootstrap-frontend-play-30"     % bootstrapVersion,
    "uk.gov.hmrc"                       %% "play-frontend-hmrc-play-30"     % playFrontendVersion excludeAll(ExclusionRule("uk.gov.hmrc","url-builder_2.12")),
    "com.fasterxml.jackson.module"      %% "jackson-module-scala"           % jacksonVersion,
    "com.fasterxml.jackson.core"        % "jackson-annotations"             % jacksonVersion,
    "com.fasterxml.jackson.core"        % "jackson-databind"                % jacksonVersion,
    "com.fasterxml.jackson.core"        % "jackson-core"                    % jacksonVersion,
    "com.fasterxml.jackson.dataformat"  % "jackson-dataformat-yaml"         % jacksonVersion,
    "com.beachape"                      %% "enumeratum-play-json"           % enumeratumVersion,
    "com.typesafe.play"                 %% "play-json-joda"                 % "2.9.2",
    "org.typelevel"                     %% "cats-core"                      % "2.10.0"
  )

  val test = Seq(
    "uk.gov.hmrc"             %% "bootstrap-test-play-30"   % bootstrapVersion        % Test,
    "org.mockito"             %% "mockito-scala-scalatest"  % "1.17.30"                 % Test,
    "org.scalacheck"          %% "scalacheck"               % scalaCheckVersion       % Test
  )

  val it = Seq.empty

}
