package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AppScreen
import com.example.ui.HomeScreen
import com.example.ui.PdfViewModel
import com.example.ui.PdfViewerScreen
import com.example.ui.ProPricingScreen
import com.example.ui.SavedFilesScreen
import com.example.ui.WorkspaceScreen
import com.example.ui.theme.PDFSwiftTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      PDFSwiftTheme {
        PdfSwiftApp()
      }
    }
  }
}

@Composable
fun PdfSwiftApp(
  viewModel: PdfViewModel = viewModel(),
  modifier: Modifier = Modifier
) {
  val uiState by viewModel.uiState.collectAsState()

  when (val screen = uiState.currentScreen) {
    is AppScreen.Home -> {
      HomeScreen(
        viewModel = viewModel,
        uiState = uiState,
        modifier = modifier.fillMaxSize()
      )
    }

    is AppScreen.Workspace -> {
      WorkspaceScreen(
        viewModel = viewModel,
        uiState = uiState,
        modifier = modifier.fillMaxSize()
      )
    }

    is AppScreen.Viewer -> {
      PdfViewerScreen(
        result = screen.result,
        viewModel = viewModel,
        modifier = modifier.fillMaxSize()
      )
    }

    is AppScreen.SavedFiles -> {
      SavedFilesScreen(
        viewModel = viewModel,
        uiState = uiState,
        modifier = modifier.fillMaxSize()
      )
    }

    is AppScreen.ProPricing -> {
      ProPricingScreen(
        viewModel = viewModel,
        modifier = modifier.fillMaxSize()
      )
    }
  }
}
