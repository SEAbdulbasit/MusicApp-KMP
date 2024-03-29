//
//  ContentView.swift
//  iosApp
//
//  Created by Abdul Basit on 29/03/2024.
//

import SwiftUI
import shared



//struct ContentView: View {
//     private var lifecycleHolder: LifecycleHolder { LifecycleHolder() }
//    var body: some View {
//        VStack {
//            Image(systemName: "globe")
//                .imageScale(.large)
//                .foregroundStyle(.tint)
//            Text("Hello, world!")
//        }
//        .padding()
//    }
//}


struct ComposeView: UIViewControllerRepresentable {
     private var lifecycleHolder: LifecycleHolder { LifecycleHolder() }

    func makeUIViewController(context: Context) -> UIViewController {
        Main_iosKt.MainiOS(lifecycle: lifecycleHolder.lifecycle)
//        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    var body: some View {
        ComposeView()
                .ignoresSafeArea(.keyboard) // Compose has own keyboard handler
    }
}

