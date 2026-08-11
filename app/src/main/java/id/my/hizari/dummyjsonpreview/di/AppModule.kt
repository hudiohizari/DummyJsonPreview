package id.my.hizari.dummyjsonpreview.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

/**
 * id.my.hizari.dummyjsonpreview.di
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    @Named(NAME_APP_LABEL)
    fun provideAppLabel(): String = "Dummy Json Preview"

    const val NAME_APP_LABEL = "appLabel"
}
