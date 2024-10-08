plugins {
  java
}

group = "me.foreverigor"
version = "1.0-SNAPSHOT"

subprojects {
  apply<JavaPlugin>()

  java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8

    sourceSets.main {
      java.srcDirs(projectDir.absolutePath)
    }
  }

  repositories {
  }

  dependencies {
    runtimeOnly(files("$rootDir/gradle/wrapper/gradle-wrapper.jar"))
  }
}

repositories {
  mavenCentral()
}

dependencies {

}
