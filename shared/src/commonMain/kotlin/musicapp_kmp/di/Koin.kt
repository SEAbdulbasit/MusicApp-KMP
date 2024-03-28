package musicapp_kmp.di

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(platformModule(), commonModule())
    }

fun KoinApplication.Companion.start(): KoinApplication = initKoin { }

fun initKoin() {
    KoinApplication.start()
}


