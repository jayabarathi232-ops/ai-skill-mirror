package com.simats.aiskillmirror.ui.settings

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.aiskillmirror.ui.dashboard.BellIcon
import com.simats.aiskillmirror.ui.dashboard.BottomNavigationBar
import com.simats.aiskillmirror.ui.dashboard.DashboardTab
import com.simats.aiskillmirror.ui.dashboard.MicIcon
import com.simats.aiskillmirror.ui.dashboard.RightArrowHeadIcon
import com.simats.aiskillmirror.ui.login.ShieldCheckIcon
import com.simats.aiskillmirror.ui.recovery.BackArrowIcon
import com.simats.aiskillmirror.ui.register.LockIcon
import com.simats.aiskillmirror.ui.register.PersonIcon
import com.simats.aiskillmirror.ui.report.ExportTrayIcon
import com.simats.aiskillmirror.ui.splash.SparkleIcon
import com.simats.aiskillmirror.ui.theme.*

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onTabSelected: (DashboardTab) -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onPersonalInformationClick: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Scrollable Settings Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.Start
            ) {
                // Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                viewModel.onBackClick()
                                onBackClick()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        BackArrowIcon(size = 20.dp, color = TextPrimary)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = uiState.headerTitle,
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = uiState.headerCategory,
                            color = TextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Section 1: Account
                Text(
                    text = "Account",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(SurfaceDark)
                        .border(1.dp, Color(0xFF22293F), RoundedCornerShape(20.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        SettingsClickableRow(
                            icon = { PersonIcon(size = 20.dp, color = TextSecondary) },
                            title = "Personal information",
                            onClick = {
                                viewModel.onPersonalInformationClick()
                                onPersonalInformationClick()
                            }
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = Color(0xFF20263D),
                            thickness = 1.dp
                        )

                        SettingsClickableRow(
                            icon = { LockIcon(size = 20.dp, color = TextSecondary) },
                            title = "Password & security",
                            onClick = { viewModel.onPasswordSecurityClick() }
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = Color(0xFF20263D),
                            thickness = 1.dp
                        )

                        SettingsToggleRow(
                            icon = { BellIcon(size = 20.dp, color = TextSecondary) },
                            title = "Learning reminders",
                            isChecked = uiState.isLearningRemindersEnabled,
                            onCheckedChange = { viewModel.onToggleLearningReminders(it) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                // Section 2: Privacy & intelligence
                Text(
                    text = "Privacy & intelligence",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(SurfaceDark)
                        .border(1.dp, Color(0xFF22293F), RoundedCornerShape(20.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        SettingsToggleRow(
                            icon = { SparkleIcon(size = 20.dp, color = TextSecondary) },
                            title = "Personalized AI insights",
                            isChecked = uiState.isPersonalizedAiEnabled,
                            onCheckedChange = { viewModel.onTogglePersonalizedAi(it) }
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = Color(0xFF20263D),
                            thickness = 1.dp
                        )

                        SettingsToggleRow(
                            icon = { MicIcon(size = 20.dp, color = TextSecondary) },
                            title = "Save voice recordings",
                            isChecked = uiState.isSaveVoiceRecordingsEnabled,
                            onCheckedChange = { viewModel.onToggleSaveVoiceRecordings(it) }
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = Color(0xFF20263D),
                            thickness = 1.dp
                        )

                        SettingsClickableRow(
                            icon = { ShieldCheckIcon(size = 20.dp, color = TextSecondary) },
                            title = "Data & permissions",
                            onClick = { viewModel.onDataPermissionsClick() }
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = Color(0xFF20263D),
                            thickness = 1.dp
                        )

                        SettingsClickableRow(
                            icon = { ExportTrayIcon(size = 20.dp, color = TextSecondary) },
                            title = "Download my data",
                            onClick = { viewModel.onDownloadDataClick() }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                // Section 3: Preferences
                Text(
                    text = "Preferences",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(SurfaceDark)
                        .border(1.dp, Color(0xFF22293F), RoundedCornerShape(20.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        SettingsValueRow(
                            icon = { MoonIcon(size = 20.dp, color = TextSecondary) },
                            title = "Appearance",
                            valueText = uiState.currentAppearanceText
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = Color(0xFF20263D),
                            thickness = 1.dp
                        )

                        SettingsValueRow(
                            icon = { GlobeIcon(size = 20.dp, color = TextSecondary) },
                            title = "Language",
                            valueText = uiState.currentLanguageText
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Footer Links: Help & Support & Log out
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Help & support",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { viewModel.onHelpSupportClick() }
                    )

                    Text(
                        text = "Log out →",
                        color = TaglineText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            viewModel.onLogoutClick()
                            onLogoutClick()
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = uiState.appVersionText,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Fixed Bottom Navigation Bar
            BottomNavigationBar(
                selectedTab = DashboardTab.PROFILE,
                onTabSelected = onTabSelected
            )
        }
    }
}

@Composable
fun SettingsClickableRow(
    icon: @Composable () -> Unit,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            icon()
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        RightArrowHeadIcon(size = 16.dp, color = TextSecondary)
    }
}

@Composable
fun SettingsToggleRow(
    icon: @Composable () -> Unit,
    title: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            icon()
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = PrimaryGradientStart,
                uncheckedThumbColor = TextSecondary,
                uncheckedTrackColor = SurfaceDark
            )
        )
    }
}

@Composable
fun SettingsValueRow(
    icon: @Composable () -> Unit,
    title: String,
    valueText: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            icon()
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = valueText,
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal
            )
            Spacer(modifier = Modifier.width(6.dp))
            RightArrowHeadIcon(size = 16.dp, color = TextSecondary)
        }
    }
}

@Composable
fun MoonIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val strokeWidth = 1.8.dp.toPx()

        drawPath(
            path = Path().apply {
                moveTo(w * 0.75f, h * 0.15f)
                quadraticTo(w * 0.30f, h * 0.20f, w * 0.30f, h * 0.65f)
                quadraticTo(w * 0.30f, h * 0.85f, w * 0.65f, h * 0.88f)
                quadraticTo(w * 0.85f, h * 0.88f, w * 0.85f, h * 0.80f)
                quadraticTo(w * 0.50f, h * 0.75f, w * 0.50f, h * 0.45f)
                quadraticTo(w * 0.50f, h * 0.25f, w * 0.75f, h * 0.15f)
                close()
            },
            color = color,
            style = Stroke(width = strokeWidth)
        )
    }
}

@Composable
fun GlobeIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val strokeWidth = 1.8.dp.toPx()

        drawCircle(
            color = color,
            radius = w * 0.42f,
            center = Offset(w / 2, h / 2),
            style = Stroke(width = strokeWidth)
        )

        // Latitude line
        drawLine(color = color, start = Offset(w * 0.10f, h / 2), end = Offset(w * 0.90f, h / 2), strokeWidth = strokeWidth)

        // Longitude oval arc
        drawPath(
            path = Path().apply {
                moveTo(w / 2, h * 0.08f)
                quadraticTo(w * 0.25f, h / 2, w / 2, h * 0.92f)
                quadraticTo(w * 0.75f, h / 2, w / 2, h * 0.08f)
            },
            color = color,
            style = Stroke(width = strokeWidth)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SettingsScreenPreview() {
    AISkillsMirrorTheme {
        SettingsScreen(
            viewModel = SettingsViewModel()
        )
    }
}
