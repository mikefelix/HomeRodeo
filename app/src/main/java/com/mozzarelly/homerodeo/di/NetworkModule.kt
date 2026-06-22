package com.mozzarelly.homerodeo.di

import com.mozzarelly.homerodeo.data.api.AlarmApi
import com.mozzarelly.homerodeo.data.api.DevicesApi
import com.mozzarelly.homerodeo.data.api.FermenterApi
import com.mozzarelly.homerodeo.data.api.WeatherApi
import com.mozzarelly.homerodeo.util.ApiResponseCallAdapterFactory
import com.mozzarelly.homerodeo.util.NetworkTestingConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

private const val BASE_URL = "http://homerodeo.local/"

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideNetworkTestingConfig(): NetworkTestingConfig = object : NetworkTestingConfig {
        override val failCallsPercent: Float = 0f
        override fun shouldFailThisCall(request: Request): Boolean = false
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder().build()

    @Provides
    @Singleton
    fun provideJson(): Json = Json { ignoreUnknownKeys = true }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        json: Json,
        testingConfig: NetworkTestingConfig,
    ): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .addCallAdapterFactory(ApiResponseCallAdapterFactory(testingConfig))
        .build()

    @Provides
    @Singleton
    fun provideDevicesApi(retrofit: Retrofit): DevicesApi =
        retrofit.create(DevicesApi::class.java)

    @Provides
    @Singleton
    fun provideAlarmApi(retrofit: Retrofit): AlarmApi =
        retrofit.create(AlarmApi::class.java)

    @Provides
    @Singleton
    fun provideFermenterApi(retrofit: Retrofit): FermenterApi =
        retrofit.create(FermenterApi::class.java)

    @Provides
    @Singleton
    fun provideWeatherApi(retrofit: Retrofit): WeatherApi =
        retrofit.create(WeatherApi::class.java)
}
