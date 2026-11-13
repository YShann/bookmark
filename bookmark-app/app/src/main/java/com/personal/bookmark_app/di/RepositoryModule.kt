package com.personal.bookmark_app.di

import android.content.ContentResolver
import android.content.Context
import com.personal.bookmark_app.data.repository.auth.ApiAuthRepository
import com.personal.bookmark_app.data.repository.auth.AuthRepository
import com.personal.bookmark_app.data.repository.user.ApiUserRepository
import com.personal.bookmark_app.data.repository.user.UserRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped

// 介面無法直接建立；用 @Binds 告訴 Hilt 應該使用哪個實作。
@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {
    companion object {
        @Provides
        fun provideContentResolver(@ApplicationContext context: Context): ContentResolver =
            context.contentResolver
    }

    // 註冊中的暫存密碼跟隨 AuthViewModel，不提升為整個 App 共用的狀態。
    @Binds
    @ViewModelScoped
    abstract fun bindAuthRepository(repository: ApiAuthRepository): AuthRepository

    @Binds
    @ViewModelScoped
    abstract fun bindUserRepository(repository: ApiUserRepository): UserRepository
}
