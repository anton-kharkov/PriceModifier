package one.upup.clover.pricemodifier

import android.app.Application
import one.upup.clover.pricemodifier.presenter.IMainPresenter
import one.upup.clover.pricemodifier.presenter.MainPresenter
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.dsl.module

class PriceModifierApp : Application() {

    private val priceModifierModule = module {
        single<IMainPresenter> { MainPresenter() }
    }

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@PriceModifierApp)
            modules(priceModifierModule)
        }
    }
}