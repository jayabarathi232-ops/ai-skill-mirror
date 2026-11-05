package com.simats.aiskillmirror.ui.notifications

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.simats.aiskillmirror.ui.dashboard.DashboardTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    viewModel: NotificationsViewModel,
    onTabSelected: (DashboardTab) -> Unit = {},
    onRequestPermission: () -> Unit = {},
    onMarkAllAsReadClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val notificationItems = listOf(
        "Welcome to AI Skills Mirror" to
                "Start your skill assessment and build your learning plan.",
        "Daily learning reminder" to
                "Continue your daily training to improve your skills.",
        "Progress update" to
                "Check your latest learning progress and achievements."
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Notifications")
                },
                navigationIcon = {
                    TextButton(onClick = onBackClick) {
                        Text("← Back")
                    }
                },
                actions = {
                    TextButton(onClick = onSettingsClick) {
                        Text("Settings")
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp)
        ) {

            Text(
                text = "Stay updated with your learning journey",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onMarkAllAsReadClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Mark all as read")
                }

                OutlinedButton(
                    onClick = onRequestPermission,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Enable alerts")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Recent notifications",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(notificationItems) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = item.first,
                                style = MaterialTheme.typography.titleMedium
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = item.second,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}
