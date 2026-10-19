package com.example.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.graphics.BitmapFactory
import com.example.model.CompressionLevel
import com.example.model.NumberFormat
import com.example.model.NumberPosition
import com.example.model.PdfTool
import com.example.model.ProcessedResult
import com.example.model.ToolInputFile
import com.example.model.WatermarkPosition
import com.example.pdf.PdfEngine
import java.io.File

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun WorkspaceScreen(
  viewModel: PdfViewModel,
  uiState: UiState,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val tool = uiState.activeTool

  BackHandler {
    viewModel.navigateTo(AppScreen.Home)
  }

  // File pickers
  val pdfPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.OpenMultipleDocuments()
  ) { uris ->
    if (uris.isNotEmpty()) {
      viewModel.addFiles(uris, context)
    }
  }

  val imagePickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickMultipleVisualMedia()
  ) { uris ->
    if (uris.isNotEmpty()) {
      viewModel.addFiles(uris, context)
    }
  }

  Scaffold(
    topBar = {
      CenterAlignedTopAppBar(
        title = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(tool.accentColor.copy(alpha = 0.15f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(tool.icon, contentDescription = null, tint = tool.accentColor, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = tool.title,
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
          }
        },
        navigationIcon = {
          IconButton(
            onClick = { viewModel.navigateTo(AppScreen.Home) },
            modifier = Modifier.testTag("workspace_back_button")
          ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        actions = {
          OutlinedButton(
            onClick = { viewModel.loadSampleFiles(context) },
            shape = RoundedCornerShape(20.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
            modifier = Modifier.padding(end = 8.dp).testTag("load_sample_shortcut_button")
          ) {
            Text("Sample File", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    },
    containerColor = MaterialTheme.colorScheme.background,
    modifier = modifier.testTag("workspace_screen")
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // 1. Tool Header Info Card
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(tool.accentColor.copy(alpha = 0.15f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(tool.icon, contentDescription = null, tint = tool.accentColor, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = tool.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = tool.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }

      // 2. File Upload / Selection Area
      if (tool != PdfTool.HTML_TO_PDF) {
        item {
          FileUploadCard(
            tool = tool,
            files = uiState.inputFiles,
            onSelectPdfs = { pdfPickerLauncher.launch(arrayOf("application/pdf")) },
            onSelectImages = { imagePickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
            onLoadSample = { viewModel.loadSampleFiles(context) },
            onRemoveFile = { viewModel.removeFile(it) },
            onClearAll = { viewModel.clearFiles() }
          )
        }
      }

      // 3. Tool-Specific Options
      item {
        ToolOptionsCard(tool = tool, viewModel = viewModel, uiState = uiState)
      }

      // 4. Action / Processing Section
      item {
        ActionSection(
          tool = tool,
          hasFiles = uiState.inputFiles.isNotEmpty() || tool == PdfTool.HTML_TO_PDF,
          isProcessing = uiState.isProcessing,
          progress = uiState.processingProgress,
          status = uiState.processingStatus,
          onProcess = { viewModel.processCurrentTool(context) }
        )
      }

      // 5. Error Message
      if (uiState.errorMessage != null) {
        item {
          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFFECACA)))
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = Color(0xFFDC2626))
              Spacer(modifier = Modifier.width(10.dp))
              Text(
                text = uiState.errorMessage,
                color = Color(0xFFB91C1C),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
              )
            }
          }
        }
      }

      // 6. Result Section (When completed)
      if (uiState.lastResult != null) {
        item {
          ResultCard(
            result = uiState.lastResult,
            onOpenViewer = { viewModel.navigateTo(AppScreen.Viewer(uiState.lastResult)) },
            onSave = { viewModel.saveToDownloads(context, uiState.lastResult) },
            onShare = { viewModel.shareResult(context, uiState.lastResult) },
            onReset = { viewModel.clearFiles() }
          )
        }
      }
    }
  }
}

@Composable
fun FileUploadCard(
  tool: PdfTool,
  files: List<ToolInputFile>,
  onSelectPdfs: () -> Unit,
  onSelectImages: () -> Unit,
  onLoadSample: () -> Unit,
  onRemoveFile: (String) -> Unit,
  onClearAll: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    modifier = Modifier.fillMaxWidth().testTag("file_upload_card")
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = if (tool == PdfTool.IMAGE_TO_PDF) "Selected Images (${files.size})" else "Selected Documents (${files.size})",
          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )

        if (files.isNotEmpty()) {
          Text(
            text = "Clear All",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.clickable { onClearAll() }
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      if (files.isEmpty()) {
        // Empty Upload Zone
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(
              width = 1.5.dp,
              color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
              shape = RoundedCornerShape(14.dp)
            )
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.03f))
            .padding(vertical = 24.dp, horizontal = 16.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
              modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = if (tool == PdfTool.IMAGE_TO_PDF) Icons.Default.Image else Icons.Default.PictureAsPdf,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
              )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
              text = if (tool == PdfTool.IMAGE_TO_PDF) "Choose Images to Convert" else "Choose PDF to Process",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = if (tool == PdfTool.IMAGE_TO_PDF) "JPG, PNG, WebP supported" else "Standard PDF format supported",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              Button(
                onClick = if (tool == PdfTool.IMAGE_TO_PDF) onSelectImages else onSelectPdfs,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("choose_files_button")
              ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (tool == PdfTool.IMAGE_TO_PDF) "Select Images" else "Select PDF")
              }

              OutlinedButton(
                onClick = onLoadSample,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("load_sample_button")
              ) {
                Text("Try Sample")
              }
            }
          }
        }
      } else {
        // Files list
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          files.forEach { file ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .padding(10.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = if (file.name.endsWith(".pdf", ignoreCase = true)) Icons.Default.PictureAsPdf else Icons.Default.Image,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
              )
              Spacer(modifier = Modifier.width(10.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = file.name,
                  style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                  color = MaterialTheme.colorScheme.onSurface,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
                Text(
                  text = "${PdfEngine.formatBytes(file.sizeBytes)}${if (file.isSample) " • Built-in Sample" else ""}",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  fontSize = 11.sp
                )
              }
              IconButton(onClick = { onRemoveFile(file.id) }, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Close, contentDescription = "Remove file", tint = MaterialTheme.colorScheme.error)
              }
            }
          }

          // Add more button
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            OutlinedButton(
              onClick = if (tool == PdfTool.IMAGE_TO_PDF) onSelectImages else onSelectPdfs,
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.weight(1f)
            ) {
              Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Add More Files", fontSize = 12.sp)
            }
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ToolOptionsCard(
  tool: PdfTool,
  viewModel: PdfViewModel,
  uiState: UiState
) {
  Card(
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    modifier = Modifier.fillMaxWidth().testTag("tool_options_card")
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Text(
        text = "Options & Settings",
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface
      )
      Spacer(modifier = Modifier.height(12.dp))

      when (tool) {
        PdfTool.COMPRESS_PDF -> {
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            CompressionLevel.entries.forEach { level ->
              val isSelected = uiState.compressionLevel == level
              Card(
                onClick = { viewModel.setCompressionLevel(level) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                  containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                ),
                border = if (isSelected) CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary)) else null,
                modifier = Modifier.fillMaxWidth().testTag("compression_level_${level.name}")
              ) {
                Row(
                  modifier = Modifier.padding(12.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Box(
                    modifier = Modifier
                      .size(20.dp)
                      .clip(CircleShape)
                      .border(2.dp, if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline, CircleShape),
                    contentAlignment = Alignment.Center
                  ) {
                    if (isSelected) {
                      Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary))
                    }
                  }
                  Spacer(modifier = Modifier.width(12.dp))
                  Column {
                    Text(text = level.title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                    Text(text = level.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                  }
                }
              }
            }
          }
        }

        PdfTool.ROTATE_PDF -> {
          Column {
            Text("Select Rotation Angle:", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              listOf(90 to "90° CW", 180 to "180° Half", 270 to "270° CCW").forEach { (deg, label) ->
                FilterChip(
                  selected = uiState.rotationAngle == deg,
                  onClick = { viewModel.setRotationAngle(deg) },
                  label = { Text(label) },
                  shape = RoundedCornerShape(10.dp)
                )
              }
            }
          }
        }

        PdfTool.DELETE_PAGES -> {
          Column {
            Text("Tap pages to mark for deletion:", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            if (uiState.pageThumbnails.isEmpty()) {
              Text("Upload a PDF or tap 'Sample File' to preview and pick pages.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
              LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(uiState.pageThumbnails) { page ->
                  val isMarked = uiState.pagesToDelete.contains(page.pageNumber)
                  Card(
                    onClick = { viewModel.togglePageToDelete(page.pageNumber) },
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = if (isMarked) Color(0xFFFEE2E2) else MaterialTheme.colorScheme.surfaceVariant),
                    border = if (isMarked) CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFDC2626))) else null,
                    modifier = Modifier.size(90.dp, 120.dp)
                  ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                      if (page.bitmap != null) {
                        Image(
                          bitmap = page.bitmap.asImageBitmap(),
                          contentDescription = "Page ${page.pageNumber}",
                          modifier = Modifier.fillMaxSize()
                        )
                      }
                      if (isMarked) {
                        Box(
                          modifier = Modifier.fillMaxSize().background(Color(0x99DC2626)),
                          contentAlignment = Alignment.Center
                        ) {
                          Icon(Icons.Default.Delete, contentDescription = null, tint = Color.White)
                        }
                      }
                      Box(
                        modifier = Modifier
                          .align(Alignment.BottomCenter)
                          .background(Color(0xCC000000))
                          .fillMaxWidth()
                          .padding(2.dp),
                        contentAlignment = Alignment.Center
                      ) {
                        Text("Page ${page.pageNumber}", color = Color.White, fontSize = 10.sp)
                      }
                    }
                  }
                }
              }
            }
          }
        }

        PdfTool.REORDER_PAGES -> {
          Column {
            Text("Adjust page sequence:", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            if (uiState.reorderedPages.isEmpty()) {
              Text("Upload a PDF to reorder its pages.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
              uiState.reorderedPages.forEachIndexed { index, pageNum ->
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text("Position ${index + 1}: Page $pageNum", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                  Row {
                    IconButton(
                      onClick = { viewModel.movePageOrder(index, index - 1) },
                      enabled = index > 0,
                      modifier = Modifier.size(32.dp)
                    ) {
                      Icon(Icons.Default.ArrowUpward, contentDescription = "Move up", modifier = Modifier.size(18.dp))
                    }
                    IconButton(
                      onClick = { viewModel.movePageOrder(index, index + 1) },
                      enabled = index < uiState.reorderedPages.size - 1,
                      modifier = Modifier.size(32.dp)
                    ) {
                      Icon(Icons.Default.ArrowDownward, contentDescription = "Move down", modifier = Modifier.size(18.dp))
                    }
                  }
                }
              }
            }
          }
        }

        PdfTool.SPLIT_PDF -> {
          Column {
            OutlinedTextField(
              value = uiState.splitRangeText,
              onValueChange = { viewModel.setSplitRange(it) },
              label = { Text("Page Ranges to Extract") },
              placeholder = { Text("e.g. 1-2, 3") },
              modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text("Example: '1' extracts page 1. '1-3' extracts pages 1 through 3.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
          }
        }

        PdfTool.ADD_WATERMARK -> {
          Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
              value = uiState.watermarkText,
              onValueChange = { viewModel.setWatermarkText(it) },
              label = { Text("Watermark Text") },
              singleLine = true,
              modifier = Modifier.fillMaxWidth().testTag("watermark_text_input")
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              listOf("CONFIDENTIAL", "DRAFT", "SAMPLE", "COPY").forEach { quickText ->
                FilterChip(
                  selected = uiState.watermarkText == quickText,
                  onClick = { viewModel.setWatermarkText(quickText) },
                  label = { Text(quickText, fontSize = 11.sp) }
                )
              }
            }

            Column {
              Text("Opacity: ${(uiState.watermarkOpacity * 100).toInt()}%", fontSize = 12.sp)
              Slider(
                value = uiState.watermarkOpacity,
                onValueChange = { viewModel.setWatermarkOpacity(it) },
                valueRange = 0.1f..0.8f,
                colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary)
              )
            }

            Column {
              Text("Position:", fontSize = 12.sp)
              FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                WatermarkPosition.entries.forEach { pos ->
                  FilterChip(
                    selected = uiState.watermarkPosition == pos,
                    onClick = { viewModel.setWatermarkPosition(pos) },
                    label = { Text(pos.title, fontSize = 11.sp) }
                  )
                }
              }
            }
          }
        }

        PdfTool.ADD_PAGE_NUMBERS -> {
          Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Numbering Format:", fontSize = 12.sp)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              NumberFormat.entries.forEach { format ->
                FilterChip(
                  selected = uiState.pageNumberFormat == format,
                  onClick = { viewModel.setPageNumberFormat(format) },
                  label = { Text(format.title, fontSize = 11.sp) }
                )
              }
            }

            Text("Position:", fontSize = 12.sp)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              NumberPosition.entries.forEach { pos ->
                FilterChip(
                  selected = uiState.pageNumberPosition == pos,
                  onClick = { viewModel.setPageNumberPosition(pos) },
                  label = { Text(pos.title, fontSize = 11.sp) }
                )
              }
            }
          }
        }

        PdfTool.PROTECT_PDF -> {
          Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
              value = uiState.protectPassword,
              onValueChange = { viewModel.setProtectPassword(it) },
              label = { Text("Encryption Password") },
              singleLine = true,
              modifier = Modifier.fillMaxWidth().testTag("protect_password_input")
            )
            Text(
              "Enforces document locking and authorization restrictions. Remember your password to unlock.",
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        PdfTool.UNLOCK_PDF -> {
          Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
              value = uiState.unlockPassword,
              onValueChange = { viewModel.setUnlockPassword(it) },
              label = { Text("Authorization Password") },
              placeholder = { Text("Enter password to unlock") },
              singleLine = true,
              modifier = Modifier.fillMaxWidth().testTag("unlock_password_input")
            )
            Text(
              "Unlock tool only processes documents you are authorized to modify. It clears password encryption and exports a clean unencrypted copy.",
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        PdfTool.HTML_TO_PDF -> {
          Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
              value = uiState.documentTitle,
              onValueChange = { viewModel.setDocumentTitle(it) },
              label = { Text("Document Header Title") },
              modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              listOf(
                "Invoice" to "<h1>Official Invoice #2026-901</h1>\n<p>Client: ACME Corp</p>\n<hr>\n<h2>Items</h2>\n<li>PDFSwift Enterprise - $800</li>\n<li>Privacy Architecture - $400</li>\n<br>\n<p>Total: <b>$1,200 USD</b></p>",
                "Resume" to "<h1>Alex Morgan</h1>\n<p>Senior Full-Stack & Android Engineer</p>\n<hr>\n<h2>Experience</h2>\n<li>Staff Engineer at CloudSwift (2024-Present)</li>\n<li>Architected On-Device Document Engines</li>\n<h2>Skills</h2>\n<li>Kotlin, Jetpack Compose, TypeScript, Python</li>",
                "Report" to "<h1>Q4 Security Audit Report</h1>\n<p>Auditor: Zero Trust Labs</p>\n<hr>\n<h2>Findings</h2>\n<li>All PDF transformations executed in memory/sandbox</li>\n<li>No external network transmissions observed</li>\n<p>Rating: <b>PASSED (Grade A+)</b></p>"
              ).forEach { (templateName, templateContent) ->
                FilterChip(
                  selected = uiState.documentTitle == templateName,
                  onClick = {
                    viewModel.setDocumentTitle(templateName)
                    viewModel.setHtmlContent(templateContent)
                  },
                  label = { Text(templateName, fontSize = 11.sp) }
                )
              }
            }

            OutlinedTextField(
              value = uiState.htmlContent,
              onValueChange = { viewModel.setHtmlContent(it) },
              label = { Text("HTML / Formatted Content") },
              minLines = 5,
              maxLines = 8,
              modifier = Modifier.fillMaxWidth().testTag("html_content_input")
            )
          }
        }

        else -> {
          Text(
            "Default parameters configured. Tap 'Process' below to execute.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }
  }
}

