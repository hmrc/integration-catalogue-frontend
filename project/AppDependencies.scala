import play.core.PlayVersion.current
import play.sbt.PlayImport._
import sbt.Keys.libraryDependencies
import sbt._

object AppDependencies {
  lazy val scalaCheckVersion = "1.14.0"
  lazy val enumeratumVersion = "1.6.3"
  lazy val jacksonVersion = "2.12.2"
  lazy val bootstrapVersion = "7.12.0"
  lazy val playFrontendVersion = "3.20.0-play-28"
  lazy val cucumberVersion = "6.2.2"
  lazy val seleniumVersion = "2.53.1"


  val compile = Seq(
    "uk.gov.hmrc"                       %% "bootstrap-frontend-play-28"     % bootstrapVersion,
    "uk.gov.hmrc"                       %% "play-frontend-hmrc"             % playFrontendVersion,
    "com.fasterxml.jackson.module"      %% "jackson-module-scala"           % jacksonVersion,
    "com.fasterxml.jackson.core"        % "jackson-annotations"             % jacksonVersion,
    "com.fasterxml.jackson.core"        % "jackson-databind"                % jacksonVersion,
    "com.fasterxml.jackson.core"        % "jackson-core"                    % jacksonVersion,
    "com.fasterxml.jackson.dataformat"  % "jackson-dataformat-yaml"         % jacksonVersion,
    "com.beachape"                      %% "enumeratum-play-json"           % enumeratumVersion,
    "com.typesafe.play"                 %% "play-json-joda"                 % "2.9.2",
    "org.typelevel"                     %% "cats-core"                      % "2.4.2"
  )

  val test = Seq(
    "uk.gov.hmrc"             %% "bootstrap-test-play-28"   % bootstrapVersion        % "test, it",
    "org.pegdown"             %  "pegdown"                  % "1.6.0"                 % "test, it",
    "org.jsoup"               %  "jsoup"                    % "1.10.2"                % "test, component",
    "org.mockito"             %% "mockito-scala-scalatest"  % "1.7.1"                 % "test, it",
    "com.vladsch.flexmark"    %  "flexmark-all"             % "0.35.10"               % "test, it",
    "com.github.tomakehurst"  % "wiremock-jre8-standalone"  % "2.27.1"                % "test, it",
    "org.scalacheck"          %% "scalacheck"               % scalaCheckVersion       % "test, it"
  ) ++
    Seq(
      "io.cucumber"                 %% "cucumber-scala"                 % cucumberVersion,
      "io.cucumber"                 %  "cucumber-junit"                 % cucumberVersion,
      "io.cucumber"                 %  "cucumber-java8"                 % cucumberVersion,
      "org.seleniumhq.selenium"     %  "selenium-java"                  % seleniumVersion,
      "com.assertthat"              %  "selenium-shutterbug"            % "0.2"
    ).map(_ % "component")
}
