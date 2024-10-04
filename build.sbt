import uk.gov.hmrc.DefaultBuildSettings

val appName = "integration-catalogue-frontend"

ThisBuild / scalaVersion := "3.5.1"
ThisBuild / majorVersion := 0

lazy val root = (project in file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .settings(
    name := appName,
    routesImport                      += "uk.gov.hmrc.integrationcataloguefrontend.controllers.binders._",
    Test / unmanagedSourceDirectories += baseDirectory(_ / "test-common").value,
    libraryDependencies              ++= AppDependencies.compile ++ AppDependencies.test,
    TwirlKeys.templateImports ++= Seq(
      "play.twirl.api.HtmlFormat",
      "uk.gov.hmrc.integrationcataloguefrontend.config.AppConfig",
      "uk.gov.hmrc.govukfrontend.views.html.components._",
      "uk.gov.hmrc.hmrcfrontend.views.html.components._"
    ),
    scalacOptions += "-Wconf:cat=deprecation:w,cat=feature:w,src=target/.*:s"
  )
  .settings(scoverageSettings)
  .settings(scalacOptions := scalacOptions.value.diff(Seq("-Wunused:all")))
  .disablePlugins(sbt.plugins.JUnitXmlReportPlugin)
  .settings(scalacOptions += "-Wconf:msg=Flag.*repeatedly:s")

  lazy val scoverageSettings = {
    import scoverage.ScoverageKeys
    Seq(
      // Semicolon-separated list of regexs matching classes to exclude
      ScoverageKeys.coverageExcludedPackages := ",.*\\.domain\\.models\\..*,uk\\.gov\\.hmrc\\.integrationcataloguefrontend\\.config\\..*,uk\\.gov\\.hmrc\\.integrationcatalogue\\.models\\..*,uk\\.gov\\.hmrc\\.BuildInfo,.*\\.Routes,.*\\.RoutesPrefix,,Module,GraphiteStartUp,.*\\.Reverse[^.]*",
      ScoverageKeys.coverageMinimumStmtTotal := 88,
      ScoverageKeys.coverageMinimumBranchTotal := 80,
      ScoverageKeys.coverageFailOnMinimum := true,
      ScoverageKeys.coverageHighlighting := true,
      Test / parallelExecution := false
  )
}

lazy val it = (project in file("it"))
  .enablePlugins(PlayScala)
  .dependsOn(root % "test->test")
  .settings(DefaultBuildSettings.itSettings())
  .settings(libraryDependencies ++= AppDependencies.it)
  .settings(scalacOptions += "-Wconf:msg=Flag.*repeatedly:s")
