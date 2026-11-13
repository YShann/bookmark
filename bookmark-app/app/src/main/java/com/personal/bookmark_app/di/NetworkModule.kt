package com.personal.bookmark_app.di

import com.personal.bookmark_app.BuildConfig
import com.personal.bookmark_app.data.remote.RetrofitClient
import com.personal.bookmark_app.data.remote.api.AuthApi
import com.personal.bookmark_app.data.remote.api.BookmarkApi
import com.personal.bookmark_app.data.remote.api.DailyPicksApi
import com.personal.bookmark_app.data.remote.api.FolderApi
import com.personal.bookmark_app.data.remote.api.UserApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

// Retrofit 的建立方式集中在這裡；ViewModel 只需要宣告自己要用哪個 API。
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    // 登入與更新 Token 使用不帶登入攔截器的連線，避免更新 Token 時再次觸發更新。
    @Provides
    @Singleton
    fun provideAuthApi(): AuthApi = Retrofit.Builder()
        .baseUrl(BuildConfig.API_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(AuthApi::class.java)

    @Provides
    @Singleton
    fun provideAuthenticatedRetrofit(client: RetrofitClient): Retrofit = client.authenticated()

    @Provides
    @Singleton
    fun provideBookmarkApi(retrofit: Retrofit): BookmarkApi =
        retrofit.create(BookmarkApi::class.java)

    @Provides
    @Singleton
    fun provideFolderApi(retrofit: Retrofit): FolderApi = retrofit.create(FolderApi::class.java)

    @Provides
    @Singleton
    fun provideUserApi(retrofit: Retrofit): UserApi = retrofit.create(UserApi::class.java)

    @Provides
    @Singleton
    fun provideDailyPicksApi(retrofit: Retrofit): DailyPicksApi =
        retrofit.create(DailyPicksApi::class.java)
}
