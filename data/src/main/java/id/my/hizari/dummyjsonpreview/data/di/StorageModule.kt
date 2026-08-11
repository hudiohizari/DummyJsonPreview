package id.my.hizari.dummyjsonpreview.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * id.my.hizari.dummyjsonpreview.data.di
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

private val Context.sessionDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "dummyjson_session"
)

@Module
@InstallIn(value = [SingletonComponent::class])
object StorageModule {

    @Provides
    @Singleton
    fun provideSessionDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> = context.sessionDataStore
}
