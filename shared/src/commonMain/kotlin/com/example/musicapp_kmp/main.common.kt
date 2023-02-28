package com.example.musicapp_kmp

import androidx.compose.runtime.Composable
import com.example.musicapp_kmp.chartdetails.ChartDetailsScreen
import com.example.musicapp_kmp.chartdetails.ChartDetailsViewModel

@Composable
internal fun MainCommon() {
    MyApplicationTheme {
//        val viewModel = DashboardViewModel()
//        DashboardScreen(viewModel)
        val viewModel = ChartDetailsViewModel()
        ChartDetailsScreen(viewModel)
    }
}