package com.example.ui

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.CompressionLevel
import com.example.model.NumberFormat
import com.example.model.NumberPosition
import com.example.model.PdfPageItem
import com.example.model.PdfTool
import com.example.model.ProcessedResult
import com.example.model.ToolCategory
import com.example.model.ToolInputFile
import com.example.model.WatermarkPosition
import com.example.pdf.PdfEngine
import com.example.pdf.SamplePdfGenerator
import kotlin.math.max
import kotlin.math.min
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

sealed class AppScreen {
  object Home : AppScreen()
  data class Workspace(val tool: PdfTool) : AppScreen()
  data class Viewer(val result: ProcessedResult) : AppScreen()
  object SavedFiles : AppScreen()
  object ProPricing : AppScreen()
}

data class UiState(
  val currentScreen: AppScreen = AppScreen.Home,
  val searchQuery: String = "",
  val selectedCategory: ToolCategory = ToolCategory.ALL,
  val activeTool: PdfTool = PdfTool.IMAGE_TO_PDF,
  val inputFiles: List<ToolInputFile> = emptyList(),
  val pageThumbnails: List<PdfPageItem> = emptyList(),
  val isProcessing: Boolean = false,
  val processingProgress: Float = 0f,
  val processingStatus: String = "",
  val lastResult: ProcessedResult? = null,
  val errorMessage: String? = null,
  val savedFiles: List<ProcessedResult> = emptyList(),

  // Tool Specific Options
  val compressionLevel: CompressionLevel = CompressionLevel.RECOMMENDED,
  val rotationAngle: Int = 90,
  val pagesToDelete: Set<Int> = emptySet(),
  val reorderedPages: List<Int> = emptyList(),
  val splitRangeText: String = "1-2",
  val watermarkText: String = "CONFIDENTIAL",
  val watermarkOpacity: Float = 0.35f,
  val watermarkAngle: Float = -45f,
  val watermarkPosition: WatermarkPosition = WatermarkPosition.DIAGONAL_CENTER,
  val pageNumberFormat: NumberFormat = NumberFormat.PAGE_X_OF_Y,
  val pageNumberPosition: NumberPosition = NumberPosition.BOTTOM_CENTER,
  val protectPassword: String = "secret123",
  val unlockPassword: String = "",
  val htmlContent: String = "<h1>Invoice #2026-089</h1>\n<p>Client: Global Logistics Partners</p>\n<hr>\n<h2>Billable Items</h2>\n<li>PDF Enterprise Processing Engine - $450</li>\n<li>On-Device Zero-Leakage Privacy Security - $300</li>\n<li>Unlimited Batch Merging & Splitting - $150</li>\n<br>\n<p>Total Balance: <b>$900.00 USD</b></p>\n<p>Status: <i>Authorized & Paid</i></p>",
  val documentTitle: String = "Business Invoice"
)

class PdfViewModel : ViewModel() {

  private val _uiState = MutableStateFlow(UiState())
  val uiState: StateFlow<UiState> = _uiState.asStateFlow()

  fun navigateTo(screen: AppScreen) {
    _uiState.update { it.copy(currentScreen = screen, errorMessage = null) }
  }

  fun updateSearchQuery(query: String) {
    _uiState.update { it.copy(searchQuery = query) }
  }

  fun selectCategory(category: ToolCategory) {
    _uiState.update { it.copy(selectedCategory = category) }
  }

  fun selectTool(tool: PdfTool, context: Context? = null) {
    _uiState.update {
      it.copy(
        activeTool = tool,
        currentScreen = AppScreen.Workspace(tool),
        inputFiles = emptyList(),
        pageThumbnails = emptyList(),
        lastResult = null,
        errorMessage = null,
        pagesToDelete = emptySet()
      )
    }
  }

  fun addFiles(uris: List<Uri>, context: Context) {
    viewModelScope.launch {
      val newFiles = mutableListOf<ToolInputFile>()
      uris.forEach { uri ->
        val name = getFileNameFromUri(context, uri)
        val size = getFileSizeFromUri(context, uri)
        val inputFile = ToolInputFile(
          uri = uri,
          name = name,
          sizeBytes = size
        )
        newFiles.add(inputFile)
      }

      val updatedList = _uiState.value.inputFiles + newFiles
      _uiState.update { it.copy(inputFiles = updatedList, lastResult = null) }

      // If the active tool inspects pages (Rotate, Split, Delete, Reorder), load thumbnails
      if (updatedList.isNotEmpty() && shouldLoadThumbnails(_uiState.value.activeTool)) {
        loadThumbnailsForPdf(context, updatedList.first().uri)
      }
    }
  }

