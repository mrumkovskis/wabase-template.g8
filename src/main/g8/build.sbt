import sbtassembly.AssemblyPlugin.autoImport.assembly
import sbtassembly.MergeStrategy
import sbtassembly.PathList

import org.mojoz.metadata.ViewDef
import org.mojoz.querease.Querease
import org.mojoz.metadata.out.DdlGenerator
import org.wabase.{AppQuerease, DefaultAppMdConventions}

import sbt.Project.inConfig
import sbt.Defaults.testSettings

import scala.collection.immutable

ThisBuild / scalaVersion := "3.8.2" // 2.13.18
ThisBuild / version      := "0.1.0-SNAPSHOT"

val wabaseVersion      = "8.0.0-RC43-SNAPSHOT"
val comSunActivationV  = "2.0.1"
val comSunMailV        = "2.0.1"

javacOptions ++= Seq("-source", "25", "-target", "25", "-Xlint")
initialize := {
  val _ = initialize.value
  val javaVersion = sys.props("java.specification.version")
  if (javaVersion != "25")
    sys.error("Java 25 is required for this project. Found " + javaVersion + " instead")
}

lazy val dependencies = Seq(
  "org.wabase"                  %% "wabase"                 % wabaseVersion,
  "org.bouncycastle"            %  "bcprov-jdk18on"         % "1.83",
  "org.bouncycastle"            %  "bcpkix-jdk18on"         % "1.83",
  "com.github.jwt-scala"        %% "jwt-core"               % "11.0.3",
  "com.github.jwt-scala"        %% "jwt-json-common"        % "11.0.3",
  "io.github.samueleresca"      %% "pekko-quartz-scheduler" % "1.3.0-pekko-1.1.x",
  "org.hsqldb"                  %  "hsqldb"                 % "2.7.4",

  "io.swagger.core.v3"          %  "swagger-jaxrs2-jakarta" % "2.2.45" exclude("jakarta.activation", "jakarta.activation-api"),

  "org.xhtmlrenderer"           %  "flying-saucer-pdf"      % "10.1.0",

  // Keep simple-java-mail: exclude other mail/activation
  "org.simplejavamail"          %  "simple-java-mail"       % "8.12.6"
    exclude("org.eclipse.angus","angus-mail")
    exclude("org.eclipse.angus","angus-activation")
    exclude("jakarta.mail","jakarta.mail-api")
    exclude("jakarta.activation","jakarta.activation-api")
    exclude("com.sun.activation","jakarta.activation"),

  // for custom data validations
  "org.graalvm.js"              %  "js"                     % "25.0.2",
  "org.graalvm.js"              %  "js-scriptengine"        % "25.0.2",

  // activation + mail implementation --> keep (com.sun.*)
  "com.sun.activation"          %  "jakarta.activation"     % comSunActivationV,
  "com.sun.mail"                %  "jakarta.mail"           % comSunMailV
)

lazy val testsDependencies = Seq(
  "org.scalatest" %% "scalatest" % "3.2.19" % Test
)

lazy val integrationTestDependencies = Seq(
  "org.wabase" %% "wabase" % wabaseVersion % Test classifier "tests"
)

lazy val commonSettings = Seq(
  scalacOptions := Seq(
    "-unchecked",
    "-deprecation",
    "-feature",
    "-encoding", "utf8"
  ),
  fork := true,
  Test / testOptions += Tests.Argument(TestFrameworks.ScalaTest, "-oDS")
)

lazy val mojozSettings = Seq(
  mojozDbNaming := identity,
  mojozDtosPackage := "dto",
  mojozDtosImports := Seq(
    "org.tresql._",
    "org.wabase.{ Dto, DtoWithId }"
  ),
  mojozShouldCompileViews := true,
  mojozMdConventions := new DefaultAppMdConventions(mojozResourceLoader.value)(),
  mojozQuerease := new AppQuerease {
    override lazy val aliasToDb           = mojozDbAliasToDb.value
    override lazy val yamlMetadata        = mojozRawYamlMetadata.value
    override lazy val typeDefs            = mojozTypeDefs.value
    override lazy val tableMetadata       = mojozTableMetadata.value
    override lazy val macrosClass         = mojozTresqlMacrosClass.value.orNull
    override lazy val resourceLoader      = mojozResourceLoader.value
    override lazy val uninheritableExtras = mojozUninheritableExtras.value
    override lazy val checkInvocations    = false
    override protected lazy val parserCacheSize = -1 // unlimited cache for compilation
  },
)

