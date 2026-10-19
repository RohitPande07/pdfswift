package com.example.ui

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PdfTool
import com.example.model.ToolCategory

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
  viewModel: PdfViewModel,
  uiState: UiState,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current

  val filteredTools = remember(uiState.searchQuery, uiState.selectedCategory) {
    PdfTool.entries.filter { tool ->
      val matchesSearch = uiState.searchQuery.isBlank() ||
          tool.title.contains(uiState.searchQuery, ignoreCase = true) ||
          tool.description.contains(uiState.searchQuery, ignoreCase = true)
      val matchesCategory = uiState.selectedCategory == ToolCategory.ALL ||
          tool.category == uiState.selectedCategory
      matchesSearch && matchesCategory
    }
  }

  Scaffold(
    topBar = {
      CenterAlignedTopAppBar(
        title = {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            Box(
              modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(
                  Brush.linearGradient(
                    listOf(Color(0xFF2563EB), Color(0xFF1D4ED8))
                  )
                ),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Bolt,
                contentDescription = null,
                tint = Color(0xFFFBBF24),
                modifier = Modifier.size(22.dp)
              )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "PDFSwift",
              style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.5).sp
              ),
              color = MaterialTheme.colorScheme.onSurface
            )
          }
        },
        actions = {
          IconButton(
            onClick = { viewModel.navigateTo(AppScreen.SavedFiles) },
            modifier = Modifier.testTag("saved_files_button")
          ) {
            Icon(
              imageVector = Icons.Default.FolderOpen,
              contentDescription = "Saved Files",
              tint = MaterialTheme.colorScheme.primary
            )
          }
          IconButton(
            onClick = { viewModel.navigateTo(AppScreen.ProPricing) },
            modifier = Modifier.testTag("pro_pricing_button")
          ) {
            Icon(
              imageVector = Icons.Default.WorkspacePremium,
              contentDescription = "PDFSwift Pro",
              tint = Color(0xFFD97706)
            )
          }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    },
    containerColor = MaterialTheme.colorScheme.background,
    modifier = modifier.testTag("home_screen")
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding),
      contentPadding = PaddingValues(bottom = 32.dp)
    ) {
      // 1. Hero Section
      item {
        HeroSection(
          onTrySamplePdf = {
            viewModel.selectTool(PdfTool.COMPRESS_PDF, context)
            viewModel.loadSampleFiles(context)
          },
          onTrySampleImages = {
            viewModel.selectTool(PdfTool.IMAGE_TO_PDF, context)
            viewModel.loadSampleFiles(context)
          }
        )
      }

      // 2. Search Bar
      item {
        SearchSection(
          searchQuery = uiState.searchQuery,
          onQueryChange = { viewModel.updateSearchQuery(it) }
        )
      }

      // 3. Category Filter Chips
      item {
        CategorySection(
          selectedCategory = uiState.selectedCategory,
          onSelectCategory = { viewModel.selectCategory(it) }
        )
      }

      // 4. Popular Tools (Shown if no search query and category is ALL)
      if (uiState.searchQuery.isBlank() && uiState.selectedCategory == ToolCategory.ALL) {
        item {
          SectionHeader(title = "Popular Tools", subtitle = "Most frequently used PDF operations")
          PopularToolsRow(
            onToolClick = { viewModel.selectTool(it, context) }
          )
        }
      }

      // 5. Tools Grid
      item {
        SectionHeader(
          title = if (uiState.searchQuery.isNotBlank()) "Search Results (${filteredTools.size})" else "All PDF Tools",
          subtitle = "100% free, private, client-side processing"
        )
      }

      item {
        ToolsGrid(
          tools = filteredTools,
          onToolClick = { viewModel.selectTool(it, context) }
        )
      }

      // 6. How it works section
      item {
        HowItWorksSection()
      }

      // 7. Security & Privacy Banner
      item {
        PrivacyBanner()
      }

      // 8. SaaS Pro Tier Preview
      item {
        ProPricingCard(
          onViewPlans = { viewModel.navigateTo(AppScreen.ProPricing) }
        )
      }

      // 9. FAQ Section
      item {
        FaqSection()
      }

      // 10. Footer
      item {
        FooterSection()
      }
    }
  }
}

