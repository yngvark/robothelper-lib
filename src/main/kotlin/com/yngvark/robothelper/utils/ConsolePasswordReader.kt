package utils

import com.yngvark.robothelper.SomewhatSecureString
import java.io.Console

class ConsolePasswordReader {
    fun read(): SomewhatSecureString {
        when (val console : Console? = System.console()) {
            null -> {
                throw RuntimeException("Not connected to console. Exiting")
            }
            else -> {
                val pw:CharArray = console.readPassword("Enter your password => ")
                return SomewhatSecureString(pw)
            }
        }
    }
}