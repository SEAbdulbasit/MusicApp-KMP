package com.example.musicapp_kmp

import androidx.compose.runtime.Composable
import com.example.musicapp_kmp.chartdetails.ChartDetailsScreen
import com.example.musicapp_kmp.chartdetails.ChartDetailsViewModel
import com.example.musicapp_kmp.dashboard.DashboardScreen
import com.example.musicapp_kmp.dashboard.DashboardViewModel

@Composable
internal fun MainCommon() {
    MyApplicationTheme {
        val viewModel = DashboardViewModel()
        DashboardScreen(viewModel)
//        val viewModel = ChartDetailsViewModel()
//        ChartDetailsScreen(viewModel)
    }
}