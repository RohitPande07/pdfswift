package com.example.ui

import android.graphics.Bitmap
import android.os.ParcelFileDescriptor
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ProcessedResult
import com.example.pdf.PdfEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfViewerScreen(
  result: ProcessedResult,
  viewModel: PdfViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var pages by remember { mutableStateOf<List<Bitmap>>(emptyList()) }
  var isLoading by remember { mutableStateOf(true) }

  BackHandler {
    viewModel.navigateTo(AppScreen.Workspace(result.tool))
  }

  LaunchedEffect(result) {
    withContext(Dispatchers.IO) {
      try {
        val file = File(result.outputFilePath)
        if (file.exists()) {
          val pfd = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
          val renderer = android.graphics.pdf.PdfRenderer(pfd)
          val loadedPages = mutableListOf<Bitmap>()
          for (i in 0 until renderer.pageCount) {
            val page = renderer.openPage(i)
            val scale = 1.5f
            val w = (page.width * scale).toInt()
            val h = (page.height * scale).toInt()
            val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            page.render(bitmap, null, null, android.graphics.pdf.PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            page.close()
            loadedPages.add(bitmap)
          }
          renderer.close()
          pfd.close()
          pages = loadedPages
        }
      } catch (e: Exception) {
        e.printStackTrace()
      } finally {
        isLoading = false
      }
    }
  }

  Scaffold(
    topBar = {
      CenterAlignedTopAppBar(
        title = {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = result.outputFileName,
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              maxLines = 1
            )
            Text(
              text = "${pages.size} page(s) • ${PdfEngine.formatBytes(result.outputSizeBytes)}",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              fontSize = 11.sp
            )
          }
        },
        navigationIcon = {
          IconButton(
            onClick = { viewModel.navigateTo(AppScreen.Workspace(result.tool)) },
            modifier = Modifier.testTag("viewer_back_button")
          ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        actions = {
          IconButton(
            onClick = { viewModel.saveToDownloads(context, result) },
            modifier = Modifier.testTag("viewer_download_button")
          ) {
            Icon(Icons.Default.Download, contentDescription = "Download")
          }
          IconButton(
            onClick = { viewModel.shareResult(context, result) },
            modifier = Modifier.testTag("viewer_share_button")
          ) {
            Icon(Icons.Default.Share, contentDescription = "Share")
          }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    },
    containerColor = MaterialTheme.colorScheme.background,
    modifier = modifier.testTag("pdf_viewer_screen")
  ) { innerPadding ->
    if (isLoading) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
          Spacer(modifier = Modifier.height(12.dp))
          Text("Rendering high-res pages...", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
      }
    } else {
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        itemsIndexed(pages) { index, bitmap ->
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .background(Color(0xFFF1F5F9))
                  .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "Page ${index + 1} of ${pages.size}",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF475569)
                )
                Text(
                  text = "${bitmap.width}x${bitmap.height} px",
                  fontSize = 10.sp,
                  color = Color(0xFF94A3B8)
                )
              }
              Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Page ${index + 1}",
                modifier = Modifier.fillMaxWidth()
              )
            }
          }
        }
      }
    }
  }
}
