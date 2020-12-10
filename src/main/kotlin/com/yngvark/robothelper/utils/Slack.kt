package com.yngvark.robothelper.utils

import com.yngvark.robothelper.RobotHelper

class Slack(private val robot: RobotHelper) {
    fun run() {
        ProcessPrinter.printProcess("Process: ${javaClass.simpleName}")

        robot.run(SLACK_COMMAND)
        robot.sleep(100)
    }

}
