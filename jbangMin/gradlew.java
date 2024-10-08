///usr/bin/env jbang --quiet "$0" "$@" ; exit $?
//DEPS https://github.com/jbangdev/jbang/blob/main/gradle/wrapper/gradle-wrapper.jar
//RUNTIME_OPTIONS -Xmx64m -Xms64m -Dorg.gradle.appname=gradlew
//JAVA 8+

import org.gradle.wrapper.GradleWrapperMain;

import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Minimal java 8 compatible gradle wrapper script based on JBang
 */
public class gradlew {

  public static void main(String... args) throws Exception {
    Path gradleWrapper = Paths.get("gradle/wrapper/gradle-wrapper.jar");
    if (!Files.exists(gradleWrapper)) {
      Files.copy(Paths.get(GradleWrapperMain.class.getProtectionDomain().getCodeSource().getLocation().toURI()), gradleWrapper);
    }
    try (URLClassLoader classLoader = new URLClassLoader(new URL[]{gradleWrapper.toUri().toURL()}, null)) {
      classLoader.loadClass(GradleWrapperMain.class.getName())
        .getMethod("main", String[].class)
        .invoke(null, (Object) args);
    }
  }
} // class gradlew
