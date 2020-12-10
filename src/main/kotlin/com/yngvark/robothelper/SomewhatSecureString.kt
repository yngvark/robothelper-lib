package com.yngvark.robothelper

import java.io.Closeable

/*
 * Not perfectly secure, but better than String.
 * Details
 * - https://medium.com/@_west_on/protecting-strings-in-jvm-memory-84c365f8f01c
 * - https://stackoverflow.com/questions/52983193/we-use-char-for-sensitive-data-in-java-what-about-in-kotlin-is-chararray-oka
 * - https://github.com/NovaCrypto/SecureString
*/
class SomewhatSecureString(password: CharArray) : Closeable {
    var value: CharArray = password
        private set

    private fun clear() {
        println("Clearing password")

        //This is important! We don't know when the character array
        //will get garbage collected and we don't want it to sit around
        //in memory holding the password. We can't control when the array
        //gets garbage collected, but we can overwrite the password with
        //blank spaces so that it doesn't hold the password.
        for (i in value.indices){
            value[i] = ' '
        }
    }

    override fun close() {
        clear()
    }

    fun asString():String {
        return value.joinToString("")
    }

}