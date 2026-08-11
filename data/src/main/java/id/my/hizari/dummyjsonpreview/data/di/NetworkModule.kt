package id.my.hizari.dummyjsonpreview.data.di

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import id.my.hizari.dummyjsonpreview.data.auth.api.AuthApi
import id.my.hizari.dummyjsonpreview.data.auth.api.AuthInterceptor
import id.my.hizari.dummyjsonpreview.data.network.DummyJsonConfig
import id.my.hizari.dummyjsonpreview.data.product.api.ProductApi
import id.my.hizari.dummyjsonpreview.data.auth.api.TokenAuthenticator
import id.my.hizari.dummyjsonpreview.data.auth.api.UserApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * id.my.hizari.dummyjsonpreview.data.di
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

/**
 * Two clients, deliberately. Serving the auth endpoints from a client that has neither the
 * interceptor nor the authenticator means a login can never carry a stale token, and it breaks the
 * dependency cycle that a single shared client would otherwise create between the authenticator
 * and the API it needs in order to refresh.
 */
@Module
@InstallIn(value = [SingletonComponent::class])
object NetworkModule {

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply(block = {
        level = HttpLoggingInterceptor.Level.BODY
    })

    /** Debug builds inspect traffic in-app; the release variant resolves to Chucker's no-op. */
    @Provides
    @Singleton
    fun provideChuckerInterceptor(
        @ApplicationContext context: Context
    ): ChuckerInterceptor = ChuckerInterceptor.Builder(context).build()

    @Provides
    @Singleton
    @UnauthenticatedClient
    fun provideUnauthenticatedClient(
        loggingInterceptor: HttpLoggingInterceptor,
        chuckerInterceptor: ChuckerInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(interceptor = loggingInterceptor)
        .addInterceptor(interceptor = chuckerInterceptor)
        .build()

    @Provides
    @Singleton
    @UnauthenticatedClient
    fun provideUnauthenticatedRetrofit(
        @UnauthenticatedClient client: OkHttpClient,
        gson: Gson
    ): Retrofit = Retrofit.Builder()
        .baseUrl(DummyJsonConfig.BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    @Provides
    @Singleton
    fun provideAuthApi(@UnauthenticatedClient retrofit: Retrofit): AuthApi =
        retrofit.create(AuthApi::class.java)

    @Provides
    @Singleton
    @AuthenticatedClient
    fun provideAuthenticatedClient(
        authInterceptor: AuthInterceptor,
        tokenAuthenticator: TokenAuthenticator,
        loggingInterceptor: HttpLoggingInterceptor,
        chuckerInterceptor: ChuckerInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(interceptor = authInterceptor)
        // Logging and Chucker go after auth so the bearer header shows up in what they record.
        .addInterceptor(interceptor = loggingInterceptor)
        .addInterceptor(interceptor = chuckerInterceptor)
        .authenticator(authenticator = tokenAuthenticator)
        .build()

    @Provides
    @Singleton
    @AuthenticatedClient
    fun provideAuthenticatedRetrofit(
        @AuthenticatedClient client: OkHttpClient,
        gson: Gson
    ): Retrofit = Retrofit.Builder()
        .baseUrl(DummyJsonConfig.BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    @Provides
    @Singleton
    fun provideUserApi(@AuthenticatedClient retrofit: Retrofit): UserApi =
        retrofit.create(UserApi::class.java)

    @Provides
    @Singleton
    fun provideProductApi(@AuthenticatedClient retrofit: Retrofit): ProductApi =
        retrofit.create(ProductApi::class.java)
}
