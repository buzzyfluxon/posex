package com.example.ui.screens

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.PoseTemplate
import com.example.data.PoseTemplateRepository
import com.example.ui.theme.IconCircle
import com.example.ui.theme.Pink80
import com.example.ui.theme.PillTrack
import com.example.ui.theme.iosPressAnimation
import com.example.ui.theme.iosPressAnimationSubtle

@Composable
fun TemplatesScreen(
    onNavigateBack: () -> Unit,
    onTemplateSelected: (String) -> Unit
) {
    var selectedCategory by remember { mutableStateOf(PoseTemplateRepository.categories.first()) }

    val filteredTemplates = remember(selectedCategory) {
        if (selectedCategory == "All") {
            PoseTemplateRepository.templates
        } else {
            PoseTemplateRepository.templates.filter { it.category == selectedCategory }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(top = 48.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val backInteractionSource = remember { MutableInteractionSource() }
            IconButton(
                onClick = onNavigateBack,
                interactionSource = backInteractionSource,
                modifier = Modifier.iosPressAnimation(backInteractionSource)
            ) {
                Icon(Icons.Filled.ArrowBack, "Back", tint = Color.White)
            }
            Text(
                text = "Pose Templates",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(PoseTemplateRepository.categories) { category ->
                val isSelected = category == selectedCategory
                val chipInteractionSource = remember { MutableInteractionSource() }
                Box(
                    modifier = Modifier
                        .iosPressAnimationSubtle(chipInteractionSource)
                        .clip(RoundedCornerShape(50))
                        .background(if (isSelected) Pink80 else PillTrack)
                        .clickable(
                            interactionSource = chipInteractionSource,
                            indication = LocalIndication.current
                        ) { selectedCategory = category }
                        .padding(horizontal = 18.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = category,
                        color = if (isSelected) Color.Black else Color.White.copy(alpha = 0.8f),
                        fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                        fontSize = 14.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredTemplates, key = { it.id }) { template ->
                TemplateCard(
                    template = template,
                    onClick = { onTemplateSelected(template.imageUrl) }
                )
            }
        }
    }
}

@Composable
private fun TemplateCard(
    template: PoseTemplate,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.75f)
            .iosPressAnimation(interactionSource)
            .clip(RoundedCornerShape(20.dp))
            .background(IconCircle)
            .clickable(interactionSource = interactionSource, indication = LocalIndication.current) { onClick() }
    ) {
        AsyncImage(
            model = template.imageUrl,
            contentDescription = template.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.75f)),
                        startY = 200f
                    )
                )
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp)
        ) {
            Text(
                text = template.title,
                color = Color.White,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp
            )
            Text(
                text = template.category,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 11.sp
            )
        }
    }
}