lazy val assemblySettings = Seq(
  assembly / assemblyMergeStrategy := {
    case PathList("META-INF", "versions", rest @ _*) if rest.lastOption.contains("module-info.class") =>
      MergeStrategy.discard

    case PathList("META-INF", "versions", "9", "OSGI-INF", "MANIFEST.MF") =>
      MergeStrategy.discard

    case PathList("META-INF", "versions", "11", "OSGI-INF", "MANIFEST.MF") =>
      MergeStrategy.discard  // Discard duplicates; keep the first occurrence

    case "module-info.class" | "application.conf" | "LICENSE-2.0.txt" =>
      MergeStrategy.discard

    case "reference.conf" =>
      CustomMergeStrategy("reverse-concat-of-reference-conf") { conflicts =>
        MergeStrategy.concat(conflicts.reverse)
      }

    case PathList("jakarta", "mail", _ @ _*) =>
      MergeStrategy.first
    case PathList("jakarta", "activation", _ @ _*) =>
      MergeStrategy.first

    case x if x  endsWith  "logback-test.xml"         => MergeStrategy.discard
    case x if x  endsWith  "logback-test.example.xml" => MergeStrategy.discard

    case x =>
      val oldStrategy = (assembly / assemblyMergeStrategy).value
      oldStrategy(x)
  }
)

lazy val root = (project in file("."))
  .enablePlugins(MojozPlugin, MojozGenerateSchemaPlugin)
  .settings(
    name := "$name$",
    libraryDependencies ++= dependencies ++ testsDependencies,

    // dependency overrides: force the single com.sun.* activation + mail impl
    dependencyOverrides ++= Seq(
      "com.sun.activation" % "jakarta.activation" % comSunActivationV,
      "com.sun.mail"       % "jakarta.mail"       % comSunMailV
    ),

    resolvers ++= Seq(
      "snapshots" at "https://central.sonatype.com/repository/maven-snapshots",
      "Typesafe Simple Repository" at "https://repo.typesafe.com/typesafe/simple/maven-releases",
      "MavenRepository" at "https://mvnrepository.com"
    ),

    // avoid snapshot churn in CI
    ThisBuild / updateOptions := updateOptions.value.withCachedResolution(true).withLatestSnapshots(false),

    commonSettings,
    mojozSettings,
    assemblySettings,

    mojozSchemaSqlFiles := Seq(
      (LocalRootProject / baseDirectory).value / "db" / "db-schema.sql"
    ),
    mojozSchemaSqlGenerators := Seq(
      DdlGenerator.postgresql(typeDefs = mojozTypeDefs.value)
    ),
    Compile / copyResources := {
      val webFolder = baseDirectory.value / "web"
      val mappings  = Path.selectSubpaths(webFolder, _.isFile).map {
        case (f, p) => (f, (Compile / classDirectory).value / (webFolder.getName + "/" + p.replace('\\\\', '/')))
      }
      Sync.sync(streams.value.cacheStoreFactory make "copy-web-resources")(mappings)
      (Compile / copyResources).value ++ mappings
    },
    Compile / compile := { (Compile / copyResources).value; (Compile / compile).value }, // expose tresql props, web
    Compile / mainClass := Some("org.wabase.WabaseServer")
  )

lazy val it = (project in file("src/it"))
  .dependsOn(root % "test->test;compile->compile")
  .settings(commonSettings: _*)
  .settings(
    publish / skip := true,
    libraryDependencies ++= (integrationTestDependencies ++ testsDependencies),
    Test / javaOptions := Seq("-Xmx2G"),
    Test / parallelExecution := false,
    Test / resourceDirectory := baseDirectory.value / "resources",
    Test / scalaSource := baseDirectory.value / "scala",
    // Test / testOptions += Tests.Argument(TestFrameworks.ScalaTest, "-h", name.value + "-it-report"),
    Test / fork := true,
    Test / testOptions += Tests.Argument(TestFrameworks.ScalaTest, "-oDS")
  )
