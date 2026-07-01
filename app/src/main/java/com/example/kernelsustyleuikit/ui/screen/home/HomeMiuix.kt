package com.example.kernelsustyleuikit.ui.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircleOutline
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kernelsustyleuikit.R
import com.example.kernelsustyleuikit.permission.PermissionState
import com.example.kernelsustyleuikit.ui.component.miuix.WarningCard
import com.example.kernelsustyleuikit.ui.theme.LocalEnableBlur
import com.example.kernelsustyleuikit.ui.util.BlurredBar
import com.example.kernelsustyleuikit.ui.util.rememberBlurBackdrop
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.blur.LayerBackdrop
import top.yukonga.miuix.kmp.blur.layerBackdrop
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Link
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic

@Composable
fun HomePagerMiuix(
    state: HomeUiState,
    permissionState: PermissionState,
    actions: HomeActions,
    bottomInnerPadding: Dp,
) {
    val scrollBehavior = MiuixScrollBehavior()
    val enableBlur = LocalEnableBlur.current
    val backdrop = rememberBlurBackdrop(enableBlur)
    val blurActive = backdrop != null
    val barColor = if (blurActive) Color.Transparent else colorScheme.surface
    Scaffold(
        topBar = {
            TopBar(
                scrollBehavior = scrollBehavior,
                backdrop = backdrop,
                barColor = barColor,
            )
        },
        popupHost = { },
        contentWindowInsets = WindowInsets.systemBars.add(WindowInsets.displayCutout).only(WindowInsetsSides.Horizontal)
    ) { innerPadding ->
        Box(modifier = if (backdrop != null) Modifier.layerBackdrop(backdrop) else Modifier) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxHeight()
                    .scrollEndHaptic()
                    .overScrollVertical()
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
                    .padding(horizontal = 12.dp),
                contentPadding = innerPadding,
                overscrollEffect = null,
            ) {
                item {
                    Column(
                        modifier = Modifier.padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        // Keep the theme settings preview in sync whenever this home layout changes.
                        WarningCard(stringResource(R.string.home_sample_notification))
                        PermissionCardMiuix(permissionState, actions.onPermissionsClick)
                        InfoCard(systemInfo = state.systemInfo)
                        ExampleLinkCard(onOpenUrl = actions.onOpenUrl)
                    }
                    Spacer(Modifier.height(bottomInnerPadding))
                }
            }
        }
    }
}

@Composable
private fun PermissionCardMiuix(
    state: PermissionState,
    onClick: () -> Unit,
) {
    val requiredGranted = state.requiredGranted
    val iconColor = if (requiredGranted) Color(0xFF36D167) else Color(0xFFF72727)
    val containerColor = if (requiredGranted) Color(0xFFDFFAE4) else Color(0xFFF8E2E2)
    val textColor = Color(0xFF111111)
    val summary =
        if (requiredGranted) {
            stringResource(R.string.permission_ready)
        } else {
            stringResource(R.string.permission_missing)
        }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.defaultColors(color = containerColor),
        onClick = onClick,
        showIndication = true,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(164.dp)
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset(x = 70.dp, y = 44.dp),
                contentAlignment = Alignment.BottomEnd,
            ) {
                Icon(
                    modifier = Modifier.size(182.dp),
                    imageVector =
                        if (requiredGranted) Icons.Rounded.CheckCircleOutline else Icons.Rounded.Cancel,
                    tint = iconColor,
                    contentDescription = null,
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 24.dp, top = 28.dp, end = 148.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text =
                            if (requiredGranted) {
                                stringResource(R.string.permission_status_ready_title)
                            } else {
                                stringResource(R.string.permission_status_missing_title)
                            },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = textColor,
                    )
                    Text(
                        text = summary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = textColor.copy(alpha = 0.72f),
                    )
                }
                Text(
                    text =
                        if (requiredGranted) {
                            stringResource(R.string.permission_granted)
                        } else {
                            stringResource(R.string.permission_action_required)
                        },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = textColor.copy(alpha = 0.78f),
                )
            }
        }
    }
}

@Composable
private fun TopBar(
    scrollBehavior: ScrollBehavior,
    backdrop: LayerBackdrop?,
    barColor: Color,
) {
    BlurredBar(backdrop) {
        TopAppBar(
            color = barColor,
            title = stringResource(R.string.app_name),
            scrollBehavior = scrollBehavior
        )
    }
}

@Composable
private fun ExampleLinkCard(
    onOpenUrl: (String) -> Unit,
) {
    val url = stringResource(R.string.home_example_link_url)
    Card(modifier = Modifier.fillMaxWidth()) {
        BasicComponent(
            title = stringResource(R.string.home_example_link_title),
            summary = stringResource(R.string.home_example_link_subtitle),
            endActions = {
                Icon(
                    imageVector = MiuixIcons.Link,
                    tint = colorScheme.onSurface,
                    contentDescription = null
                )
            },
            onClick = { onOpenUrl(url) }
        )
    }
}

@Composable
private fun InfoCard(systemInfo: SystemInfo) {
    @Composable
    fun InfoText(
        title: String,
        content: String,
        bottomPadding: Dp = 24.dp
    ) {
        Text(
            text = title,
            fontSize = MiuixTheme.textStyles.headline1.fontSize,
            fontWeight = FontWeight.Medium,
            color = colorScheme.onSurface
        )
        Text(
            text = content,
            fontSize = MiuixTheme.textStyles.body2.fontSize,
            color = colorScheme.onSurfaceVariantSummary,
            modifier = Modifier.padding(top = 2.dp, bottom = bottomPadding)
        )
    }

    Card {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            InfoText(
                title = stringResource(R.string.home_app_version),
                content = systemInfo.appVersion,
                bottomPadding = 0.dp
            )
        }
    }
}
