package id.my.hizari.dummyjsonpreview

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import id.my.hizari.dummyjsonpreview.navigation.graph.RootNavGraph
import id.my.hizari.dummyjsonpreview.ui.theme.DummyJsonPreviewTheme

/**
 * id.my.hizari.dummyjsonpreview
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        // Holding the splash until the stored session has been read is what prevents the login
        // screen appearing for a frame on a cold start.
        splashScreen.setKeepOnScreenCondition(
            condition = { viewModel.authState.value == AuthState.Loading }
        )
        enableEdgeToEdge()
        setContent(content = {
            DummyJsonPreviewTheme(dynamicColor = false) {
                val authState by viewModel.authState.collectAsStateWithLifecycle()
                if (authState != AuthState.Loading) {
                    val authenticated = authState == AuthState.Authenticated
                    // Frozen so later session changes redirect instead of rebuilding the graph.
                    val startsAuthenticated = rememberSaveable(init = { authenticated })
                    RootNavGraph(
                        isAuthenticated = authenticated,
                        startsAuthenticated = startsAuthenticated
                    )
                }
            }
        })
    }
}
