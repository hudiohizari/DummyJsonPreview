package id.my.hizari.dummyjsonpreview.feature.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import id.my.hizari.dummyjsonpreview.R
import id.my.hizari.dummyjsonpreview.domain.auth.model.User
import id.my.hizari.dummyjsonpreview.ui.components.StateBanner
import id.my.hizari.dummyjsonpreview.ui.theme.DummyJsonPreviewTheme

/**
 * id.my.hizari.dummyjsonpreview.feature.profile
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ProfileContent(
        modifier = modifier,
        state = state,
        onRefresh = viewModel::onRefresh,
        onLogoutClick = viewModel::onLogoutClick,
        onLogoutDismiss = viewModel::onLogoutDismiss,
        onLogoutConfirm = viewModel::onLogoutConfirm
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileContent(
    modifier: Modifier = Modifier,
    state: ProfileState,
    onRefresh: () -> Unit,
    onLogoutClick: () -> Unit,
    onLogoutDismiss: () -> Unit,
    onLogoutConfirm: () -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(title = { Text(text = stringResource(id = R.string.title_profile)) })
        }
    ) { innerPadding ->
        PullToRefreshBox(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = innerPadding),
            isRefreshing = state.isRefreshing,
            onRefresh = onRefresh
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(state = rememberScrollState())
            ) {
                // The stored profile is still on screen, so a failed refresh sits above it.
                if (state.errorMessage != null) {
                    StateBanner(
                        icon = Icons.Default.ErrorOutline,
                        message = state.errorMessage,
                        iconTint = MaterialTheme.colorScheme.onErrorContainer,
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        messageColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        modifier = Modifier
                            .size(size = 96.dp)
                            .clip(shape = CircleShape),
                        model = state.user?.image,
                        contentDescription = state.user?.fullName,
                        contentScale = ContentScale.Crop
                    )
                    Text(
                        modifier = Modifier.padding(top = 16.dp),
                        text = state.user?.fullName.orEmpty(),
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = state.user?.username.orEmpty(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                HorizontalDivider()

                Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
                    ProfileRow(
                        label = stringResource(id = R.string.label_email),
                        value = state.user?.email
                    )
                    ProfileRow(
                        label = stringResource(id = R.string.label_username),
                        value = state.user?.username
                    )
                    ProfileRow(
                        label = stringResource(id = R.string.label_gender),
                        value = state.user?.gender
                    )
                }

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 24.dp),
                    onClick = onLogoutClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ),
                    content = {
                        Icon(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(size = 18.dp),
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = null
                        )
                        Text(text = stringResource(id = R.string.action_logout))
                    }
                )
            }
        }
    }

    if (state.isLogoutDialogVisible) {
        LogoutConfirmDialog(
            isLoggingOut = state.isLoggingOut,
            onDismiss = onLogoutDismiss,
            onConfirm = onLogoutConfirm
        )
    }
}

/** Skips itself when the field is missing, so absent data leaves no empty row behind. */
@Composable
private fun ProfileRow(
    modifier: Modifier = Modifier,
    label: String,
    value: String?
) {
    if (value.isNullOrBlank()) return

    Row(modifier = modifier.padding(vertical = 8.dp)) {
        Text(
            modifier = Modifier.weight(weight = 1f),
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun LogoutConfirmDialog(
    isLoggingOut: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { if (!isLoggingOut) onDismiss() },
        icon = {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Logout,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = { Text(text = stringResource(id = R.string.logout_confirm_title)) },
        text = { Text(text = stringResource(id = R.string.logout_confirm_message)) },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = !isLoggingOut,
                content = {
                    Text(
                        text = stringResource(id = R.string.action_logout),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            )
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoggingOut,
                content = { Text(text = stringResource(id = R.string.action_cancel)) }
            )
        }
    )
}

@Preview(name = "Profile", showBackground = true)
@Composable
private fun ProfilePreview() {
    DummyJsonPreviewTheme(dynamicColor = false) {
        ProfileContent(
            state = ProfileState(user = previewUser()),
            onRefresh = {},
            onLogoutClick = {},
            onLogoutDismiss = {},
            onLogoutConfirm = {}
        )
    }
}

@Preview(name = "Profile refresh failed", showBackground = true)
@Composable
private fun ProfileErrorPreview() {
    DummyJsonPreviewTheme(dynamicColor = false) {
        ProfileContent(
            state = ProfileState(user = previewUser(), errorMessage = "Network unavailable"),
            onRefresh = {},
            onLogoutClick = {},
            onLogoutDismiss = {},
            onLogoutConfirm = {}
        )
    }
}

@Preview(name = "Profile log out confirm", showBackground = true)
@Composable
private fun ProfileLogoutConfirmPreview() {
    DummyJsonPreviewTheme(dynamicColor = false) {
        ProfileContent(
            state = ProfileState(user = previewUser(), isLogoutDialogVisible = true),
            onRefresh = {},
            onLogoutClick = {},
            onLogoutDismiss = {},
            onLogoutConfirm = {}
        )
    }
}

@Preview(name = "Profile without a user", showBackground = true)
@Composable
private fun ProfileEmptyPreview() {
    DummyJsonPreviewTheme(dynamicColor = false) {
        ProfileContent(
            state = ProfileState(),
            onRefresh = {},
            onLogoutClick = {},
            onLogoutDismiss = {},
            onLogoutConfirm = {}
        )
    }
}

private fun previewUser() = User(
    id = 1,
    username = "emilys",
    email = "emily.johnson@x.dummyjson.com",
    firstName = "Emily",
    lastName = "Johnson",
    gender = "female",
    image = null
)
