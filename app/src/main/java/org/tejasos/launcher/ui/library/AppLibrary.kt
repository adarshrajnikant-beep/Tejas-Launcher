package org.tejasos.launcher.ui.library

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import org.tejasos.launcher.ui.theme.shunyaGlass
import org.tejasos.launcher.viewmodel.AppModel

data class CategoryGroup(
    val title: String,
    val apps: List<AppModel>
)

@Composable
fun AppLibraryView(
    allApps: List<AppModel>,
    onAppClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // स्मार्ट ऑटो-कैटेगरी क्लासिफायर
    val categories = remember(allApps) {
        listOf(
            CategoryGroup("वित्त एवं UPI", allApps.filter { 
                it.packageName.contains("pay", true) || it.packageName.contains("bank", true) || it.name.contains("bhim", true) || it.name.contains("upi", true)
            }),
            CategoryGroup("संवाद व सोशल", allApps.filter { 
                it.packageName.contains("whatsapp", true) || it.packageName.contains("telegram", true) || it.packageName.contains("instagram", true) || it.packageName.contains("message", true)
            }),
            CategoryGroup("उपयोगिता व टूल्स", allApps.filter { 
                it.packageName.contains("settings", true) || it.packageName.contains("camera", true) || it.packageName.contains("gallery", true) || it.packageName.contains("calculator", true)
            }),
            CategoryGroup("अन्य अनुप्रयोग", allApps.takeLast(12))
        ).filter { it.apps.isNotEmpty() }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = modifier.fillMaxSize()
    ) {
        items(categories) { category ->
            CategoryCard(category = category, onAppClick = onAppClick)
        }
    }
}

@Composable
fun CategoryCard(
    category: CategoryGroup,
    onAppClick: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .shunyaGlass(cornerRadius = 26.dp)
            .padding(14.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = category.title,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            // iOS 2x2 लार्ज फोल्डर ग्रिड (शीर्ष 4 ऐप्स)
            val displayApps = category.apps.take(4)
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    displayApps.getOrNull(0)?.let { MiniFolderItem(it, onAppClick) }
                    displayApps.getOrNull(1)?.let { MiniFolderItem(it, onAppClick) }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    displayApps.getOrNull(2)?.let { MiniFolderItem(it, onAppClick) }
                    displayApps.getOrNull(3)?.let { MiniFolderItem(it, onAppClick) }
                }
            }
        }
    }
}

@Composable
fun MiniFolderItem(
    app: AppModel,
    onClick: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick(app.packageName) },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = app.icon),
            contentDescription = app.name,
            modifier = Modifier.size(42.dp)
        )
    }
}
