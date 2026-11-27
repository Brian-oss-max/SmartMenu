package com.chipandchill.smartmenu.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.chipandchill.smartmenu.data.entities.OrderDetail
import com.chipandchill.smartmenu.data.entities.OrderEntity
import com.chipandchill.smartmenu.data.entities.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface MenuDao {

    // PRODUCTOS

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<Product>)

    @Update
    suspend fun updateProduct(product: Product)

    @Query("SELECT * FROM products ORDER BY category, name")
    fun getAllProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE available = 1 ORDER BY category, name")
    fun getAvailableProducts(): Flow<List<Product>>

    @Query("UPDATE products SET available = :available WHERE id = :productId")
    suspend fun updateProductAvailability(
        productId: Int,
        available: Boolean
    )

    @Query("SELECT COUNT(*) FROM products")
    suspend fun getProductCount(): Int

    // PEDIDOS

    @Insert
    suspend fun insertOrder(order: OrderEntity): Long

    @Insert
    suspend fun insertOrderDetails(details: List<OrderDetail>)

    @Transaction
    suspend fun createOrder(
        order: OrderEntity,
        details: List<OrderDetail>
    ): Long {
        val orderId = insertOrder(order)

        val detailsWithOrderId = details.map {
            it.copy(orderId = orderId.toInt())
        }

        insertOrderDetails(detailsWithOrderId)

        return orderId
    }

    @Query("SELECT * FROM orders WHERE status = 'PENDIENTE' ORDER BY createdAt ASC")
    fun getPendingOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Query("UPDATE orders SET status = 'ENTREGADO' WHERE id = :orderId")
    suspend fun markOrderAsDelivered(orderId: Int)

    @Query("SELECT * FROM order_details WHERE orderId = :orderId")
    suspend fun getOrderDetails(orderId: Int): List<OrderDetail>
}