@Composable
fun HeroSection(
  onTrySamplePdf: () -> Unit,
  onTrySampleImages: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 8.dp)
      .clip(RoundedCornerShape(24.dp))
      .background(
        Brush.verticalGradient(
          listOf(
            Color(0xFF1E3A8A),
            Color(0xFF172554)
          )
        )
      )
      .padding(20.dp)
  ) {
    Column {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .clip(RoundedCornerShape(20.dp))
          .background(Color(0x33FFFFFF))
          .padding(horizontal = 10.dp, vertical = 4.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Security,
          contentDescription = null,
          tint = Color(0xFF93C5FD),
          modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "100% PRIVATE • ZERO CLOUD UPLOADS",
          color = Color(0xFFE0E7FF),
          fontSize = 11.sp,
          fontWeight = FontWeight.SemiBold
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      Text(
        text = "All Your PDF Tools in One Place.",
        color = Color.White,
        style = MaterialTheme.typography.headlineMedium.copy(
          fontWeight = FontWeight.ExtraBold,
          letterSpacing = (-0.5).sp
        )
      )

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = "Convert, merge, compress, and edit PDFs in seconds. Fast on-device processing directly in your browser or phone.",
        color = Color(0xFFBFDBFE),
        style = MaterialTheme.typography.bodyMedium,
        lineHeight = 20.sp
      )

      Spacer(modifier = Modifier.height(18.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Button(
          onClick = onTrySamplePdf,
          colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF2563EB),
            contentColor = Color.White
          ),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .weight(1f)
            .testTag("try_sample_pdf_button")
        ) {
          Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Try Sample PDF", fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }

        OutlinedButton(
          onClick = onTrySampleImages,
          colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color.White
          ),
          border = ButtonDefaults.outlinedButtonBorder.copy(
            brush = Brush.horizontalGradient(listOf(Color(0x66FFFFFF), Color(0x66FFFFFF)))
          ),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .weight(1f)
            .testTag("try_sample_images_button")
        ) {
          Text("Try Images to PDF", fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
      }
    }
  }
}

@Composable
fun SearchSection(
  searchQuery: String,
  onQueryChange: (String) -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 6.dp)
  ) {
    OutlinedTextField(
      value = searchQuery,
      onValueChange = onQueryChange,
      placeholder = { Text("Search PDF tools (e.g., merge, compress, watermark)...") },
      leadingIcon = {
        Icon(
          Icons.Default.Search,
          contentDescription = "Search",
          tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
      },
      trailingIcon = {
        if (searchQuery.isNotEmpty()) {
          IconButton(onClick = { onQueryChange("") }) {
            Icon(Icons.Default.Close, contentDescription = "Clear search")
          }
        }
      },
      singleLine = true,
      shape = RoundedCornerShape(16.dp),
      colors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = MaterialTheme.colorScheme.surface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
      ),
      modifier = Modifier
        .fillMaxWidth()
        .testTag("search_input")
    )
  }
}

@Composable
fun CategorySection(
  selectedCategory: ToolCategory,
  onSelectCategory: (ToolCategory) -> Unit
) {
  LazyRow(
    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    items(ToolCategory.entries) { category ->
      val isSelected = category == selectedCategory
      FilterChip(
        selected = isSelected,
        onClick = { onSelectCategory(category) },
        label = {
          Text(
            text = category.title,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 13.sp
          )
        },
        colors = FilterChipDefaults.filterChipColors(
          selectedContainerColor = MaterialTheme.colorScheme.primary,
          selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
          containerColor = MaterialTheme.colorScheme.surface,
          labelColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(12.dp)
      )
    }
  }
}

@Composable
fun SectionHeader(title: String, subtitle: String) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 8.dp)
  ) {
    Text(
      text = title,
      style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
      color = MaterialTheme.colorScheme.onBackground
    )
    Text(
      text = subtitle,
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
  }
}

@Composable
fun PopularToolsRow(
  onToolClick: (PdfTool) -> Unit
) {
  val popularTools = PdfTool.entries.filter { it.isPopular }
  LazyRow(
    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
    horizontalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    items(popularTools) { tool ->
      Card(
        onClick = { onToolClick(tool) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
          .width(180.dp)
          .height(130.dp)
          .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
          .testTag("popular_tool_${tool.id}")
      ) {
        Column(
          modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
          verticalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(tool.accentColor.copy(alpha = 0.15f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = tool.icon,
                contentDescription = null,
                tint = tool.accentColor,
                modifier = Modifier.size(20.dp)
              )
            }
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFFEF3C7))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text("Popular", color = Color(0xFFB45309), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
          }

          Column {
            Text(
              text = tool.title,
              style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = tool.description,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              maxLines = 2,
              overflow = TextOverflow.Ellipsis,
              fontSize = 11.sp
            )
          }
        }
      }
    }
  }
}

@Composable
fun ToolsGrid(
  tools: List<PdfTool>,
  onToolClick: (PdfTool) -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    tools.chunked(2).forEach { rowTools ->
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        rowTools.forEach { tool ->
          Card(
            onClick = { onToolClick(tool) },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier
              .weight(1f)
              .height(145.dp)
              .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
              .testTag("tool_card_${tool.id}")
          ) {
            Column(
              modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
              verticalArrangement = Arrangement.SpaceBetween
            ) {
              Box(
                modifier = Modifier
                  .size(38.dp)
                  .clip(RoundedCornerShape(10.dp))
                  .background(tool.accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = tool.icon,
                  contentDescription = null,
                  tint = tool.accentColor,
                  modifier = Modifier.size(22.dp)
                )
              }

              Column {
                Text(
                  text = tool.title,
                  style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.onSurface,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                  text = tool.description,
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  maxLines = 2,
                  overflow = TextOverflow.Ellipsis,
                  fontSize = 11.sp,
                  lineHeight = 15.sp
                )
              }
            }
          }
        }
        if (rowTools.size == 1) {
          Spacer(modifier = Modifier.weight(1f))
        }
      }
    }
  }
}

