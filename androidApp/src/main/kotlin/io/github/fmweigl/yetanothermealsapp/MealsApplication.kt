package io.github.fmweigl.yetanothermealsapp

import android.app.Application
import io.github.fmweigl.yetanothermealsapp.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class MealsApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidLogger()
            androidContext(this@MealsApplication)
        }
    }
}
