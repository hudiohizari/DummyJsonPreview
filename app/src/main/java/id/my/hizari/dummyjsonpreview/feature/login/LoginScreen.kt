package id.my.hizari.dummyjsonpreview.feature.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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
        modifier = modifier,
        state = state,
        onUsernameChange = viewModel::onUsernameChange,
        onPasswordChange = viewModel::onPasswordChange,
        onSignIn = viewModel::signIn
    )
}

@Composable
fun LoginContent(
    modifier: Modifier = Modifier,
    state: LoginState,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSignIn: () -> Unit
) {
    var isPasswordVisible by remember { mutableStateOf(value = false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(all = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.title_login),
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            modifier = Modifier.padding(top = 4.dp),
            text = stringResource(id = R.string.placeholder_login),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            value = state.username,
            onValueChange = onUsernameChange,
            label = { Text(text = stringResource(id = R.string.label_username)) },
            singleLine = true,
            isError = state.isUsernameBlank,
            supportingText = {
                if (state.isUsernameBlank) {
                    Text(text = stringResource(id = R.string.error_username_required))
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            enabled = !state.isLoading
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            value = state.password,
            onValueChange = onPasswordChange,
            label = { Text(text = stringResource(id = R.string.label_password)) },
            singleLine = true,
            isError = state.isPasswordBlank,
            supportingText = {
                if (state.isPasswordBlank) {
                    Text(text = stringResource(id = R.string.error_password_required))
                }
            },
            visualTransformation = if (isPasswordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                IconButton(
                    onClick = { isPasswordVisible = !isPasswordVisible },
                    content = {
                        Icon(
                            imageVector = if (isPasswordVisible) {
                                Icons.Default.VisibilityOff
                            } else {
                                Icons.Default.Visibility
                            },
                            contentDescription = stringResource(
                                id = if (isPasswordVisible) {
                                    R.string.action_hide_password
                                } else {
                                    R.string.action_show_password
                                }
                            )
                        )
                    }
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { onSignIn() }),
            enabled = !state.isLoading
        )

        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(top = 24.dp))
        } else {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                onClick = onSignIn,
                content = { Text(text = stringResource(id = R.string.action_sign_in)) }
            )
        }

        state.errorMessage?.let(block = { message ->
            Text(
                modifier = Modifier.padding(top = 12.dp),
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        })
    }
}

@Preview(name = "Login", showBackground = true)
@Composable
private fun LoginContentPreview() {
    DummyJsonPreviewTheme(dynamicColor = false) {
        LoginContent(
            state = LoginState(username = "emilys", password = "emilyspass"),
            onUsernameChange = {},
            onPasswordChange = {},
            onSignIn = {}
        )
    }
}

@Preview(name = "Login signing in", showBackground = true)
@Composable
private fun LoginContentLoadingPreview() {
    DummyJsonPreviewTheme(dynamicColor = false) {
        LoginContent(
            state = LoginState(username = "emilys", password = "emilyspass", isLoading = true),
            onUsernameChange = {},
            onPasswordChange = {},
            onSignIn = {}
        )
    }
}

@Preview(name = "Login rejected", showBackground = true)
@Composable
private fun LoginContentErrorPreview() {
    DummyJsonPreviewTheme(dynamicColor = false) {
        LoginContent(
            state = LoginState(
                username = "emilys",
                password = "wrong",
                errorMessage = "Invalid credentials"
            ),
            onUsernameChange = {},
            onPasswordChange = {},
            onSignIn = {}
        )
    }
}

@Preview(name = "Login empty fields", showBackground = true)
@Composable
private fun LoginContentBlankPreview() {
    DummyJsonPreviewTheme(dynamicColor = false) {
        LoginContent(
            state = LoginState(isUsernameBlank = true, isPasswordBlank = true),
            onUsernameChange = {},
            onPasswordChange = {},
            onSignIn = {}
        )
    }
}