  fun loadSampleFiles(context: Context) {
    viewModelScope.launch {
      when (_uiState.value.activeTool) {
        PdfTool.IMAGE_TO_PDF -> {
          val samples = SamplePdfGenerator.getOrCreateSampleImages(context)
          _uiState.update { it.copy(inputFiles = samples, lastResult = null) }
        }
        PdfTool.MERGE_PDF -> {
          val pdf1 = SamplePdfGenerator.getOrCreateSamplePdf(context)
          val pdf2 = SamplePdfGenerator.getOrCreateSamplePdfSecond(context)
          _uiState.update { it.copy(inputFiles = listOf(pdf1, pdf2), lastResult = null) }
        }
        PdfTool.HTML_TO_PDF -> {
          // Ready with default HTML sample template
          _uiState.update { it.copy(lastResult = null) }
        }
        else -> {
          val samplePdf = SamplePdfGenerator.getOrCreateSamplePdf(context)
          _uiState.update { it.copy(inputFiles = listOf(samplePdf), lastResult = null) }
          if (shouldLoadThumbnails(_uiState.value.activeTool)) {
            loadThumbnailsForPdf(context, samplePdf.uri)
          }
        }
      }
    }
  }

  private fun shouldLoadThumbnails(tool: PdfTool): Boolean {
    return tool in listOf(
      PdfTool.SPLIT_PDF,
      PdfTool.ROTATE_PDF,
      PdfTool.DELETE_PAGES,
      PdfTool.REORDER_PAGES,
      PdfTool.PDF_TO_IMAGES
    )
  }

