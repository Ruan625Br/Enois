package dev.jn.enois.app

import android.app.Application
import dev.jn.enois.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class EnoisApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@EnoisApp)
            modules(appModules)
        }
    }
}