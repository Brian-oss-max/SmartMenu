package com.chipandchill.smartmenu.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val customerName: String,

    val total: Double,

    val status: String = "PENDIENTE",

    val createdAt: Long = System.currentTimeMillis()
)

