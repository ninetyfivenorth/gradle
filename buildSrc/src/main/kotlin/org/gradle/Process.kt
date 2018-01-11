@file:JvmName("Process") // To make it easier to access these functions from Groovy
package org.gradle.process

import org.gradle.api.Project
import org.gradle.internal.os.OperatingSystem
import java.io.ByteArrayOutputStream

fun Project.pkill(pid: String) {
    val killOutput = ByteArrayOutputStream()
    val result = exec {
        // KTS: The ExecSpec properties in these block are red in IDEA due to
        // https://github.com/gradle/kotlin-dsl/issues/476
        commandLine = if (isWindows) {
            listOf("taskkill.exe", "/F", "/T", "/PID", pid)
        } else {
            listOf("kill", pid)
        }
        standardOutput = killOutput
        errorOutput = killOutput
        isIgnoreExitValue = true
    }
    if (result.exitValue != 0) {
        val out = killOutput.toString()
        if (!out.contains("No such process")) {
            logger.warn(
                """Failed to kill daemon process $pid. Maybe already killed?
Output: $killOutput
""")
        }
    }
}

val isWindows get() = OperatingSystem.current().isWindows

