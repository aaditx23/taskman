package org.aaditx23.taskman

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform