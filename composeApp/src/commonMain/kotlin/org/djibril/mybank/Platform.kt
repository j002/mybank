package org.djibril.mybank

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform