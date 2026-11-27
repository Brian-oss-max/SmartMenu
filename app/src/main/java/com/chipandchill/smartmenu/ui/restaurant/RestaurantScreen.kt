package com.chipandchill.smartmenu.ui.restaurant

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chipandchill.smartmenu.data.entities.OrderEntity
import com.chipandchill.smartmenu.data.entities.Product
import com.chipandchill.smartmenu.viewmodel.OrderViewModel
import com.chipandchill.smartmenu.viewmodel.ProductViewModel
import java.util.Locale

private val Rojo = Color(0xFFE6372A)
private val Amarillo = Color(0xFFF5D83A)
private val Fondo = Color(0xFFFFFBF4)
private val Verde = Color(0xFF20A86B)
private val TextoOscuro = Color(0xFF292929)

@Composable
fun RestaurantScreen(
    productViewModel: ProductViewModel,
    orderViewModel: OrderViewModel
) {
    val products by productViewModel.products.collectAsState()
    val pendingOrders by orderViewModel.pendingOrders.collectAsState()

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(Fondo)
            .padding(20.dp)
    ) {

        // CATÁLOGO DE PRODUCTOS
        Column(
            modifier = Modifier
                .weight(0.68f)
                .fillMaxSize()
        ) {

            Text(
                text = "CHIP & CHILL",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Rojo
            )

            Text(
                text = "Administración de productos",
                fontSize = 18.sp,
                color = TextoOscuro
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "MENÚ DE PRODUCTOS",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoOscuro
                )

                Text(
                    text = "Productos: ${products.size}",
                    fontSize = 15.sp,
                    color = TextoOscuro
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (products.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Cargando productos...")
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = products,
                        key = { it.id }
                    ) { product ->

                        ProductAdminCard(
                            product = product,
                            onAvailabilityChange = { available ->
                                productViewModel.updateAvailability(
                                    product.id,
                                    available
                                )
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.width(18.dp))

        // PANEL DE PEDIDOS
        Column(
            modifier = Modifier
                .weight(0.32f)
                .fillMaxSize()
                .background(
                    Color.White,
                    RoundedCornerShape(18.dp)
                )
                .padding(16.dp)
        ) {

            Text(
                text = "PEDIDOS PENDIENTES",
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                color = Rojo
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Pedidos por entregar: ${pendingOrders.size}",
                fontSize = 15.sp,
                color = TextoOscuro
            )

            Spacer(modifier = Modifier.height(14.dp))

            if (pendingOrders.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay pedidos pendientes",
                        color = Color.Gray,
                        fontSize = 17.sp
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = pendingOrders,
                        key = { it.id }
                    ) { order ->

                        PendingOrderCard(
                            order = order,
                            onDelivered = {
                                orderViewModel.markAsDelivered(order.id)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductAdminCard(
    product: Product,
    onAvailabilityChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Column {
                Text(
                    text = product.category.uppercase(),
                    color = Rojo,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = product.name,
                    color = TextoOscuro,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = formatPrice(product.price),
                    color = Verde,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = {
                    onAvailabilityChange(!product.available)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (product.available) Verde else Rojo
                )
            ) {
                Text(
                    text = if (product.available) {
                        "DISPONIBLE"
                    } else {
                        "AGOTADO"
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun PendingOrderCard(
    order: OrderEntity,
    onDelivered: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Fondo
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {

            Text(
                text = "Pedido #${order.id}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Rojo
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Cliente: ${order.customerName}",
                fontSize = 15.sp,
                color = TextoOscuro
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Total: ${formatPrice(order.total)}",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Verde
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onDelivered,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Verde
                )
            ) {
                Text(
                    text = "CONFIRMAR ENTREGA",
                    fontSize = 11.sp
                )
            }
        }
    }
}

private fun formatPrice(price: Double): String {
    return String.format(
        Locale("es", "MX"),
        "$%.2f",
        price
    )
}

