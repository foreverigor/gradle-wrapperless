///usr/bin/env jbang --quiet "$0" "$@" ; exit $?
//DEPS https://github.com/jbangdev/jbang/blob/v0.116.0/gradle/wrapper/gradle-wrapper.jar
//RUNTIME_OPTIONS -Xmx64m -Xms64m -Dorg.gradle.appname=gradlew
//JAVA 8+

import org.gradle.cli.CommandLineParser;
import org.gradle.wrapper.*;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Fully featured gradlew script based on JBang
 * Doesn't work with main branch anymore, works with old commit!
 */
public class gradlew {

  static final Class<GradleWrapperMain> WRAPPER_MAIN_CLASS = GradleWrapperMain.class;

  public static void main(String... args) throws Exception {
    WrapperExecutor proj = WrapperExecutor.forProjectDirectory(new File("").getCanonicalFile());
    Field field = proj.getClass().getDeclaredField("propertiesFile");
    field.setAccessible(true);
    File propsFile = (File) field.get(proj);
    Path wrapperJarLocation = propsFile.toPath().resolveSibling(propsFile.getName().replaceFirst("\\.properties$", ".jar"));

    Path sourceJar = Paths.get(WRAPPER_MAIN_CLASS.getProtectionDomain().getCodeSource().getLocation().toURI());
    Logger logger = createLogger(args);
    if (Files.exists(wrapperJarLocation)) {
      logger.log("wrapper jar exists");
    } else {
      logger.log("wrapper jar not found, copying into project directory..");
      Files.copy(sourceJar, wrapperJarLocation);
    }
    if (sourceJar.equals(wrapperJarLocation)) {
      GradleWrapperMain.main(args);
    } else {
      startWrapper(logger, wrapperJarLocation, args);
    }
  }

  static void startWrapper(Logger logger, Path wrapperJarLocation, String[] args) throws Exception {
    try (URLClassLoader classLoader = new URLClassLoader(new URL[]{wrapperJarLocation.toUri().toURL()}, null)) {
      logger.log("Starting gradle wrapper main");
      classLoader.loadClass(WRAPPER_MAIN_CLASS.getName())
        .getMethod("main", String[].class)
        .invoke(null, (Object) args);
    }
  }

  static Logger createLogger(String[] args) {
    //noinspection PointlessBooleanExpression
    return new Logger(new CommandLineParser() {{option("info", "debug");}}
        .allowUnknownOptions()
        .parse(args).hasAnyOption(Arrays.asList("info", "debug")) == false
    );
  }
} // class gradlew
