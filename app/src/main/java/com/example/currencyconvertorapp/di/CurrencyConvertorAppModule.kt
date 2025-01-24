package com.example.currencyconvertorapp.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.currencyconvertorapp.data.Constants.BASE_URL
import com.example.currencyconvertorapp.data.local.ExchangeRateDao
import com.example.currencyconvertorapp.data.local.ExchangeRateDatabase
import com.example.currencyconvertorapp.data.remote.OpenExchangeRatesApi
import com.example.currencyconvertorapp.data.repository.ExchangeRatesRepositoryImpl
import com.example.currencyconvertorapp.domain.repository.ExchangeRatesRepository
import com.example.currencyconvertorapp.domain.usecases.GetExchangeRatesUseCase
import com.example.currencyconvertorapp.data.utils.NetworkUtils
import com.example.currencyconvertorapp.data.utils.SharedPreferenceUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CurrencyConvertorAppModule {

    @Provides
    @Singleton
    fun provideApi(): OpenExchangeRatesApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenExchangeRatesApi::class.java)
    }

    @Provides
    @Singleton
    fun provideDatabase(app: Application): ExchangeRateDatabase {
        return Room.databaseBuilder(
            app,
            ExchangeRateDatabase::class.java,
            "exchange_rates_db"
        ).build()
    }

    @Provides
    fun provideExchangeRateDao(db: ExchangeRateDatabase): ExchangeRateDao {
        return db.exchangeRateDao()
    }

    @Provides
    @Singleton
    fun provideGetExchangeRatesUseCase(repository: ExchangeRatesRepository): GetExchangeRatesUseCase {
        return GetExchangeRatesUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideExchangeRatesRepository(
        apiService: OpenExchangeRatesApi,
        exchangeRateDao: ExchangeRateDao,
        networkUtils: NetworkUtils,
        sharedPreferenceUtils: SharedPreferenceUtils
    ): ExchangeRatesRepository {
        return ExchangeRatesRepositoryImpl(
            apiService,
            exchangeRateDao,
            networkUtils,
            sharedPreferenceUtils
        )
    }

    @Provides
    @Singleton
    fun provideNetworkUtils(@ApplicationContext context: Context): NetworkUtils {
        return NetworkUtils(context)
    }

    @Provides
    @Singleton
    fun provideSharedPreferenceUtils(@ApplicationContext context: Context): SharedPreferenceUtils {
        return SharedPreferenceUtils(context)
    }

}
