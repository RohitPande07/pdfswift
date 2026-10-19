package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.PdfTool
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("PDFSwift", appName)
  }

  @Test
  fun `verify all 14 pdf tools are registered`() {
    assertEquals(14, PdfTool.entries.size)
    val toolIds = PdfTool.entries.map { it.id }
    assertTrue(toolIds.contains("image_to_pdf"))
    assertTrue(toolIds.contains("pdf_to_images"))
    assertTrue(toolIds.contains("merge_pdf"))
    assertTrue(toolIds.contains("split_pdf"))
    assertTrue(toolIds.contains("compress_pdf"))
    assertTrue(toolIds.contains("rotate_pdf"))
    assertTrue(toolIds.contains("delete_pages"))
    assertTrue(toolIds.contains("reorder_pages"))
    assertTrue(toolIds.contains("pdf_to_text"))
    assertTrue(toolIds.contains("add_page_numbers"))
    assertTrue(toolIds.contains("add_watermark"))
    assertTrue(toolIds.contains("protect_pdf"))
    assertTrue(toolIds.contains("unlock_pdf"))
    assertTrue(toolIds.contains("html_to_pdf"))
  }
}
