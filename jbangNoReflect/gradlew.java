///usr/bin/env jbang --quiet "$0" "$@" ; exit $?
//DEPS https://github.com/jbangdev/jbang/blob/main/gradle/wrapper/gradle-wrapper.jar
//RUNTIME_OPTIONS -Xmx64m -Xms64m -Dorg.gradle.appname=gradlew
//JAVA 8+

import org.gradle.cli.CommandLineParser;
import org.gradle.wrapper.GradleWrapperMain;
import org.gradle.wrapper.Logger;

import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class gradlew {

  static final Class<GradleWrapperMain> WRAPPER_MAIN_CLASS = GradleWrapperMain.class;

  public static void main(String... args) throws Exception {
    Path wrapperJarLocation = Paths.get("gradle/wrapper/gradle-wrapper.jar");

    Path sourceJar = Paths.get(WRAPPER_MAIN_CLASS.getProtectionDomain().getCodeSource().getLocation().toURI());
    Logger logger = createLogger(args);
    if (!Files.exists(wrapperJarLocation)) {
      logger.log("wrapper jar not found, copying into project directory..");
      Files.copy(sourceJar, wrapperJarLocation);
    } else {
      logger.log("wrapper jar exists");
    }
    if (sourceJar.equals(wrapperJarLocation)) { // Non-reflective case when the correct jar is already on classpath
      GradleWrapperMain.main(args);
    } else {
      startWrapper(logger, wrapperJarLocation, args);
    }
  }

  static Logger createLogger(String[] args) {
    //noinspection PointlessBooleanExpression
    return new Logger(new CommandLineParser() {{option("info", "debug");}}
                        .allowUnknownOptions()
                        .parse(args).hasOption("info") == false);
  }

  static void startWrapper(Logger logger, Path wrapperJarLocation, String[] args) throws Exception {
    try (URLClassLoader classLoader = new URLClassLoader(new URL[]{wrapperJarLocation.toUri().toURL()}, null)) {
      logger.log("Starting gradle wrapper main");
      classLoader.loadClass(WRAPPER_MAIN_CLASS.getName())
        .getMethod("main", String[].class)
        .invoke(null, (Object) args);
    }
  }
} // class gradlew
