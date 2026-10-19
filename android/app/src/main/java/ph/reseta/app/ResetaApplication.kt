package ph.reseta.app

import android.app.Application
import ph.reseta.app.di.AppContainer
import ph.reseta.app.di.DefaultAppContainer

/**
 * Custom Application class providing dependency injection container.
 */
class ResetaApplication : Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
