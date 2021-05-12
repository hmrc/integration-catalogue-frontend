import play.core.PlayVersion.current
import play.sbt.PlayImport._
import sbt.Keys.libraryDependencies
import sbt._

object AppDependencies {
  lazy val scalaCheckVersion = "1.14.0"
  lazy val enumeratumVersion = "1.6.2"


  val compile = Seq(
    "uk.gov.hmrc"                       %% "bootstrap-frontend-play-27"     % "3.4.0",
    "uk.gov.hmrc"                       %% "play-frontend-hmrc"             % "0.57.0-play-27",
    "uk.gov.hmrc"                       %% "play-frontend-govuk"            % "0.69.0-play-27",
    "com.fasterxml.jackson.core"        % "jackson-annotations"             % "2.12.2",
    "com.fasterxml.jackson.core"        % "jackson-databind"                % "2.12.2",
    "com.fasterxml.jackson.dataformat"  % "jackson-dataformat-yaml"         % "2.12.2",
    "io.swagger.parser.v3"              % "swagger-parser-v3"               % "2.0.24",
    "com.beachape"                      %% "enumeratum-play-json"           % enumeratumVersion,
    "com.typesafe.play"                 %% "play-json-joda"                 % "2.9.2"
  )

  val test = Seq(
    "uk.gov.hmrc"             %% "bootstrap-test-play-27"   % "3.0.0"                 % Test,
    "org.pegdown"             %  "pegdown"                  % "1.6.0"                 % "test, it",
    "org.jsoup"               %  "jsoup"                    % "1.10.2"                % Test,
    "com.typesafe.play"       %% "play-test"                % current                 % Test,
    "org.mockito"             %% "mockito-scala-scalatest"  % "1.7.1"                 % "test, it",
    "com.vladsch.flexmark"    %  "flexmark-all"             % "0.35.10"               % "test, it",
    "org.scalatestplus.play"  %% "scalatestplus-play"       % "4.0.3"                 % "test, it",
    "com.github.tomakehurst"  % "wiremock-jre8-standalone"  % "2.27.1"                % "test, it",
    "org.scalacheck"          %% "scalacheck"               % scalaCheckVersion       % "test, it"
  )
}
