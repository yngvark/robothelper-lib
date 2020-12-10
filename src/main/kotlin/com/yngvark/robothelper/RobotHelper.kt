package com.yngvark.robothelper

import java.awt.Robot
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import java.io.IOException
import java.util.concurrent.TimeUnit

class RobotHelper(private val robot: Robot) {
    val DEBUG_SLEEP:Long = 500
    var debugMode: Boolean = false

    // Keyboard layout: https://en.wikipedia.org/wiki/British_and_American_keyboards#/media/File:KB_United_Kingdom.svg
    private val specialChars = mapOf(
        '$' to listOfNotNull(KeyEvent.VK_SHIFT, KeyEvent.VK_4),
        '_' to listOfNotNull(KeyEvent.VK_SHIFT, KeyEvent.VK_UNDERSCORE),
        '@' to listOfNotNull(KeyEvent.VK_SHIFT, KeyEvent.VK_2),
        '"' to listOfNotNull(KeyEvent.VK_SHIFT, KeyEvent.VK_QUOTE),
        '?' to listOfNotNull(KeyEvent.VK_SHIFT, KeyEvent.VK_SLASH),
        '&' to listOfNotNull(KeyEvent.VK_SHIFT, KeyEvent.VK_7),
        '%' to listOfNotNull(KeyEvent.VK_SHIFT, KeyEvent.VK_5),
        ':' to listOfNotNull(KeyEvent.VK_SHIFT, KeyEvent.VK_COLON)
    )

    fun click(x: Int, y: Int): RobotHelper {
        robot.mouseMove(x, y)

        if (debugMode)
            sleep(DEBUG_SLEEP)

        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK)
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK)

        return this
    }

    fun click(clicks:List<Click>, pauseBeforeClick:Long, pauseAfterClick: Long = 500): RobotHelper {
        clicks.forEach {
            click(it, pauseBeforeClick, pauseAfterClick)
        }

        return this
    }

    fun click(click: Click, pauseBeforeClick:Long, pauseAfterClick:Long): RobotHelper {
        robot.mouseMove(click.x, click.y)
        sleep(pauseBeforeClick)
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK)
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK)
        sleep(pauseAfterClick)

        return this
    }

    fun type(text: String, charPause: Long = 0): RobotHelper {
        if (debugMode)
            println("Typing: $text")

        text.forEach {
            typeChar(it)

            if (charPause > 0)
                sleep(charPause)
        }
        return this
    }

    fun type(text: SomewhatSecureString): RobotHelper {
        if (debugMode)
            println("Typing (secure): ${text.asString()}")

        text.value.forEach {
            typeChar(it)
        }

        if (debugMode)
            sleep(DEBUG_SLEEP)

        return this
    }

    private fun typeChar(char:Char) {
        val keyEventsForChar:List<Int> = getKeyEventsForChar(char)

        keyEventsForChar.forEach { keyEvent -> robot.keyPress(keyEvent)}
        sleep(2)
        keyEventsForChar.reversed().forEach { keyEvent -> robot.keyRelease(keyEvent)}
        sleep(2)
    }

    private fun getKeyEventsForChar(char:Char):List<Int> {
        if (specialChars.containsKey(char)) {
            return specialChars[char] ?: error("no such key: $char")
        }

        if (char.isUpperCase()) {
            val keyEvent:Int = KeyEvent.getExtendedKeyCodeForChar(char.toInt())
            return listOf(KeyEvent.VK_SHIFT, keyEvent)
        }

        return listOf(KeyEvent.getExtendedKeyCodeForChar(char.toInt()))
    }

    fun pressAndRelease(vararg keyCodes: Int): RobotHelper {
        keyCodes.forEach { robot.keyPress(it) }
        sleep(30)
        keyCodes.reversed().forEach { robot.keyRelease(it) }
        sleep(30) // If this is 5, sometimes ALT isn't released.

        if (debugMode) {
            var report = ""
            report += "\nPressing:  "
            report += keyCodes.map { it -> KeyEvent.getKeyText(it) }.joinToString(", ")
            report += "\nReleasing: "
            report += keyCodes.reversed().map { it -> KeyEvent.getKeyText(it) }.joinToString(", ")
            println(report)

            sleep(DEBUG_SLEEP)
        }

        return this
    }

    fun enter(): RobotHelper {
        var report = ""

        report += "\nPressing:  ENTER"
        robot.keyPress(KeyEvent.VK_ENTER)
        sleep(5)

        report += "\nReleasing: ENTER"
        robot.keyRelease(KeyEvent.VK_ENTER)
        sleep(5)

        if (debugMode) {
            println(report)
            sleep(DEBUG_SLEEP)
        }

        return this
    }

    fun run(cmd: String, path: String = ""): String {
        val parts = cmd.split("\\s".toRegex())
        return run(parts, path)
    }

    fun run(cmd: List<String>, path:String = ""): String {
        println("Running: $cmd")
        try {
            val builder = ProcessBuilder(cmd)
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT)

            val oldPath = builder.environment().get("PATH")

            builder.environment().put("PATH", "${oldPath}:${path}")

            val proc = builder.start();
            proc.waitFor(1, TimeUnit.SECONDS)

            return proc.inputStream.bufferedReader().readText()
        } catch(e: IOException) {
            e.printStackTrace()
            throw RuntimeException(e)
        }
    }

    fun sleep(ms: Long): RobotHelper {
        Thread.sleep(ms)

        return this
    }

    fun getClipboardContents(): String {
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard.getData(DataFlavor.stringFlavor) as String
        if (debugMode)
            println("Getting clipboard contents: $clipboard")

        return clipboard
    }

    fun clearClipboardContents() {
        val selection = StringSelection("")
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        clipboard.setContents(selection, selection)
    }
}
