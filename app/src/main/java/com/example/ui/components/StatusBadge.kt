package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.data.model.StockStatus
import com.example.ui.theme.*

@Composable
fun StockStatusBadge(
    status: StockStatus,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, borderColor) = when (status) {
        StockStatus.IN_STOCK -> Triple(SuccessGreenLight, SuccessGreenDark, SuccessGreen.copy(alpha = 0.3f))
        StockStatus.LOW_STOCK -> Triple(AccentOrangeLight, AccentOrangeDark, AccentOrange.copy(alpha = 0.4f))
        StockStatus.OUT_OF_STOCK -> Triple(DangerRedLight, DangerRedDark, DangerRed.copy(alpha = 0.4f))
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = status.label,
            style = MaterialTheme.typography.labelSmall,
            color = textColor
        )
    }
}

@Composable
fun TransactionTypeBadge(
    type: String,
    modifier: Modifier = Modifier
) {
    val isPurchase = type.equals("IN", ignoreCase = true)
    val bgColor = if (isPurchase) Color(0xFFEFF6FF) else Color(0xFFF0FDF4)
    val textColor = if (isPurchase) Color(0xFF1D4ED8) else Color(0xFF15803D)
    val borderColor = if (isPurchase) Color(0xFFBFDBFE) else Color(0xFFBBF7D0)
    val label = if (isPurchase) "Stock In (Purchase)" else "Stock Out (Sale)"

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = textColor
        )
    }
}
