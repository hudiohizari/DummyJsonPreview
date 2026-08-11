package id.my.hizari.dummyjsonpreview.feature.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.my.hizari.dummyjsonpreview.R
import id.my.hizari.dummyjsonpreview.domain.auth.model.User
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
    val user by viewModel.user.collectAsStateWithLifecycle()

    ProfileContent(
        modifier = modifier,
        user = user,
        onLogout = viewModel::logout
    )
}

@Composable
fun ProfileContent(
    modifier: Modifier = Modifier,
    user: User?,
    onLogout: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(all = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = user?.fullName.orEmpty(),
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = user?.email.orEmpty(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Button(
            modifier = Modifier.padding(top = 24.dp),
            onClick = onLogout,
            content = { Text(text = stringResource(id = R.string.action_logout)) }
        )
    }
}

@Preview(name = "Profile", showBackground = true)
@Composable
private fun ProfileContentPreview() {
    DummyJsonPreviewTheme(dynamicColor = false) {
        ProfileContent(
            user = User(
                id = 1,
                username = "emilys",
                email = "emily.johnson@x.dummyjson.com",
                firstName = "Emily",
                lastName = "Johnson",
                gender = "female",
                image = null
            ),
            onLogout = {}
        )
    }
}

@Preview(name = "Profile without a user", showBackground = true)
@Composable
private fun ProfileContentEmptyPreview() {
    DummyJsonPreviewTheme(dynamicColor = false) {
        ProfileContent(user = null, onLogout = {})
    }
}
