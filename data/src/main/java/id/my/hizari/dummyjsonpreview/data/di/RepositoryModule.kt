package id.my.hizari.dummyjsonpreview.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import id.my.hizari.dummyjsonpreview.data.auth.api.TokenStore
import id.my.hizari.dummyjsonpreview.data.auth.repository.AuthRepositoryImpl
import id.my.hizari.dummyjsonpreview.data.product.repository.ProductRepositoryImpl
import id.my.hizari.dummyjsonpreview.data.auth.session.SessionManager
import id.my.hizari.dummyjsonpreview.domain.auth.repository.AuthRepository
import id.my.hizari.dummyjsonpreview.domain.product.repository.ProductRepository
import javax.inject.Singleton

/**
 * id.my.hizari.dummyjsonpreview.data.di
 *
 * Created by Hudio Hizari on 11/08/26.
 * https://github.com/hudiohizari
 * https://hizari.my.id
 */

@Module
@InstallIn(value = [SingletonComponent::class])
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindProductRepository(impl: ProductRepositoryImpl): ProductRepository

    /** SessionManager is the only thing that can read the token without suspending. */
    @Binds
    @Singleton
    abstract fun bindTokenStore(impl: SessionManager): TokenStore
}