@Composable
fun HowItWorksSection() {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(16.dp),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
  ) {
    Column(modifier = Modifier.padding(18.dp)) {
      Text(
        text = "How It Works",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface
      )
      Spacer(modifier = Modifier.height(14.dp))

      val steps = listOf(
        Triple("1", "Select a Tool", "Choose from 14 high-performance PDF tools."),
        Triple("2", "Pick or Test Files", "Import your documents or tap our 1-click sample files."),
        Triple("3", "Instant Download", "Process right on your device and save or share immediately.")
      )

      steps.forEach { (num, title, desc) ->
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(28.dp)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = num,
              color = MaterialTheme.colorScheme.primary,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            )
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(
              text = title,
              style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = desc,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }
    }
  }
}

@Composable
fun PrivacyBanner() {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 6.dp),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(Color(0xFFBBF7D0), Color(0xFFBBF7D0))))
  ) {
    Row(
      modifier = Modifier.padding(16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(40.dp)
          .clip(CircleShape)
          .background(Color(0xFFDCFCE7)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Lock,
          contentDescription = null,
          tint = Color(0xFF16A34A),
          modifier = Modifier.size(22.dp)
        )
      }
      Spacer(modifier = Modifier.width(14.dp))
      Column {
        Text(
          text = "Privacy First: 100% On-Device",
          style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
          color = Color(0xFF166534)
        )
        Text(
          text = "Your documents are never sent to external servers or cloud queues. All rendering and manipulation happens strictly in the local sandboxed environment.",
          style = MaterialTheme.typography.bodySmall,
          color = Color(0xFF15803D),
          fontSize = 11.sp
        )
      }
    }
  }
}

@Composable
fun ProPricingCard(onViewPlans: () -> Unit) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(16.dp),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(Color(0xFFF59E0B), Color(0xFFD97706))))
  ) {
    Column(modifier = Modifier.padding(18.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = Color(0xFFD97706))
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "PDFSwift Pro Tier",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
        }
        Text(
          text = "$4.99/mo",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
          color = Color(0xFFD97706)
        )
      }

      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = "All core PDF tools are 100% free forever. Upgrade to Pro for unlimited batch processing, custom branding, and extreme compression algorithms.",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      Spacer(modifier = Modifier.height(14.dp))
      Button(
        onClick = onViewPlans,
        colors = ButtonDefaults.buttonColors(
          containerColor = Color(0xFFD97706),
          contentColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Text("Compare Free vs Pro Plans", fontWeight = FontWeight.Bold)
      }
    }
  }
}

@Composable
fun FaqSection() {
  var expandedIndex by remember { mutableStateOf<Int?>(null) }
  val faqs = listOf(
    Pair("Is PDFSwift really free?", "Yes! All 14 core tools including Image to PDF, Merge, Split, Compress, Watermark, and Page deletion are completely free with zero hidden paywalls."),
    Pair("Are my files uploaded to any servers?", "No. Unlike other PDF websites that upload your files to remote cloud servers, PDFSwift runs 100% on-device using Android's native PdfRenderer and PdfDocument. Your files never leave this device."),
    Pair("What file types are supported for Image to PDF?", "You can convert JPG, JPEG, PNG, and WebP images. They are automatically scaled and fitted into high-quality A4 pages."),
    Pair("How does PDF compression work?", "PDFSwift re-samples page bitmaps with intelligent DPI scaling and JPEG quantization, reducing file size by up to 75% without compromising document legibility.")
  )

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 12.dp)
  ) {
    Text(
      text = "Frequently Asked Questions",
      style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
      color = MaterialTheme.colorScheme.onSurface
    )
    Spacer(modifier = Modifier.height(10.dp))

    faqs.forEachIndexed { index, (q, a) ->
      val isExpanded = expandedIndex == index
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp)
          .clickable { expandedIndex = if (isExpanded) null else index },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = q,
              style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
              color = MaterialTheme.colorScheme.onSurface,
              modifier = Modifier.weight(1f)
            )
            Icon(
              imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          AnimatedVisibility(visible = isExpanded) {
            Column {
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = a,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun FooterSection() {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(top = 24.dp, bottom = 12.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text(
      text = "PDFSwift v1.0 • Modern All-in-One PDF Suite",
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      fontWeight = FontWeight.Medium
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
      text = "Privacy Protected • Zero-Knowledge Architecture",
      style = MaterialTheme.typography.labelSmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
    )
  }
}
