import play.core.PlayVersion.current
import play.sbt.PlayImport._
import sbt.Keys.libraryDependencies
import sbt._

object AppDependencies {
  lazy val scalaCheckVersion = "1.18.0"
  lazy val enumeratumVersion = "1.8.0"
  lazy val jacksonVersion = "2.17.1"
  lazy val bootstrapVersion = "9.3.0"
  lazy val playFrontendVersion = "10.0.0"

  val compile = Seq(
    "uk.gov.hmrc"                       %% "bootstrap-frontend-play-30"     % bootstrapVersion,
    "uk.gov.hmrc"                       %% "play-frontend-hmrc-play-30"     % playFrontendVersion,
    "com.fasterxml.jackson.module"      %% "jackson-module-scala"           % jacksonVersion,
    "com.fasterxml.jackson.core"        % "jackson-annotations"             % jacksonVersion,
    "com.fasterxml.jackson.core"        % "jackson-databind"                % jacksonVersion,
    "com.fasterxml.jackson.core"        % "jackson-core"                    % jacksonVersion,
    "com.fasterxml.jackson.dataformat"  % "jackson-dataformat-yaml"         % jacksonVersion,
    "com.beachape"                      %% "enumeratum-play-json"           % enumeratumVersion,
    "org.typelevel"                     %% "cats-core"                      % "2.10.0"
  )

  val test = Seq(
    "uk.gov.hmrc"             %% "bootstrap-test-play-30"   % bootstrapVersion        % Test,
    "org.scalatestplus"       %% "mockito-4-11"             % "3.2.17.0"              % Test,
    "org.scalacheck"          %% "scalacheck"               % scalaCheckVersion       % Test
  )

  val it = Seq.empty

}
