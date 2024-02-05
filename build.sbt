import uk.gov.hmrc.DefaultBuildSettings.integrationTestSettings
//import sbt.Tests.{Group, SubProcess}
import sbt._

val appName = "integration-catalogue-frontend"

ThisBuild / scalaVersion := "2.13.12"
ThisBuild / majorVersion := 0

lazy val microservice = (project in file("."))
  .enablePlugins(play.sbt.PlayScala, SbtAutoBuildPlugin, SbtDistributablesPlugin)
  .settings(
    scalaVersion := "2.13.12",
    routesImport                      += "uk.gov.hmrc.integrationcataloguefrontend.controllers.binders._",
    Test / unmanagedSourceDirectories += baseDirectory(_ / "test-common").value,
    libraryDependencies              ++= AppDependencies.compile ++ AppDependencies.test,
    TwirlKeys.templateImports ++= Seq(
      "play.twirl.api.HtmlFormat",
      "uk.gov.hmrc.integrationcataloguefrontend.config.AppConfig",
      "uk.gov.hmrc.govukfrontend.views.html.components._",
      "uk.gov.hmrc.hmrcfrontend.views.html.components._"
    ),
    scalacOptions += "-Wconf:cat=deprecation:ws,cat=feature:ws,cat=optimizer:ws,src=target/.*:s"
  )
  .settings(scoverageSettings)
  .configs(IntegrationTest)
  .settings(integrationTestSettings(): _*)
  .settings(
    Defaults.itSettings,
    IntegrationTest / Keys.fork := false,
    IntegrationTest / parallelExecution := false,
    IntegrationTest / unmanagedSourceDirectories += baseDirectory(_ / "test-common").value,
    IntegrationTest / unmanagedSourceDirectories += baseDirectory(_ / "it").value,
    IntegrationTest / unmanagedResourceDirectories += baseDirectory(_ / "it" / "resources").value,
    IntegrationTest / managedClasspath += (Assets / packageBin).value
  )
  .settings(headerSettings(IntegrationTest) ++ automateHeaderSettings(IntegrationTest),
//    scalafixConfigSettings(IntegrationTest)
  )

//  .configs(ComponentTest)
//  .settings(inConfig(ComponentTest)(Defaults.testSettings): _*)
//  .settings(
//    ComponentTest / testOptions := Seq(Tests.Argument(TestFrameworks.ScalaTest, "-eT")),
//    ComponentTest / unmanagedSourceDirectories ++= Seq(baseDirectory.value / "component", baseDirectory.value / "test-common"),
//    ComponentTest / unmanagedResourceDirectories += baseDirectory.value / "test",
//    ComponentTest / unmanagedResourceDirectories += baseDirectory.value / "target" / "web" / "public" / "test",
//    ComponentTest / testOptions += Tests.Setup(() => System.setProperty("javascript.enabled", "true")),
//    ComponentTest / testGrouping := oneForkedJvmPerTest((ComponentTest / definedTests).value),
//    ComponentTest / parallelExecution := false
//  )
//  .settings(headerSettings(ComponentTest) ++ automateHeaderSettings(ComponentTest),
////    scalafixConfigSettings(ComponentTest)
//  )
  .disablePlugins(sbt.plugins.JUnitXmlReportPlugin)

  lazy val scoverageSettings = {
    import scoverage.ScoverageKeys
    Seq(
      // Semicolon-separated list of regexs matching classes to exclude
      ScoverageKeys.coverageExcludedPackages := ";.*\\.domain\\.models\\..*;uk\\.gov\\.hmrc\\.BuildInfo;.*\\.Routes;.*\\.RoutesPrefix;;Module;GraphiteStartUp;.*\\.Reverse[^.]*",
      ScoverageKeys.coverageMinimumStmtTotal := 94,
      ScoverageKeys.coverageMinimumBranchTotal := 85,
      ScoverageKeys.coverageFailOnMinimum := true,
      ScoverageKeys.coverageHighlighting := true,
      Test / parallelExecution := false
  )
}

//def oneForkedJvmPerTest(tests: Seq[TestDefinition]): Seq[Group] =
//  tests map { test =>
//    Group(
//      test.name,
//      Seq(test),
//      SubProcess(
//        ForkOptions().withRunJVMOptions(Vector(s"-Dtest.name=${test.name}"))
//      )
//    )
//  }
