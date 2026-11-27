package com.chipandchill.smartmenu.data.repository

import com.chipandchill.smartmenu.data.database.MenuDao
import com.chipandchill.smartmenu.data.entities.OrderDetail
import com.chipandchill.smartmenu.data.entities.OrderEntity
import com.chipandchill.smartmenu.data.entities.Product

class SmartMenuRepository(
    private val menuDao: MenuDao
) {

    val allProducts = menuDao.getAllProducts()

    val availableProducts = menuDao.getAvailableProducts()

    val pendingOrders = menuDao.getPendingOrders()

    val allOrders = menuDao.getAllOrders()

    suspend fun initializeProducts() {
        if (menuDao.getProductCount() == 0) {
            menuDao.insertProducts(initialProducts)
        }
    }

    suspend fun updateProductAvailability(
        productId: Int,
        available: Boolean
    ) {
        menuDao.updateProductAvailability(productId, available)
    }

    suspend fun createOrder(
        customerName: String,
        details: List<OrderDetail>
    ): Long {

        val total = details.sumOf { it.subtotal }

        val order = OrderEntity(
            customerName = customerName,
            total = total
        )

        return menuDao.createOrder(order, details)
    }

    suspend fun markOrderAsDelivered(orderId: Int) {
        menuDao.markOrderAsDelivered(orderId)
    }

    companion object {

        private val initialProducts = listOf(

            // PARA COMER
            Product(name = "Hot Dog mediano", category = "Para comer", price = 45.0),
            Product(name = "Hot Dog mediano en combo", category = "Para comer", price = 65.0),
            Product(name = "Hot Dog jumbo", category = "Para comer", price = 58.0),
            Product(name = "Hot Dog jumbo en combo", category = "Para comer", price = 78.0),
            Product(name = "Croissant jamón y queso", category = "Para comer", price = 35.0),
            Product(name = "Papas a la francesa", category = "Para comer", price = 45.0),
            Product(name = "Panini jamón", category = "Para comer", price = 65.0),
            Product(name = "Panini choriqueso", category = "Para comer", price = 65.0),
            Product(name = "Orden 3 tostadas frijoles con queso", category = "Para comer", price = 35.0),
            Product(name = "Ensalada de pollo con mayonesa", category = "Para comer", price = 50.0),

            // BEBIDAS
            Product(name = "Agua embotellada 500 ml", category = "Bebidas", price = 10.0),
            Product(name = "Agua embotellada 1 L", category = "Bebidas", price = 15.0),
            Product(name = "Refresco sabor 400 ml", category = "Bebidas", price = 18.0),
            Product(name = "Refresco Pepsi 400 ml", category = "Bebidas", price = 18.0),
            Product(name = "Arizona 460 ml", category = "Bebidas", price = 25.0),

            // ALGO CON FRUTA
            Product(name = "Charola fruta del día", category = "¿Algo con fruta?", price = 35.0),
            Product(name = "Vaso yogurt fruta 12 oz", category = "¿Algo con fruta?", price = 45.0),
            Product(name = "Fresas crema 9 oz", category = "¿Algo con fruta?", price = 45.0),

            // ELOTE
            Product(name = "Papas preparadas", category = "¿Mmm... Elote?", price = 50.0),
            Product(name = "Nachos charola", category = "¿Mmm... Elote?", price = 40.0),
            Product(name = "Elote vaso", category = "¿Mmm... Elote?", price = 35.0),

            // BOTANITAS
            Product(name = "Papas Sabritas", category = "Una botanita", price = 25.0),
            Product(name = "Bote loco papas", category = "Una botanita", price = 35.0),
            Product(name = "Bolsa chicharrines", category = "Una botanita", price = 25.0),
            Product(name = "Bolsa tostileo", category = "Una botanita", price = 25.0),
            Product(name = "Bolsa chetos", category = "Una botanita", price = 25.0),
            Product(name = "Bolsa papa natural", category = "Una botanita", price = 25.0),
            Product(name = "Bolsa cacahuates", category = "Una botanita", price = 20.0),
            Product(name = "Sopa Nissin", category = "Una botanita", price = 30.0),

            // GOMITAS Y DULCES
            Product(name = "Variedad gomitas y dulces", category = "Gomitas y dulces", price = 20.0)
        )
    }
}
