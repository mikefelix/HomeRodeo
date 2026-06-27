package com.mozzarelly.homerodeo.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.chuckerteam.chucker.api.RetentionManager
import com.mozzarelly.homerodeo.BuildConfig
import com.mozzarelly.homerodeo.HomeRodeoApplication
import com.mozzarelly.homerodeo.data.api.AlarmApi
import com.mozzarelly.homerodeo.data.api.DevicesApi
import com.mozzarelly.homerodeo.data.api.FermenterApi
import com.mozzarelly.homerodeo.data.api.WeatherApi
import com.mozzarelly.homerodeo.data.api.fake.FakeAlarmApi
import com.mozzarelly.homerodeo.data.api.fake.FakeDevicesApi
import com.mozzarelly.homerodeo.data.api.fake.FakeFermenterApi
import com.mozzarelly.homerodeo.data.api.fake.FakeWeatherApi
import com.mozzarelly.homerodeo.util.ApiResponseCallAdapterFactory
import com.mozzarelly.homerodeo.util.NetworkTestingConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideHomeRodeoApplication(application: Application): HomeRodeoApplication =
        application as HomeRodeoApplication

    @Provides
    @Singleton
    fun provideNetworkTestingConfig(): NetworkTestingConfig = object : NetworkTestingConfig {
        override val failCallsPercent: Float = 0f
        override fun shouldFailThisCall(request: Request): Boolean = false
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        @ApplicationContext context: Context,
        sharedPrefs: SharedPreferences
    ): OkHttpClient {
      return OkHttpClient.Builder().run {
        addInterceptor(
          HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        )

        addInterceptor { chain ->
          chain.proceed(
            chain.request().newBuilder()
              .addHeader("Authorization", BuildConfig.API_AUTH)
              .build()
          )
        }

        if (sharedPrefs.getBoolean("USE_CHUCKER", false)) {
          //        .addInterceptor(ChuckerInterceptor(context))
          addInterceptor(
            ChuckerInterceptor.Builder(context)
              // The previously created Collector
              .collector(
                ChuckerCollector(
                  context = context,
                  // Toggles visibility of the notification
                  showNotification = true,
                  // Allows to customize the retention period of collected data
                  retentionPeriod = RetentionManager.Period.ONE_HOUR
                )
              )
              // The max body content length in bytes, after this responses will be truncated.
              .maxContentLength(250_000L)
              // List of headers to replace with ** in the Chucker UI
              //            .redactHeaders("Auth-Token", "Bearer")
              // Read the whole response body even when the client does not consume the response completely. This is useful in case of parsing errors or when the response body is closed before being read like in Retrofit with Void and Unit types.
              .alwaysReadResponseBody(true)
              // Use decoder when processing request and response bodies. When multiple decoders are installed they are applied in an order they were added.
              //        .addBodyDecoder(decoder)
              // Controls Android shortcut creation.
              .createShortcut(true)
              .build()
          )
        }

        build()
      }
    }

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        isLenient = true
        ignoreUnknownKeys = true
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        json: Json,
        testingConfig: NetworkTestingConfig,
    ): Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL.takeUnless { it.isBlank() } ?: "http://localhost/")
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .addCallAdapterFactory(ApiResponseCallAdapterFactory(testingConfig))
        .build()

    @Provides
    @Singleton
    fun provideDevicesApi(app: HomeRodeoApplication, retrofit: Retrofit): DevicesApi =
        if (app.useFakes) FakeDevicesApi() else retrofit.create(DevicesApi::class.java)

    @Provides
    @Singleton
    fun provideAlarmApi(app: HomeRodeoApplication, retrofit: Retrofit): AlarmApi =
        if (app.useFakes) FakeAlarmApi() else retrofit.create(AlarmApi::class.java)

    @Provides
    @Singleton
    fun provideFermenterApi(app: HomeRodeoApplication, retrofit: Retrofit): FermenterApi =
        if (app.useFakes) FakeFermenterApi() else retrofit.create(FermenterApi::class.java)

    @Provides
    @Singleton
    fun provideWeatherApi(app: HomeRodeoApplication, retrofit: Retrofit): WeatherApi =
        if (app.useFakes) FakeWeatherApi() else retrofit.create(WeatherApi::class.java)


}
