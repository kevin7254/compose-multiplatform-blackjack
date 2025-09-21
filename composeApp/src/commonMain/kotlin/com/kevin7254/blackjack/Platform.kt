package com.kevin7254.blackjack

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform