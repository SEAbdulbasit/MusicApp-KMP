package musicapp_kmp.di

import musicapp_kmp.player.PlatformContext
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single<PlatformContext> { PlatformContext(androidContext()) }
}