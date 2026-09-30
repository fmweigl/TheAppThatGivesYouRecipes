package com.example.yetanothermealsapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform