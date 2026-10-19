package com.example.pdf

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.example.model.ToolInputFile
import java.io.File
import java.io.FileOutputStream

object SamplePdfGenerator {

  /**
   * Generates a sample 3-page business document PDF for testing all PDF tools.
   */
  fun getOrCreateSamplePdf(context: Context): ToolInputFile {
    val file = File(context.cacheDir, "sample_document.pdf")
    if (!file.exists() || file.length() < 100) {
      createSampleDocument(file)
    }
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    return ToolInputFile(
      uri = uri,
      name = "sample_document.pdf",
      sizeBytes = file.length(),
      pageCount = 3,
      isSample = true
    )
  }

  /**
   * Generates a second sample PDF for testing merge functionality.
   */
  fun getOrCreateSamplePdfSecond(context: Context): ToolInputFile {
    val file = File(context.cacheDir, "sample_appendix.pdf")
    if (!file.exists() || file.length() < 100) {
      createSampleAppendix(file)
    }
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    return ToolInputFile(
      uri = uri,
      name = "sample_appendix.pdf",
      sizeBytes = file.length(),
      pageCount = 2,
      isSample = true
    )
  }

  /**
   * Generates a list of sample images for testing Image to PDF.
   */
  fun getOrCreateSampleImages(context: Context): List<ToolInputFile> {
    val list = mutableListOf<ToolInputFile>()
    val colors = listOf(
      Triple("Receipt_Oct_2026.jpg", 0xFF2563EB.toInt(), "BUSINESS RECEIPT #4892\nAmount: $128.50\nVendor: Cloud Tech Store\nDate: Oct 19, 2026\nStatus: PAID"),
      Triple("Design_Mockup.png", 0xFF0D9488.toInt(), "PROJECT DESIGN MOCKUP\nFeature: PDF Tools Suite\nSpeed: Instant On-Device\nZero Uploads\nPrivacy Guaranteed"),
      Triple("ID_Document.jpg", 0xFF7C3AED.toInt(), "MEMBERSHIP VERIFICATION\nMember: Alex Morgan\nAccount Tier: Verified\nIssued: 2026-10-19\nAuthentic Record")
    )

    colors.forEachIndexed { index, (fileName, bgColor, textContent) ->
      val file = File(context.cacheDir, fileName)
      if (!file.exists() || file.length() < 100) {
        val bitmap = Bitmap.createBitmap(800, 1000, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)

        val headerPaint = Paint().apply {
          color = bgColor
          isAntiAlias = true
        }
        canvas.drawRect(0f, 0f, 800f, 180f, headerPaint)

        val titlePaint = Paint().apply {
          color = Color.WHITE
          textSize = 36f
          isFakeBoldText = true
          isAntiAlias = true
        }
        canvas.drawText("SAMPLE ASSET #${index + 1}", 40f, 105f, titlePaint)

        val textPaint = Paint().apply {
          color = Color.DKGRAY
          textSize = 28f
          isAntiAlias = true
        }

        var yPos = 260f
        textContent.split("\n").forEach { line ->
          canvas.drawText(line, 50f, yPos, textPaint)
          yPos += 50f
        }

        // Draw illustrative shape/card
        val cardPaint = Paint().apply {
          color = 0xFFF1F5F9.toInt()
          isAntiAlias = true
        }
        canvas.drawRoundRect(RectF(50f, yPos + 30f, 750f, yPos + 220f), 16f, 16f, cardPaint)

        val badgePaint = Paint().apply {
          color = bgColor
          textSize = 22f
          isAntiAlias = true
        }
        canvas.drawText("Verified Sample File Ready for Image-to-PDF Conversion", 80f, yPos + 120f, badgePaint)

        val fos = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos)
        fos.close()
      }

      val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
      list.add(
        ToolInputFile(
          uri = uri,
          name = fileName,
          sizeBytes = file.length(),
          pageCount = 1,
          isSample = true
        )
      )
    }
    return list
  }

  private fun createSampleDocument(file: File) {
    val doc = PdfDocument()
    val pageWidth = 595 // Standard A4 width in points
    val pageHeight = 842 // Standard A4 height in points

    // PAGE 1: Cover & Summary
    run {
      val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
      val page = doc.startPage(pageInfo)
      val canvas = page.canvas

      // Header Banner
      val bannerPaint = Paint().apply {
        color = 0xFF1E3A8A.toInt() // Dark Blue
      }
      canvas.drawRect(0f, 0f, pageWidth.toFloat(), 130f, bannerPaint)

      val titlePaint = Paint().apply {
        color = Color.WHITE
        textSize = 24f
        isFakeBoldText = true
        isAntiAlias = true
      }
      canvas.drawText("PDFSWIFT QUARTERLY REPORT", 40f, 65f, titlePaint)

      val subtitlePaint = Paint().apply {
        color = 0xFF93C5FD.toInt()
        textSize = 14f
        isAntiAlias = true
      }
      canvas.drawText("Document Security, Performance & On-Device Processing", 40f, 95f, subtitlePaint)

      val bodyPaint = Paint().apply {
        color = 0xFF1E293B.toInt()
        textSize = 12f
        isAntiAlias = true
      }

      val headingPaint = Paint().apply {
        color = 0xFF0F172A.toInt()
        textSize = 15f
        isFakeBoldText = true
        isAntiAlias = true
      }

      canvas.drawText("1. Executive Summary", 40f, 170f, headingPaint)
      val p1 = listOf(
        "PDFSwift provides lightning-fast PDF manipulation entirely on your device.",
        "With client-side processing, no files ever leave the local sandboxed environment.",
        "This sample document is designed to test core operations including compression,",
        "page rotation, deletion, splitting, merging, numbering, and watermarking."
      )
      var y = 200f
      p1.forEach {
        canvas.drawText(it, 40f, y, bodyPaint)
        y += 20f
      }

      canvas.drawText("2. Key Performance Benchmarks", 40f, y + 25f, headingPaint)
      y += 55f
      val stats = listOf(
        "• Image to PDF conversion: Sub-second instantaneous assembly",
        "• Document compression: 40% to 75% size reduction without visual loss",
        "• Page extraction and split: Zero quality degradation",
        "• Text extraction: Direct unicode stream parsing and export"
      )
      stats.forEach {
        canvas.drawText(it, 40f, y, bodyPaint)
        y += 24f
      }

      // Draw stylized stats card
      val cardPaint = Paint().apply {
        color = 0xFFEFF6FF.toInt()
      }
      canvas.drawRoundRect(RectF(40f, y + 20f, pageWidth - 40f, y + 140f), 12f, 12f, cardPaint)

      val statNumPaint = Paint().apply {
        color = 0xFF2563EB.toInt()
        textSize = 28f
        isFakeBoldText = true
      }
      canvas.drawText("100% PRIVATE", 65f, y + 75f, statNumPaint)
      canvas.drawText("Zero server uploads. Everything stays secure on this device.", 65f, y + 105f, bodyPaint)

      doc.finishPage(page)
    }

    // PAGE 2: Financials & Data Table
    run {
      val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 2).create()
      val page = doc.startPage(pageInfo)
      val canvas = page.canvas

      val headingPaint = Paint().apply {
        color = 0xFF0F172A.toInt()
        textSize = 18f
        isFakeBoldText = true
        isAntiAlias = true
      }
      canvas.drawText("Page 2: Financial Breakdown & Tool Usage", 40f, 60f, headingPaint)

      val linePaint = Paint().apply {
        color = 0xFFCBD5E1.toInt()
        strokeWidth = 1f
      }
      canvas.drawLine(40f, 75f, pageWidth - 40f, 75f, linePaint)

      // Table Header
      val thBg = Paint().apply { color = 0xFFF1F5F9.toInt() }
      canvas.drawRect(40f, 100f, pageWidth - 40f, 135f, thBg)

      val thText = Paint().apply {
        color = 0xFF334155.toInt()
        textSize = 11f
        isFakeBoldText = true
      }
      canvas.drawText("TOOL NAME", 55f, 122f, thText)
      canvas.drawText("SPEED", 220f, 122f, thText)
      canvas.drawText("PRIVACY TIER", 350f, 122f, thText)
      canvas.drawText("SAVINGS", 460f, 122f, thText)

      val rows = listOf(
        Triple("Image to PDF", "0.2s", "Local Sandbox"),
        Triple("PDF Compression", "0.4s", "Local Sandbox"),
        Triple("Merge & Split", "0.3s", "Local Sandbox"),
        Triple("Watermark & Stamp", "0.2s", "Local Sandbox"),
        Triple("PDF to Text", "0.1s", "Local Sandbox"),
        Triple("Page Deletion", "0.1s", "Local Sandbox")
      )

      var rowY = 160f
      val cellText = Paint().apply {
        color = 0xFF475569.toInt()
        textSize = 11f
      }
      val badgeGreen = Paint().apply {
        color = 0xFF16A34A.toInt()
        textSize = 11f
        isFakeBoldText = true
      }

      rows.forEach { (name, speed, tier) ->
        canvas.drawText(name, 55f, rowY, cellText)
        canvas.drawText(speed, 220f, rowY, cellText)
        canvas.drawText(tier, 350f, rowY, badgeGreen)
        canvas.drawText("FREE", 460f, rowY, cellText)
        canvas.drawLine(40f, rowY + 10f, pageWidth - 40f, rowY + 10f, linePaint)
        rowY += 35f
      }

      doc.finishPage(page)
    }

    // PAGE 3: Terms & Appendix
    run {
      val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 3).create()
      val page = doc.startPage(pageInfo)
      val canvas = page.canvas

      val headingPaint = Paint().apply {
        color = 0xFF0F172A.toInt()
        textSize = 18f
        isFakeBoldText = true
        isAntiAlias = true
      }
      canvas.drawText("Page 3: Privacy Policy & Final Certification", 40f, 60f, headingPaint)

      val bodyPaint = Paint().apply {
        color = 0xFF334155.toInt()
        textSize = 11f
      }

      val certLines = listOf(
        "PDFSwift strictly operates under zero-knowledge architectural principles.",
        "Files processed within this application are stored in the local application",
        "cache and are subject to immediate user disposal at any time.",
        "",
        "Authorized by: Security & Privacy Compliance Office",
        "Document Stamp: VERIFIED-AUTHENTIC-2026",
        "Valid Through: 2027-10-19"
      )

      var y = 110f
      certLines.forEach {
        canvas.drawText(it, 40f, y, bodyPaint)
        y += 24f
      }

      // Draw official stamp box
      val stampBorder = Paint().apply {
        color = 0xFFDC2626.toInt()
        style = Paint.Style.STROKE
        strokeWidth = 2f
      }
      canvas.drawRoundRect(RectF(40f, y + 40f, 260f, y + 130f), 8f, 8f, stampBorder)

      val stampText = Paint().apply {
        color = 0xFFDC2626.toInt()
        textSize = 14f
        isFakeBoldText = true
      }
      canvas.drawText("PDFSWIFT VERIFIED", 55f, y + 80f, stampText)
      val stampSub = Paint().apply {
        color = 0xFFDC2626.toInt()
        textSize = 10f
      }
      canvas.drawText("OFFICIAL SAMPLE DOCUMENT", 55f, y + 105f, stampSub)

      doc.finishPage(page)
    }

    val fos = FileOutputStream(file)
    doc.writeTo(fos)
    fos.close()
    doc.close()
  }

  private fun createSampleAppendix(file: File) {
    val doc = PdfDocument()
    val pageWidth = 595
    val pageHeight = 842

    // Page 1
    run {
      val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
      val page = doc.startPage(pageInfo)
      val canvas = page.canvas

      val bannerPaint = Paint().apply { color = 0xFF4338CA.toInt() }
      canvas.drawRect(0f, 0f, pageWidth.toFloat(), 120f, bannerPaint)

      val titlePaint = Paint().apply {
        color = Color.WHITE
        textSize = 22f
        isFakeBoldText = true
      }
      canvas.drawText("APPENDIX A - SUPPLEMENTAL DATA", 40f, 70f, titlePaint)

      val bodyPaint = Paint().apply {
        color = 0xFF334155.toInt()
        textSize = 12f
      }
      canvas.drawText("This supplemental document is used to verify multi-file merge operations.", 40f, 160f, bodyPaint)
      canvas.drawText("When merged with the main document, all pages are preserved sequentially.", 40f, 190f, bodyPaint)

      doc.finishPage(page)
    }

    // Page 2
    run {
      val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 2).create()
      val page = doc.startPage(pageInfo)
      val canvas = page.canvas

      val heading = Paint().apply {
        color = 0xFF0F172A.toInt()
        textSize = 18f
        isFakeBoldText = true
      }
      canvas.drawText("Appendix A - Page 2: Diagrams & Notes", 40f, 70f, heading)

      val body = Paint().apply {
        color = 0xFF475569.toInt()
        textSize = 12f
      }
      canvas.drawText("End of supplemental material. Ready for compilation.", 40f, 140f, body)

      doc.finishPage(page)
    }

    val fos = FileOutputStream(file)
    doc.writeTo(fos)
    fos.close()
    doc.close()
  }
}
