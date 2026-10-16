package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ml.ModelMetricsData
import com.example.ml.VivaQuestion
import com.example.ui.components.MetricStatCard
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.IndigoLight
import com.example.ui.theme.SentimentNegative
import com.example.ui.theme.SentimentNeutral
import com.example.ui.theme.SentimentPositive
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun EvaluationScreen(
    onNavigateBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    androidx.activity.compose.BackHandler {
        onNavigateBack()
    }

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Metrics & Matrix", "Viva & Presentation", "Full-Stack Code")

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("evaluation_screen")
    ) {
        // Top Title with back navigation
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.material3.IconButton(
                onClick = onNavigateBack,
                modifier = Modifier.padding(end = 4.dp).testTag("btn_evaluation_back")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Icon(
                imageVector = Icons.Default.Assessment,
                contentDescription = null,
                tint = CyanAccent,
                modifier = Modifier.padding(end = 8.dp)
            )
            Column {
                Text(
                    text = "Model Evaluation & Viva Guide",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Black
                    ),
                    color = Color.White
                )
                Text(
                    text = "Benchmark metrics, confusion matrix, & college presentation kit",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Tab Navigation
        SecondaryTabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = Color.White
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            when (selectedTab) {
                0 -> MetricsTabContent()
                1 -> VivaTabContent()
                2 -> CodeBlueprintTabContent()
            }
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun MetricsTabContent() {
    Column {
        // High Level KPI Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricStatCard(
                title = "Accuracy",
                value = "${(ModelMetricsData.overallAccuracy * 100).toInt()}%",
                subtitle = "1,000 test set",
                icon = Icons.Default.Assessment,
                accentColor = SentimentPositive,
                modifier = Modifier.weight(1f)
            )

            MetricStatCard(
                title = "Macro F1",
                value = "0.88",
                subtitle = "Harmonic mean",
                icon = Icons.Default.Assessment,
                accentColor = CyanAccent,
                modifier = Modifier.weight(1f)
            )

            MetricStatCard(
                title = "Test Samples",
                value = "${ModelMetricsData.totalTestSamples}",
                subtitle = "Held-out split",
                icon = Icons.Default.Assessment,
                accentColor = IndigoLight,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Classification Report Table Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, DarkBorder, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Per-Class Evaluation Report",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Tested on un-seen held-out evaluation dataset",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Table Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Class", style = MaterialTheme.typography.labelSmall, color = TextMuted, modifier = Modifier.weight(1.2f))
                    Text("Precision", style = MaterialTheme.typography.labelSmall, color = TextMuted, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                    Text("Recall", style = MaterialTheme.typography.labelSmall, color = TextMuted, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                    Text("F1-Score", style = MaterialTheme.typography.labelSmall, color = TextMuted, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                    Text("Support", style = MaterialTheme.typography.labelSmall, color = TextMuted, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                }

                Spacer(modifier = Modifier.height(8.dp))

                ModelMetricsData.classMetrics.forEach { metric ->
                    val color = when (metric.className) {
                        "Positive" -> SentimentPositive
                        "Negative" -> SentimentNegative
                        else -> SentimentNeutral
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = metric.className,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = color,
                            modifier = Modifier.weight(1.2f)
                        )
                        Text("${metric.precision}", style = MaterialTheme.typography.bodySmall, color = Color.White, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                        Text("${metric.recall}", style = MaterialTheme.typography.bodySmall, color = Color.White, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                        Text("${metric.f1Score}", style = MaterialTheme.typography.bodySmall, color = Color.White, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                        Text("${metric.support}", style = MaterialTheme.typography.bodySmall, color = TextSecondary, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                    }
                    HorizontalDivider(color = DarkBorder.copy(alpha = 0.5f))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3x3 Confusion Matrix Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, DarkBorder, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "3×3 Confusion Matrix",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Rows: Actual Class | Columns: Predicted Class (Diagonal = True Positives)",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Column Headers
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Actual \\ Pred", style = MaterialTheme.typography.labelSmall, color = TextMuted, modifier = Modifier.weight(1.2f))
                    Text("Pred Pos", style = MaterialTheme.typography.labelSmall, color = SentimentPositive, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                    Text("Pred Neg", style = MaterialTheme.typography.labelSmall, color = SentimentNegative, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                    Text("Pred Neu", style = MaterialTheme.typography.labelSmall, color = SentimentNeutral, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                }

                Spacer(modifier = Modifier.height(8.dp))

                val rowLabels = listOf("Act. Pos", "Act. Neg", "Act. Neu")
                ModelMetricsData.confusionMatrix.matrix.forEachIndexed { rowIndex, row ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = rowLabels[rowIndex],
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.weight(1.2f)
                        )

                        row.forEachIndexed { colIndex, cellValue ->
                            val isDiagonal = rowIndex == colIndex
                            val cellBg = if (isDiagonal) IndigoLight.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surface
                            val cellBorder = if (isDiagonal) IndigoLight else DarkBorder

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 4.dp)
                                    .height(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(cellBg)
                                    .border(1.dp, cellBorder, RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$cellValue",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (isDiagonal) FontWeight.Black else FontWeight.Normal,
                                    color = if (isDiagonal) IndigoLight else TextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Dataset & Pipeline Specs
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, DarkBorder, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Training & Pipeline Specifications",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(10.dp))

                ModelMetricsData.datasetDetails.forEach { (key, value) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = key,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = value,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            modifier = Modifier.weight(1.4f),
                            textAlign = TextAlign.End
                        )
                    }
                    HorizontalDivider(color = DarkBorder.copy(alpha = 0.3f))
                }
            }
        }
    }
}

@Composable
fun VivaTabContent() {
    Column {
        // Project Overview Script
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, DarkBorder, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        tint = IndigoLight,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "30-Second Viva Pitch / Abstract",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "\"Our project is an end-to-end Sentiment Analysis Tool that classifies arbitrary text into Positive, Negative, or Neutral. It uses an NLP pipeline featuring text normalization, negation-handling N-grams, TF-IDF vectorization, and a multinomial classifier calibrated with Softmax. Inferences are logged in a Room SQLite database for instant analytics, search, and trend visualization.\"",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.9f),
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(8.dp)
                ) {
                    Text(
                        text = "Hinglish tip: 'Sir, yeh tool customer reviews ko analyze karta hai. TF-IDF aur N-grams se 'not good' jaise negation capture hote hain, aur SQLite database mein saari history aur analytics charts save hote hain.'",
                        style = MaterialTheme.typography.bodySmall,
                        color = CyanAccent,
                        fontSize = 11.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Frequently Asked Viva Questions (Q&A)",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = "Tap on any question to view technical answer and Hinglish explanation",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(10.dp))

        ModelMetricsData.vivaQuestions.forEach { viva ->
            VivaQuestionCard(viva = viva)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun VivaQuestionCard(viva: VivaQuestion) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, if (isExpanded) IndigoLight.copy(alpha = 0.5f) else DarkBorder, RoundedCornerShape(12.dp))
            .clickable { isExpanded = !isExpanded },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(IndigoLight.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${viva.id}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = IndigoLight
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = viva.question,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = TextSecondary
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Text(
                        text = "Technical Answer:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = IndigoLight
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = viva.englishAnswer,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(8.dp)
                    ) {
                        Column {
                            Text(
                                text = "Hinglish Quick Note:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = CyanAccent
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = viva.hinglishNote,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CodeBlueprintTabContent() {
    Column {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, DarkBorder, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Code,
                        contentDescription = null,
                        tint = CyanAccent,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "FastAPI Backend Reference (Python)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Complete production Python backend code for the REST API endpoint:",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF070A11))
                        .padding(12.dp)
                ) {
                    Text(
                        text = ModelMetricsData.sampleFastApiSnippet,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = Color(0xFFE2E8F0),
                        lineHeight = 16.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, DarkBorder, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Code,
                        contentDescription = null,
                        tint = SentimentPositive,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "React Frontend Reference (App.jsx)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Clean React component connecting to the /analyze endpoint:",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF070A11))
                        .padding(12.dp)
                ) {
                    Text(
                        text = ModelMetricsData.sampleReactSnippet,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = Color(0xFFE2E8F0),
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}
