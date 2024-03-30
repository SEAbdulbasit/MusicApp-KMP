//
//  LifecycleHolder.swift
//  iosApp
//
//  Created by Abdul Basit on 29/03/2024.
//

import shared

class LifecycleHolder : ObservableObject {
    let lifecycle: LifecycleRegistry
    
    init() {
        lifecycle = LifecycleRegistryKt.LifecycleRegistry()
    
        lifecycle.onCreate()
    }
    
    deinit {
        lifecycle.onDestroy()
    }
}
