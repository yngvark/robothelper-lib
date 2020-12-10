package com.yngvark.pc_init

import com.yngvark.robothelper.RobotHelper
import org.junit.jupiter.api.Test

internal class TestVariousStuff {
    @Test
    fun testPressAndRelease() {
        val robot = RobotHelper(java.awt.Robot())
        robot.type("Hello")
    }
}