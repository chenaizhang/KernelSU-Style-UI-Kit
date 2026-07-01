package com.example.kernelsustyleuikit.ui.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.kernelsustyleuikit.R
import com.example.kernelsustyleuikit.permission.PermissionState
import com.example.kernelsustyleuikit.ui.component.material.TonalCard

@Composable
fun HomePagerMaterial(
    state: HomeUiState,
    permissionState: PermissionState,
    actions: HomeActions,
    bottomInnerPadding: Dp,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        topBar = { TopBar(scrollBehavior = scrollBehavior) },
        contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Keep the theme settings preview in sync whenever this home layout changes.
            WarningCard(stringResource(R.string.home_sample_notification))
            PermissionCard(permissionState, actions.onPermissionsClick)
            InfoCard(systemInfo = state.systemInfo)
            ExampleLinkCard(onOpenUrl = actions.onOpenUrl)
            Spacer(Modifier.height(bottomInnerPadding))
        }
    }
}

@Composable
private fun TopBar(
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    LargeFlexibleTopAppBar(
        title = { Text(stringResource(R.string.app_name)) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            scrolledContainerColor = MaterialTheme.colorScheme.surface
        ),
        windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal),
        scrollBehavior = scrollBehavior
    )
}

@Composable
private fun PermissionCard(
    state: PermissionState,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor =
                if (state.requiredGranted) MaterialTheme.colorScheme.secondaryContainer
                else MaterialTheme.colorScheme.errorContainer,
            contentColor =
                if (state.requiredGranted) MaterialTheme.colorScheme.onSecondaryContainer
                else MaterialTheme.colorScheme.onErrorContainer,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.permission_section),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text =
                            if (state.requiredGranted) {
                                stringResource(R.string.permission_ready)
                            } else {
                                stringResource(R.string.permission_missing)
                            },
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                AssistChip(
                    onClick = { },
                    colors = AssistChipDefaults.assistChipColors(
                        labelColor =
                            if (state.requiredGranted) {
                                MaterialTheme.colorScheme.onSecondaryContainer
                            } else {
                                MaterialTheme.colorScheme.onErrorContainer
                            },
                        leadingIconContentColor =
                            if (state.requiredGranted) {
                                MaterialTheme.colorScheme.onSecondaryContainer
                            } else {
                                MaterialTheme.colorScheme.onErrorContainer
                            },
                    ),
                    label = {
                        Text(
                            if (state.requiredGranted) {
                                stringResource(R.string.permission_granted)
                            } else {
                                stringResource(R.string.permission_action_required)
                            }
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector =
                                if (state.requiredGranted) Icons.Default.CheckCircle
                                else Icons.Default.ErrorOutline,
                            contentDescription = null,
                        )
                    },
                )
            }
        }
    }
}

@Composable
private fun WarningCard(
    message: String,
    color: Color = MaterialTheme.colorScheme.error,
    onClick: (() -> Unit)? = null
) {
    val content = @Composable {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(text = message, style = MaterialTheme.typography.bodyMedium)
        }
    }
    if (onClick != null) {
        TonalCard(containerColor = color, onClick = onClick, content = content)
    } else {
        TonalCard(containerColor = color, content = content)
    }
}

@Composable
private fun ExampleLinkCard(onOpenUrl: (String) -> Unit) {
    val url = stringResource(R.string.home_example_link_url)
    TonalCard(onClick = { onOpenUrl(url) }) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = stringResource(R.string.home_example_link_title), style = MaterialTheme.typography.titleSmall)
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.home_example_link_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Composable
private fun InfoCard(systemInfo: SystemInfo) {
    TonalCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, top = 24.dp, end = 24.dp, bottom = 16.dp)
        ) {
            @Composable
            fun InfoCardItem(label: String, content: String) {
                Text(text = label, style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            InfoCardItem(stringResource(R.string.home_app_version), systemInfo.appVersion)
        }
    }
}
