package com.example.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.FormatColorText
import androidx.compose.material.icons.filled.Html
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.MergeType
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Reorder
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.TextSnippet
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class ToolCategory(val title: String) {
  ALL("All Tools"),
  CONVERT_TO("Convert to PDF"),
  CONVERT_FROM("Convert from PDF"),
  ORGANIZE("Organize"),
  EDIT_OPTIMIZE("Edit & Optimize"),
  SECURITY("Security")
}

enum class PdfTool(
  val id: String,
  val title: String,
  val description: String,
  val category: ToolCategory,
  val icon: ImageVector,
  val badge: String? = null,
  val accentColor: Color = Color(0xFF2563EB),
  val isPopular: Boolean = false
) {
  IMAGE_TO_PDF(
    id = "image_to_pdf",
    title = "Image to PDF",
    description = "Convert JPG, PNG, and WebP images into a single clean PDF document.",
    category = ToolCategory.CONVERT_TO,
    icon = Icons.Default.Image,
    accentColor = Color(0xFF2563EB),
    isPopular = true
  ),
  PDF_TO_IMAGES(
    id = "pdf_to_images",
    title = "PDF to Images",
    description = "Extract and convert every PDF page into high-resolution JPG or PNG images.",
    category = ToolCategory.CONVERT_FROM,
    icon = Icons.Default.PictureAsPdf,
    accentColor = Color(0xFF0D9488),
    isPopular = true
  ),
  MERGE_PDF(
    id = "merge_pdf",
    title = "Merge PDF",
    description = "Combine multiple PDF files into one unified document in any order.",
    category = ToolCategory.ORGANIZE,
    icon = Icons.Default.MergeType,
    accentColor = Color(0xFF6366F1),
    isPopular = true
  ),
  SPLIT_PDF(
    id = "split_pdf",
    title = "Split PDF",
    description = "Extract specific page ranges or split a PDF into separate files.",
    category = ToolCategory.ORGANIZE,
    icon = Icons.Default.FolderZip,
    accentColor = Color(0xFFEC4899),
    isPopular = true
  ),
  COMPRESS_PDF(
    id = "compress_pdf",
    title = "Compress PDF",
    description = "Reduce PDF file size while retaining crystal-clear readability.",
    category = ToolCategory.EDIT_OPTIMIZE,
    icon = Icons.Default.Compress,
    accentColor = Color(0xFFF59E0B),
    isPopular = true
  ),
  ROTATE_PDF(
    id = "rotate_pdf",
    title = "Rotate PDF",
    description = "Rotate PDF pages clockwise 90°, 180°, or 270° orientation.",
    category = ToolCategory.ORGANIZE,
    icon = Icons.Default.RotateRight,
    accentColor = Color(0xFF8B5CF6)
  ),
  DELETE_PAGES(
    id = "delete_pages",
    title = "Delete Pages",
    description = "Select and remove unwanted pages from your PDF file.",
    category = ToolCategory.ORGANIZE,
    icon = Icons.Default.Delete,
    accentColor = Color(0xFFEF4444)
  ),
  REORDER_PAGES(
    id = "reorder_pages",
    title = "Reorder Pages",
    description = "Rearrange the sequence of pages in your PDF document effortlessly.",
    category = ToolCategory.ORGANIZE,
    icon = Icons.Default.Reorder,
    accentColor = Color(0xFF10B981)
  ),
  PDF_TO_TEXT(
    id = "pdf_to_text",
    title = "PDF to Text",
    description = "Extract selectable text from your PDF into a clean readable text file.",
    category = ToolCategory.CONVERT_FROM,
    icon = Icons.Default.TextSnippet,
    accentColor = Color(0xFF0284C7)
  ),
  ADD_PAGE_NUMBERS(
    id = "add_page_numbers",
    title = "Add Page Numbers",
    description = "Insert customizable page numbering at headers or footers.",
    category = ToolCategory.EDIT_OPTIMIZE,
    icon = Icons.Default.Numbers,
    accentColor = Color(0xFF3B82F6)
  ),
  ADD_WATERMARK(
    id = "add_watermark",
    title = "Add Watermark",
    description = "Stamp custom text watermarks with adjustable opacity, angle, and size.",
    category = ToolCategory.EDIT_OPTIMIZE,
    icon = Icons.Default.FormatColorText,
    accentColor = Color(0xFFD97706)
  ),
  PROTECT_PDF(
    id = "protect_pdf",
    title = "Protect PDF",
    description = "Encrypt your document with password protection and security permissions.",
    category = ToolCategory.SECURITY,
    icon = Icons.Default.Lock,
    accentColor = Color(0xFFDC2626)
  ),
  UNLOCK_PDF(
    id = "unlock_pdf",
    title = "Unlock PDF",
    description = "Remove security restrictions from authorized PDF documents you own.",
    category = ToolCategory.SECURITY,
    icon = Icons.Default.LockOpen,
    accentColor = Color(0xFF16A34A)
  ),
  HTML_TO_PDF(
    id = "html_to_pdf",
    title = "HTML to PDF",
    description = "Convert HTML code, rich formatted text, or web reports into a PDF.",
    category = ToolCategory.CONVERT_TO,
    icon = Icons.Default.Html,
    accentColor = Color(0xFF7C3AED)
  );

  companion object {
    fun fromId(id: String?): PdfTool = entries.find { it.id == id } ?: IMAGE_TO_PDF
  }
}
