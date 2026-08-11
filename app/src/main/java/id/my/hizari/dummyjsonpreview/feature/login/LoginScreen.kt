package id.my.hizari.dummyjsonpreview.feature.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import id.my.hizari.dummyjsonpreview.ui.theme.DummyJsonPreviewTheme

/**
 * id.my.hizari.dummyjsonpreview.feature.login
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LoginContent(
        state = state,
        onSignIn = { viewModel.signIn(username = DEMO_USERNAME, password = DEMO_PASSWORD) },
        modifier = modifier
    )
}

@Composable
fun LoginContent(
    state: LoginState,
    onSignIn: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(all = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.title_login),
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = stringResource(id = R.string.placeholder_login),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
        )
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(top = 24.dp))
        } else {
            Button(
                onClick = onSignIn,
                modifier = Modifier.padding(top = 24.dp),
                content = { Text(text = stringResource(id = R.string.action_sign_in)) }
            )
        }
        state.errorMessage?.let(block = { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 12.dp)
            )
        })
    }
}

private const val DEMO_USERNAME = "emilys"
private const val DEMO_PASSWORD = "emilyspass"

@Preview(name = "Login", showBackground = true)
@Composable
private fun LoginContentPreview() {
    DummyJsonPreviewTheme(dynamicColor = false) {
        LoginContent(state = LoginState(), onSignIn = {})
    }
}

@Preview(name = "Login signing in", showBackground = true)
@Composable
private fun LoginContentLoadingPreview() {
    DummyJsonPreviewTheme(dynamicColor = false) {
        LoginContent(state = LoginState(isLoading = true), onSignIn = {})
    }
}

@Preview(name = "Login rejected", showBackground = true)
@Composable
private fun LoginContentErrorPreview() {
    DummyJsonPreviewTheme(dynamicColor = false) {
        LoginContent(
            state = LoginState(errorMessage = "Invalid credentials"),
            onSignIn = {}
        )
    }
}
