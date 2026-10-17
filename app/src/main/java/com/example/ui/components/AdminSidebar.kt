package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.AdminUser
import com.example.ui.NavScreen
import com.example.ui.theme.*

data class NavItem(
    val screen: NavScreen,
    val label: String,
    val icon: ImageVector,
    val badgeCount: Int = 0
)

@Composable
fun AdminSidebar(
    currentScreen: NavScreen,
    lowStockCount: Int,
    currentUser: AdminUser?,
    onNavigate: (NavScreen) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val navItems = listOf(
        NavItem(NavScreen.DASHBOARD, "Dashboard", Icons.Default.Dashboard),
        NavItem(NavScreen.PRODUCTS, "Products", Icons.Default.Inventory2),
        NavItem(NavScreen.STOCK_IN, "Stock In (Purchases)", Icons.Default.AddShoppingCart),
        NavItem(NavScreen.STOCK_OUT, "Stock Out (Sales)", Icons.Default.PointOfSale),
        NavItem(NavScreen.SUPPLIERS, "Suppliers", Icons.Default.Business),
        NavItem(NavScreen.LOW_STOCK, "Low Stock Alerts", Icons.Default.Warning, badgeCount = lowStockCount),
        NavItem(NavScreen.REPORTS, "Reports & Analytics", Icons.AutoMirrored.Filled.TrendingUp)
    )

    Column(
        modifier = modifier
            .width(260.dp)
            .fillMaxHeight()
            .background(Slate900)
            .padding(16.dp)
    ) {
        // App Branding
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(AccentOrange),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Inventory,
                    contentDescription = "StockPilot Logo",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "StockPilot",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Slate700)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "ENTERPRISE ERP",
                        style = MaterialTheme.typography.labelSmall,
                        color = Slate300
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = Slate800, thickness = 1.dp)
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "MAIN NAVIGATION",
            style = MaterialTheme.typography.labelSmall,
            color = Slate400,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )

        // Navigation links
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            navItems.forEach { item ->
                val isSelected = currentScreen == item.screen ||
                        (item.screen == NavScreen.PRODUCTS && currentScreen == NavScreen.ADD_EDIT_PRODUCT)

                val bgColor = if (isSelected) Slate700 else Color.Transparent
                val textColor = if (isSelected) Color.White else Slate300
                val iconColor = if (isSelected) AccentOrange else Slate400

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(bgColor)
                        .clickable { onNavigate(item.screen) }
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                        .testTag("nav_item_${item.screen.name.lowercase()}"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = iconColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.bodyMedium,
                        color = textColor,
                        modifier = Modifier.weight(1f)
                    )

                    if (item.badgeCount > 0) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(AccentOrange)
                                .padding(horizontal = 7.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${item.badgeCount}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        HorizontalDivider(color = Slate800, thickness = 1.dp)
        Spacer(modifier = Modifier.height(12.dp))

        // Admin User Profile footer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Slate800)
                .border(1.dp, Slate700, RoundedCornerShape(10.dp))
                .padding(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Slate600),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = currentUser?.name?.take(1)?.uppercase() ?: "A",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = currentUser?.name ?: "Administrator",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White,
                        maxLines = 1
                    )
                    Text(
                        text = currentUser?.role ?: "Admin",
                        style = MaterialTheme.typography.labelSmall,
                        color = Slate400,
                        maxLines = 1
                    )
                }
                IconButton(
                    onClick = onLogout,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("sidebar_logout_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Logout",
                        tint = Slate400,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
