package com.example.currencyconvertorapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.currencyconvertorapp.domain.model.Currency


@Database(entities = [Currency::class], version = 1, exportSchema = false)
abstract class ExchangeRateDatabase : RoomDatabase() {
    abstract fun exchangeRateDao(): ExchangeRateDao
}
