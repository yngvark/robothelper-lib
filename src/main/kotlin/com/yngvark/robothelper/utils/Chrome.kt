package utils

import com.yngvark.robothelper.RobotHelper

class Chrome(private val robot:RobotHelper, private val profile:String) {
    fun open(webpage: String) {
        robot.run(listOf(
            "/usr/bin/google-chrome-stable",
            "--disable-gpu",
            "--profile-directory=$profile",
            webpage
        ))

    }
}