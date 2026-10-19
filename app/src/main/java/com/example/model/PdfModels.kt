package com.example.model

import android.graphics.Bitmap
import android.net.Uri

data class ToolInputFile(
  val id: String = java.util.UUID.randomUUID().toString(),
  val uri: Uri,
  val name: String,
  val sizeBytes: Long,
  val pageCount: Int = 1,
  val thumbnail: Bitmap? = null,
  val isSample: Boolean = false
)

data class PdfPageItem(
  val pageNumber: Int, // 1-indexed
  val bitmap: Bitmap? = null,
  val isSelected: Boolean = true,
  val rotationDegrees: Int = 0
)

data class ProcessedResult(
  val id: String = java.util.UUID.randomUUID().toString(),
  val tool: PdfTool,
  val title: String,
  val outputFileName: String,
  val outputFilePath: String,
  val outputUri: Uri,
  val outputSizeBytes: Long,
  val originalSizeBytes: Long = 0,
  val pageCount: Int = 1,
  val exportedImages: List<String> = emptyList(), // For PDF to Images
  val extractedText: String? = null, // For PDF to Text
  val timestamp: Long = System.currentTimeMillis(),
  val note: String? = null
)

enum class CompressionLevel(val title: String, val description: String, val scale: Float, val quality: Int) {
  RECOMMENDED("Recommended", "Best balance of quality and small file size (approx. 50-70% smaller)", 1.0f, 70),
  EXTREME("Extreme Compression", "Maximum compression for emails and upload limits (approx. 70-85% smaller)", 0.75f, 45),
  HIGH_QUALITY("Less Compression", "Preserves high DPI for printing with light compression (approx. 20-35% smaller)", 1.25f, 85)
}

enum class NumberPosition(val title: String) {
  BOTTOM_CENTER("Bottom Center"),
  BOTTOM_RIGHT("Bottom Right"),
  BOTTOM_LEFT("Bottom Left"),
  TOP_CENTER("Top Center"),
  TOP_RIGHT("Top Right")
}

enum class NumberFormat(val title: String, val template: (curr: Int, total: Int) -> String) {
  PAGE_X_OF_Y("Page X of Y", { c, t -> "Page $c of $t" }),
  PAGE_X("Page X", { c, _ -> "Page $c" }),
  JUST_NUMBER("1, 2, 3...", { c, _ -> "$c" }),
  SLASH_TOTAL("X / Y", { c, t -> "$c / $t" })
}

enum class WatermarkPosition(val title: String) {
  DIAGONAL_CENTER("Diagonal Center (45°)"),
  HORIZONTAL_CENTER("Horizontal Center"),
  TOP_HEADER("Header Banner"),
  BOTTOM_FOOTER("Footer Banner")
}