@Composable
fun ActionSection(
  tool: PdfTool,
  hasFiles: Boolean,
  isProcessing: Boolean,
  progress: Float,
  status: String,
  onProcess: () -> Unit
) {
  Column(
    modifier = Modifier.fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    if (isProcessing) {
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        LinearProgressIndicator(
          progress = { progress },
          modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
          color = tool.accentColor
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
          CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = tool.accentColor)
          Spacer(modifier = Modifier.width(8.dp))
          Text(text = status, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
      }
    } else {
      Button(
        onClick = onProcess,
        enabled = hasFiles,
        colors = ButtonDefaults.buttonColors(containerColor = tool.accentColor),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("process_action_button")
      ) {
        Icon(tool.icon, contentDescription = null, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Run ${tool.title}",
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }
  }
}

@Composable
fun ResultCard(
  result: ProcessedResult,
  onOpenViewer: () -> Unit,
  onSave: () -> Unit,
  onShare: () -> Unit,
  onReset: () -> Unit
) {
  val context = LocalContext.current

  Card(
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF22C55E))),
    modifier = Modifier.fillMaxWidth().testTag("result_card")
  ) {
    Column(modifier = Modifier.padding(18.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(26.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Processing Complete!",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = Color(0xFF15803D)
          )
        }

        IconButton(onClick = onReset, modifier = Modifier.size(32.dp)) {
          Icon(Icons.Default.Refresh, contentDescription = "New conversion")
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = result.title,
        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface
      )

      if (result.note != null) {
        Text(
          text = result.note,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Stats row
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
          .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceAround
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text("File Size", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
          Text(PdfEngine.formatBytes(result.outputSizeBytes), fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text("Pages", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
          Text("${result.pageCount}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
        if (result.originalSizeBytes > 0 && result.tool == PdfTool.COMPRESS_PDF) {
          val saved = maxOf(0, (((result.originalSizeBytes - result.outputSizeBytes).toFloat() / result.originalSizeBytes) * 100).toInt())
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Reduced", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("-$saved%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
          }
        }
      }

      // If PDF to Text: show preview text box
      if (result.extractedText != null) {
        Spacer(modifier = Modifier.height(12.dp))
        Text("Extracted Text Preview:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            .padding(10.dp)
        ) {
          Text(
            text = result.extractedText,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurface
          )
        }

        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(
          onClick = {
            val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            cm.setPrimaryClip(ClipData.newPlainText("PDFSwift Extract", result.extractedText))
            Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
          },
          modifier = Modifier.fillMaxWidth()
        ) {
          Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Copy Extracted Text")
        }
      }

      // If PDF to Images: show thumbnail row
      if (result.exportedImages.isNotEmpty()) {
        Spacer(modifier = Modifier.height(12.dp))
        Text("Exported Images (${result.exportedImages.size}):", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        LazyRow(
          contentPadding = PaddingValues(vertical = 6.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          items(result.exportedImages) { imgPath ->
            val bitmap = remember(imgPath) { BitmapFactory.decodeFile(imgPath) }
            if (bitmap != null) {
              Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Exported page image",
                modifier = Modifier
                  .size(80.dp, 100.dp)
                  .clip(RoundedCornerShape(8.dp))
                  .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Action Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        if (result.tool != PdfTool.PDF_TO_TEXT && result.tool != PdfTool.PDF_TO_IMAGES) {
          Button(
            onClick = onOpenViewer,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.weight(1f).testTag("preview_result_button")
          ) {
            Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Preview", fontSize = 13.sp)
          }
        }

        Button(
          onClick = onSave,
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
          modifier = Modifier.weight(1f).testTag("download_result_button")
        ) {
          Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Download", fontSize = 13.sp)
        }

        IconButton(
          onClick = onShare,
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .testTag("share_result_button")
        ) {
          Icon(Icons.Default.Share, contentDescription = "Share", tint = MaterialTheme.colorScheme.primary)
        }
      }
    }
  }
}
