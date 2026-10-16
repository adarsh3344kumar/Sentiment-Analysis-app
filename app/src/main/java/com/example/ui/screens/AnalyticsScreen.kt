package com.example.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.SentimentViewModel
import com.example.ui.components.ChartLegendItem
import com.example.ui.components.MetricStatCard
import com.example.ui.components.SentimentDonutChart
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.IndigoLight
import com.example.ui.theme.SentimentNegative
import com.example.ui.theme.SentimentNeutral
import com.example.ui.theme.SentimentPositive
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary

@Composable
fun AnalyticsScreen(
    viewModel: SentimentViewModel,
    modifier: Modifier = Modifier
) {
    val analytics by viewModel.analytics.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("analytics_screen")
    ) {
        // Title
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Analytics,
                contentDescription = null,
                tint = IndigoLight,
                modifier = Modifier.padding(end = 8.dp)
            )
            Column {
                Text(
                    text = "Analytics Dashboard",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Black
                    ),
                    color = Color.White
                )
                Text(
                    text = "Aggregated dataset metrics calculated from Room SQLite",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Top KPI Stats Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricStatCard(
                title = "Total Analyses",
                value = "${analytics.totalCount}",
                subtitle = "Logged in database",
                icon = Icons.Default.TrendingUp,
                accentColor = IndigoLight,
                modifier = Modifier
                    .weight(1f)
                    .testTag("kpi_total_analyses")
            )

            MetricStatCard(
                title = "Avg Confidence",
                value = "${analytics.avgConfidence.toInt()}%",
                subtitle = "Model certainty",
                icon = Icons.Default.Speed,
                accentColor = CyanAccent,
                modifier = Modifier
                    .weight(1f)
                    .testTag("kpi_avg_confidence")
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Visual Sentiment Donut Chart Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, DarkBorder, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Sentiment Class Distribution",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(16.dp))

                SentimentDonutChart(
                    positiveCount = analytics.positiveCount,
                    neutralCount = analytics.neutralCount,
                    negativeCount = analytics.negativeCount
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Legends
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ChartLegendItem(
                        label = "Positive Reviews",
                        count = analytics.positiveCount,
                        percent = analytics.positivePercent,
                        color = SentimentPositive
                    )
                    ChartLegendItem(
                        label = "Neutral / Factual",
                        count = analytics.neutralCount,
                        percent = analytics.neutralPercent,
                        color = SentimentNeutral
                    )
                    ChartLegendItem(
                        label = "Negative Feedback",
                        count = analytics.negativeCount,
                        percent = analytics.negativePercent,
                        color = SentimentNegative
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Three class individual breakdowns
        Text(
            text = "Class Volume Breakdown",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ClassCountMiniCard(
                label = "Positive",
                count = analytics.positiveCount,
                percent = analytics.positivePercent,
                color = SentimentPositive,
                icon = Icons.Default.CheckCircle,
                modifier = Modifier.weight(1f)
            )

            ClassCountMiniCard(
                label = "Neutral",
                count = analytics.neutralCount,
                percent = analytics.neutralPercent,
                color = SentimentNeutral,
                icon = Icons.Default.RemoveCircle,
                modifier = Modifier.weight(1f)
            )

            ClassCountMiniCard(
                label = "Negative",
                count = analytics.negativeCount,
                percent = analytics.negativePercent,
                color = SentimentNegative,
                icon = Icons.Default.Error,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Executive Summary Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, DarkBorder, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Executive Summary",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(6.dp))
                val summaryText = if (analytics.totalCount == 0) {
                    "No review data available yet. Please submit reviews on the Analyze tab to generate live insights."
                } else if (analytics.positiveCount >= analytics.negativeCount && analytics.positiveCount >= analytics.neutralCount) {
                    "Overall sentiment trends favorably positive (${analytics.positivePercent.toInt()}%). Key positive patterns dominate customer feedback with solid user satisfaction."
                } else if (analytics.negativeCount >= analytics.positiveCount) {
                    "Critical negative feedback accounts for ${analytics.negativePercent.toInt()}% of analyzed records. Attention required for recurring service delays and defect issues."
                } else {
                    "Customer feedback is predominantly neutral/informational (${analytics.neutralPercent.toInt()}%), reflecting objective inquiries and standard transaction confirmations."
                }
                Text(
                    text = summaryText,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
fun ClassCountMiniCard(
    label: String,
    count: Int,
    percent: Float,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.border(1.dp, DarkBorder, RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = color,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = androidx.compose.ui.Modifier.size(14.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "$count",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = Color.White
            )

            Text(
                text = "${percent.toInt()}% of total",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                fontSize = 10.sp
            )
        }
    }
}
