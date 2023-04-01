import SwiftUI
import shared

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {
    var window: UIWindow?
    private var lifecycleHolder: LifecycleHolder { LifecycleHolder() }

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        window = UIWindow(frame: UIScreen.main.bounds)
        let mainViewController = Main_iosKt.MainiOS(lifecycle: lifecycleHolder.lifecycle)
        window?.rootViewController = mainViewController
        window?.makeKeyAndVisible()
        return true
    }
}
