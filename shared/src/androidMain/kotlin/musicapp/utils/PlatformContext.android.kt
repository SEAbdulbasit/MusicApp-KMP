package musicapp.utils

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

actual class PlatformContext(val applicationContext: Context)


// Global variable to store the application context
private var applicationContext: Context? = null

/**
 * Initialize the platform context with the application context.
 * This should be called from the Application class or MainActivity.
 */
fun initializePlatformContext(context: Context) {
    applicationContext = context.applicationContext
}

/**
 * Get the platform context for Android.
 * This returns a PlatformContext instance that wraps the Android application context.
 */
actual fun getPlatformContext(): PlatformContext {
    // If the application context is not initialized, throw an exception
    val context = applicationContext ?: throw IllegalStateException(
        "PlatformContext not initialized. Call initializePlatformContext() first."
    )
    return PlatformContext(context)
}

/**
 * Get the platform context from a Composable function.
 * This is an alternative way to get the platform context when in a Composable scope.
 */
@Composable
fun getPlatformContextFromComposable(): PlatformContext {
    val context = LocalContext.current
    return PlatformContext(context)
}