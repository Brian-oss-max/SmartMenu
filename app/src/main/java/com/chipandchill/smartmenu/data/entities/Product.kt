package com.chipandchill.smartmenu.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,

    val category: String,

    val description: String = "",

    val price: Double,

    val available: Boolean = true,

    val imageName: String? = null
)
