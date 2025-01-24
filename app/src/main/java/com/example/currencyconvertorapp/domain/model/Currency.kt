package com.example.currencyconvertorapp.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currency")
data class Currency(
    @PrimaryKey val code: String,
    val rate: Double
)
