package com.group_7.library_management.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.group_7.library_management.ui.borrowing.BorrowTab
import com.group_7.library_management.ui.theme.LibrarySpacing

@Composable
fun BorrowingTopBar(
    selectedTab: BorrowTab,
    onTabSelected: (BorrowTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = BorrowTab.values()

    ScrollableTabRow(
        selectedTabIndex = selectedTab.ordinal,
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary,
        edgePadding = LibrarySpacing.Medium,
        divider = {
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.5.dp)
        }
    ) {
        tabs.forEach { tab ->
            Tab(
                selected = selectedTab == tab,
                onClick = { onTabSelected(tab) },
                text = {
                    Text(
                        text = tab.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Medium,
                        modifier = Modifier.padding(vertical = LibrarySpacing.Small),
                        maxLines = 1
                    )
                },
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