  private suspend fun loadThumbnailsForPdf(context: Context, uri: Uri) {
    try {
      val thumbs = PdfEngine.loadPageThumbnails(context, uri)
      val initialOrder = thumbs.map { it.pageNumber }
      _uiState.update {
        it.copy(
          pageThumbnails = thumbs,
          reorderedPages = initialOrder,
          pagesToDelete = emptySet()
        )
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  fun removeFile(id: String) {
    _uiState.update {
      val filtered = it.inputFiles.filterNot { f -> f.id == id }
      it.copy(inputFiles = filtered)
    }
  }

  fun clearFiles() {
    _uiState.update {
      it.copy(inputFiles = emptyList(), pageThumbnails = emptyList(), lastResult = null)
    }
  }

  // Option Updaters
  fun setCompressionLevel(level: CompressionLevel) = _uiState.update { it.copy(compressionLevel = level) }
  fun setRotationAngle(angle: Int) = _uiState.update { it.copy(rotationAngle = angle) }
  fun togglePageToDelete(pageNum: Int) = _uiState.update {
    val set = it.pagesToDelete.toMutableSet()
    if (set.contains(pageNum)) set.remove(pageNum) else set.add(pageNum)
    it.copy(pagesToDelete = set)
  }
  fun movePageOrder(fromIndex: Int, toIndex: Int) = _uiState.update {
    val list = it.reorderedPages.toMutableList()
    if (fromIndex in list.indices && toIndex in list.indices) {
      val item = list.removeAt(fromIndex)
      list.add(toIndex, item)
    }
    it.copy(reorderedPages = list)
  }
  fun setSplitRange(range: String) = _uiState.update { it.copy(splitRangeText = range) }
  fun setWatermarkText(text: String) = _uiState.update { it.copy(watermarkText = text) }
  fun setWatermarkOpacity(opacity: Float) = _uiState.update { it.copy(watermarkOpacity = opacity) }
  fun setWatermarkAngle(angle: Float) = _uiState.update { it.copy(watermarkAngle = angle) }
  fun setWatermarkPosition(pos: WatermarkPosition) = _uiState.update { it.copy(watermarkPosition = pos) }
  fun setPageNumberFormat(format: NumberFormat) = _uiState.update { it.copy(pageNumberFormat = format) }
  fun setPageNumberPosition(pos: NumberPosition) = _uiState.update { it.copy(pageNumberPosition = pos) }
  fun setProtectPassword(pw: String) = _uiState.update { it.copy(protectPassword = pw) }
  fun setUnlockPassword(pw: String) = _uiState.update { it.copy(unlockPassword = pw) }
  fun setHtmlContent(content: String) = _uiState.update { it.copy(htmlContent = content) }
  fun setDocumentTitle(title: String) = _uiState.update { it.copy(documentTitle = title) }

  /**
   * Primary processing execution workflow
   */
  fun processCurrentTool(context: Context) {
    val state = _uiState.value
    viewModelScope.launch {
      _uiState.update {
        it.copy(
          isProcessing = true,
          processingProgress = 0.2f,
          processingStatus = "Initializing ${state.activeTool.title}...",
          errorMessage = null
        )
      }

      try {
        delay(250)
        _uiState.update { it.copy(processingProgress = 0.5f, processingStatus = "Processing on-device engine...") }

        val result: ProcessedResult = when (state.activeTool) {
          PdfTool.IMAGE_TO_PDF -> {
            if (state.inputFiles.isEmpty()) throw IllegalArgumentException("Please select at least 1 image file.")
            PdfEngine.imageToPdf(context, state.inputFiles.map { it.uri })
          }

          PdfTool.PDF_TO_IMAGES -> {
            if (state.inputFiles.isEmpty()) throw IllegalArgumentException("Please select a PDF file.")
            PdfEngine.pdfToImages(context, state.inputFiles.first().uri)
          }

          PdfTool.MERGE_PDF -> {
            if (state.inputFiles.size < 2) throw IllegalArgumentException("Please select at least 2 PDF files to merge.")
            PdfEngine.mergePdfs(context, state.inputFiles.map { it.uri })
          }

          PdfTool.SPLIT_PDF -> {
            if (state.inputFiles.isEmpty()) throw IllegalArgumentException("Please select a PDF file.")
            val pages = parsePageRange(state.splitRangeText, state.pageThumbnails.size)
            PdfEngine.splitPdf(context, state.inputFiles.first().uri, pages)
          }

          PdfTool.COMPRESS_PDF -> {
            if (state.inputFiles.isEmpty()) throw IllegalArgumentException("Please select a PDF file.")
            PdfEngine.compressPdf(context, state.inputFiles.first().uri, state.compressionLevel)
          }

          PdfTool.ROTATE_PDF -> {
            if (state.inputFiles.isEmpty()) throw IllegalArgumentException("Please select a PDF file.")
            PdfEngine.rotatePdf(context, state.inputFiles.first().uri, state.rotationAngle)
          }

          PdfTool.DELETE_PAGES -> {
            if (state.inputFiles.isEmpty()) throw IllegalArgumentException("Please select a PDF file.")
            if (state.pagesToDelete.isEmpty()) throw IllegalArgumentException("Please select at least one page to delete.")
            PdfEngine.deletePages(context, state.inputFiles.first().uri, state.pagesToDelete)
          }

          PdfTool.REORDER_PAGES -> {
            if (state.inputFiles.isEmpty()) throw IllegalArgumentException("Please select a PDF file.")
            val order = if (state.reorderedPages.isNotEmpty()) state.reorderedPages else listOf(1)
            PdfEngine.reorderPages(context, state.inputFiles.first().uri, order)
          }

          PdfTool.PDF_TO_TEXT -> {
            if (state.inputFiles.isEmpty()) throw IllegalArgumentException("Please select a PDF file.")
            PdfEngine.pdfToText(context, state.inputFiles.first().uri)
          }

          PdfTool.ADD_PAGE_NUMBERS -> {
            if (state.inputFiles.isEmpty()) throw IllegalArgumentException("Please select a PDF file.")
            PdfEngine.addPageNumbers(
              context,
              state.inputFiles.first().uri,
              state.pageNumberFormat,
              state.pageNumberPosition
            )
          }

          PdfTool.ADD_WATERMARK -> {
            if (state.inputFiles.isEmpty()) throw IllegalArgumentException("Please select a PDF file.")
            PdfEngine.addWatermark(
              context,
              state.inputFiles.first().uri,
              text = state.watermarkText,
              opacity = state.watermarkOpacity,
              angle = state.watermarkAngle,
              position = state.watermarkPosition
            )
          }

          PdfTool.PROTECT_PDF -> {
            if (state.inputFiles.isEmpty()) throw IllegalArgumentException("Please select a PDF file.")
            PdfEngine.protectPdf(context, state.inputFiles.first().uri, state.protectPassword)
          }

          PdfTool.UNLOCK_PDF -> {
            if (state.inputFiles.isEmpty()) throw IllegalArgumentException("Please select a PDF file.")
            PdfEngine.unlockPdf(context, state.inputFiles.first().uri, state.unlockPassword)
          }

          PdfTool.HTML_TO_PDF -> {
            PdfEngine.htmlToPdf(context, state.htmlContent, state.documentTitle)
          }
        }

        _uiState.update {
          it.copy(
            isProcessing = false,
            processingProgress = 1f,
            processingStatus = "Done!",
            lastResult = result,
            savedFiles = listOf(result) + it.savedFiles
          )
        }
      } catch (e: Exception) {
        e.printStackTrace()
        _uiState.update {
          it.copy(
            isProcessing = false,
            errorMessage = e.message ?: "An unexpected error occurred during processing."
          )
        }
      }
    }
  }

  fun shareResult(context: Context, result: ProcessedResult) {
    try {
      val intent = Intent(Intent.ACTION_SEND).apply {
        type = if (result.tool == PdfTool.PDF_TO_TEXT) "text/plain" else "application/pdf"
        putExtra(Intent.EXTRA_STREAM, result.outputUri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
      }
      context.startActivity(Intent.createChooser(intent, "Share ${result.outputFileName}"))
    } catch (e: Exception) {
      Toast.makeText(context, "Could not share file: ${e.message}", Toast.LENGTH_SHORT).show()
    }
  }

  fun saveToDownloads(context: Context, result: ProcessedResult) {
    try {
      val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
      val destFile = File(downloadsDir, result.outputFileName)
      val srcFile = File(result.outputFilePath)

      if (srcFile.exists()) {
        srcFile.copyTo(destFile, overwrite = true)
        Toast.makeText(context, "Saved to Downloads: ${result.outputFileName}", Toast.LENGTH_LONG).show()
      } else {
        Toast.makeText(context, "File ready in App Cache: ${result.outputFileName}", Toast.LENGTH_SHORT).show()
      }
    } catch (e: Exception) {
      Toast.makeText(context, "Saved in App Sandbox (${result.outputFileName})", Toast.LENGTH_SHORT).show()
    }
  }

  fun deleteSavedFile(id: String) {
    _uiState.update {
      it.copy(savedFiles = it.savedFiles.filterNot { f -> f.id == id })
    }
  }

  private fun parsePageRange(text: String, totalPages: Int): List<Int> {
    val result = mutableListOf<Int>()
    val parts = text.split(",").map { it.trim() }
    parts.forEach { part ->
      if (part.contains("-")) {
        val range = part.split("-").mapNotNull { it.trim().toIntOrNull() }
        if (range.size == 2) {
          val start = max(1, range[0])
          val end = min(max(start, range[1]), if (totalPages > 0) totalPages else 100)
          for (p in start..end) {
            result.add(p)
          }
        }
      } else {
        part.toIntOrNull()?.let { result.add(it) }
      }
    }
    return if (result.isNotEmpty()) result.distinct() else listOf(1)
  }

  private fun getFileNameFromUri(context: Context, uri: Uri): String {
    var name = "document.pdf"
    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
      val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
      if (cursor.moveToFirst() && nameIndex >= 0) {
        name = cursor.getString(nameIndex) ?: name
      }
    }
    return name
  }

  private fun getFileSizeFromUri(context: Context, uri: Uri): Long {
    var size = 0L
    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
      val sizeIndex = cursor.getColumnIndex(android.provider.OpenableColumns.SIZE)
      if (cursor.moveToFirst() && sizeIndex >= 0) {
        size = cursor.getLong(sizeIndex)
      }
    }
    return size
  }
}
