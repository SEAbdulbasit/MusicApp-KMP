//
//  LifecycleHolder.swift
//  iosApp
//
//  Created by Abdul Basit on 01/04/2023.
//  Copyright © 2023 orgName. All rights reserved.
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
