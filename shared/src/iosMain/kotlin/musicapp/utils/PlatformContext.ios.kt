package musicapp.utils

import platform.UIKit.UIViewController

/**
 * Platform context for iOS.
 * This class holds the iOS UIViewController which can be used to access iOS-specific functionality.
 */
actual class PlatformContext {
    private var viewController: UIViewController? = null

    /**
     * Default constructor for PlatformContext.
     */
    constructor()

    /**
     * Constructor that takes a UIViewController.
     */
    constructor(viewController: UIViewController) {
        this.viewController = viewController
    }

    /**
     * Get the UIViewController.
     */
    fun getViewController(): UIViewController? {
        return viewController
    }

    /**
     * Set the UIViewController.
     */
    fun setViewController(viewController: UIViewController) {
        this.viewController = viewController
    }


}

// Global variable to store the platform context
private var platformContext: PlatformContext? = null

/**
 * Initialize the platform context with a UIViewController.
 * This should be called from the iOS app's entry point.
 */
fun initializePlatformContext(viewController: UIViewController) {
    platformContext = PlatformContext(viewController)
}

/**
 * Get the platform context for iOS.
 * This returns a PlatformContext instance that may contain a UIViewController.
 */
actual fun getPlatformContext(): PlatformContext {
    return platformContext ?: PlatformContext()
}
