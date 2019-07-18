plugins {
  java
  id("me.champeau.gradle.jmh") version "0.5.0-rc-1"
}

val deps by extra {
  mapOf(
          "jmh" to "1.21",
          "slf4j" to "1.7.26"
  )
}

repositories {
  jcenter()
}

java {
  sourceCompatibility = JavaVersion.VERSION_11
}

dependencies {
  implementation("junit:junit:4.12")
  implementation("org.slf4j:slf4j-api:${deps["slf4j"]}")
  implementation("org.slf4j:slf4j-ext:${deps["slf4j"]}")
  implementation("org.jboss.netty:netty:3.2.10.Final")
  implementation("commons-codec:commons-codec:1.4")
  implementation("com.google.guava:guava:18.0")
  implementation("joda-time:joda-time:2.7")
  implementation("com.google.code.findbugs:jsr305:3.0.0")

  runtime("ch.qos.logback:logback-classic:0.9.26")

  /* help intellij figure out jmh*/
  implementation("org.openjdk.jmh:jmh-core:${deps["jmh"]}")
}

jmh {
  jmhVersion = deps["jmh"]
  include = listOf(".*Benchmark")
}

