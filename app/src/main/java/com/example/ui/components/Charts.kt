package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.ui.BestSellingItem
import com.example.ui.MonthlyComparison
import com.example.ui.theme.*

@Composable
fun StockValueTrendChart(
    points: List<Pair<String, Double>>,
    modifier: Modifier = Modifier
) {
    if (points.isEmpty()) return

    val maxValue = points.maxOfOrNull { it.second } ?: 1.0
    val minValue = (points.minOfOrNull { it.second } ?: 0.0) * 0.9

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceWhite)
            .border(1.dp, Slate200, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Stock Valuation Over Time",
                    style = MaterialTheme.typography.titleMedium,
                    color = Slate900
                )
                Text(
                    text = "Historical asset value valuation (USD)",
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate500
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(SuccessGreenLight)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "+8.4% Trend",
                    style = MaterialTheme.typography.labelSmall,
                    color = SuccessGreenDark
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height - 25.dp.toPx()
                val stepX = width / (points.size - 1).coerceAtLeast(1)
                val valueRange = (maxValue - minValue).coerceAtLeast(1.0)

                // Grid lines
                val gridLines = 4
                for (i in 0..gridLines) {
                    val y = height * (i.toFloat() / gridLines)
                    drawLine(
                        color = Slate200,
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                // Path construction
                val path = Path()
                val fillPath = Path()

                points.forEachIndexed { index, pair ->
                    val x = index * stepX
                    val normalizedY = 1f - ((pair.second - minValue) / valueRange).toFloat()
                    val y = normalizedY * height

                    if (index == 0) {
                        path.moveTo(x, y)
                        fillPath.moveTo(x, height)
                        fillPath.lineTo(x, y)
                    } else {
                        val prevX = (index - 1) * stepX
                        val prevNormalizedY = 1f - ((points[index - 1].second - minValue) / valueRange).toFloat()
                        val prevY = prevNormalizedY * height

                        val cx = (prevX + x) / 2
                        path.cubicTo(cx, prevY, cx, y, x, y)
                        fillPath.cubicTo(cx, prevY, cx, y, x, y)
                    }

                    if (index == points.size - 1) {
                        fillPath.lineTo(x, height)
                        fillPath.close()
                    }
                }

                // Draw gradient area under curve
                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            AccentOrange.copy(alpha = 0.25f),
                            AccentOrange.copy(alpha = 0.02f)
                        ),
                        startY = 0f,
                        endY = height
                    )
                )

                // Draw main curve line
                drawPath(
                    path = path,
                    color = AccentOrange,
                    style = Stroke(width = 3.dp.toPx())
                )

                // Draw data points
                points.forEachIndexed { index, pair ->
                    val x = index * stepX
                    val normalizedY = 1f - ((pair.second - minValue) / valueRange).toFloat()
                    val y = normalizedY * height

                    drawCircle(
                        color = SurfaceWhite,
                        radius = 4.5.dp.toPx(),
                        center = Offset(x, y)
                    )
                    drawCircle(
                        color = AccentOrange,
                        radius = 3.dp.toPx(),
                        center = Offset(x, y)
                    )
                }
            }

            // X-axis labels
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                points.forEach { (label, _) ->
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = Slate400
                    )
                }
            }
        }
    }
}

@Composable
fun PurchasesVsSalesChart(
    data: List<MonthlyComparison>,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) return

    val maxVal = data.maxOfOrNull { maxOf(it.purchasesTotal, it.salesTotal) } ?: 1.0

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceWhite)
            .border(1.dp, Slate200, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Purchases vs. Sales Comparison",
                    style = MaterialTheme.typography.titleMedium,
                    color = Slate900
                )
                Text(
                    text = "Monthly cash flow & throughput comparison",
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate500
                )
            }
            // Legend
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Slate700)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Purchases",
                        style = MaterialTheme.typography.labelSmall,
                        color = Slate600
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(SuccessGreen)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Sales",
                        style = MaterialTheme.typography.labelSmall,
                        color = Slate600
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height - 24.dp.toPx()
                val barGroupWidth = width / data.size
                val barWidth = barGroupWidth * 0.28f
                val spacing = 4.dp.toPx()

                // Background grid lines
                val gridLines = 4
                for (i in 0..gridLines) {
                    val y = height * (i.toFloat() / gridLines)
                    drawLine(
                        color = Slate200,
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                data.forEachIndexed { i, item ->
                    val groupCenter = (i * barGroupWidth) + (barGroupWidth / 2)

                    // Purchase bar (Slate700)
                    val purchaseHeight = ((item.purchasesTotal / maxVal) * height).toFloat()
                    val pLeft = groupCenter - barWidth - (spacing / 2)
                    drawRoundRect(
                        color = Slate700,
                        topLeft = Offset(pLeft, height - purchaseHeight),
                        size = Size(barWidth, purchaseHeight),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx(), 4.dp.toPx())
                    )

                    // Sales bar (SuccessGreen)
                    val salesHeight = ((item.salesTotal / maxVal) * height).toFloat()
                    val sLeft = groupCenter + (spacing / 2)
                    drawRoundRect(
                        color = SuccessGreen,
                        topLeft = Offset(sLeft, height - salesHeight),
                        size = Size(barWidth, salesHeight),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx(), 4.dp.toPx())
                    )
                }
            }

            // Month labels
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                data.forEach { item ->
                    Text(
                        text = item.monthName,
                        style = MaterialTheme.typography.labelSmall,
                        color = Slate500
                    )
                }
            }
        }
    }
}

@Composable
fun BestSellingLeaderboard(
    items: List<BestSellingItem>,
    modifier: Modifier = Modifier
) {
    val maxRevenue = items.maxOfOrNull { it.revenue }?.coerceAtLeast(1.0) ?: 1.0

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceWhite)
            .border(1.dp, Slate200, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "Top-Performing Inventory Items",
            style = MaterialTheme.typography.titleMedium,
            color = Slate900
        )
        Text(
            text = "Ranked by gross sales volume and revenue generation",
            style = MaterialTheme.typography.bodySmall,
            color = Slate500
        )

        Spacer(modifier = Modifier.height(14.dp))

        if (items.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No sales recorded yet. Record sales to populate leaderboard.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Slate400
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items.take(5).forEachIndexed { index, item ->
                    val ratio = (item.revenue / maxRevenue).toFloat().coerceIn(0.05f, 1f)

                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(if (index == 0) AccentOrange else Slate100),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${index + 1}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (index == 0) Color.White else Slate700
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = item.productName,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = Slate900
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = item.sku,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Slate400
                                )
                            }
                            Text(
                                text = "$${String.format(java.util.Locale.US, "%,.2f", item.revenue)} (${item.unitsSold} units)",
                                style = MaterialTheme.typography.labelMedium,
                                color = Slate700
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Progress bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(Slate100)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(ratio)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(
                                        if (index == 0) AccentOrange else Slate700
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}
