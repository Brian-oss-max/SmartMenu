
package com.chipandchill.smartmenu.ui.client

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import com.chipandchill.smartmenu.data.entities.OrderDetail
import com.chipandchill.smartmenu.data.entities.Product
import com.chipandchill.smartmenu.viewmodel.OrderViewModel
import com.chipandchill.smartmenu.viewmodel.ProductViewModel
import java.util.Locale

private val RojoCliente = Color(0xFFE6372A)
private val AmarilloCliente = Color(0xFFF5D83A)
private val FondoCliente = Color(0xFFFFFBF4)
private val VerdeCliente = Color(0xFF20A86B)
private val TextoCliente = Color(0xFF292929)

private data class CartItem(
    val product: Product,
    val quantity: Int
)

@Composable
fun ClientScreen(
    productViewModel: ProductViewModel,
    orderViewModel: OrderViewModel,
    onRegresar: () -> Unit
) {
    val availableProducts by productViewModel.availableProducts.collectAsState()

    var customerName by remember { mutableStateOf("") }
    var started by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("Todos") }
    var sendingOrder by remember { mutableStateOf(false) }

    val cart = remember { mutableStateListOf<CartItem>() }

    val categories = remember(availableProducts) {
        listOf("Todos") + availableProducts
            .map { it.category }
            .distinct()
    }

    val visibleProducts = if (selectedCategory == "Todos") {
        availableProducts
    } else {
        availableProducts.filter { it.category == selectedCategory }
    }

    val total = cart.sumOf {
        it.product.price * it.quantity
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = FondoCliente
    ) {
        if (!started) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "¡Bienvenido a Chip & Chill!",
                    color = RojoCliente,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Escribe tu nombre para comenzar",
                    fontSize = 21.sp,
                    color = TextoCliente
                )

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = customerName,
                    onValueChange = { customerName = it },
                    label = { Text("Nombre del cliente") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (customerName.isNotBlank()) {
                            started = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RojoCliente
                    )
                ) {
                    Text("COMENZAR PEDIDO")
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(onClick = onRegresar) {
                    Text("REGRESAR")
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                // CATÁLOGO DEL CLIENTE
                Column(
                    modifier = Modifier
                        .weight(0.68f)
                        .fillMaxSize()
                ) {
                    Text(
                        text = "¡Hola, $customerName!",
                        fontSize = 27.sp,
                        fontWeight = FontWeight.Bold,
                        color = RojoCliente
                    )

                    Text(
                        text = "Selecciona los productos que deseas",
                        fontSize = 16.sp,
                        color = TextoCliente
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // CATEGORÍAS
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(categories) { category ->
                            Button(
                                onClick = {
                                    selectedCategory = category
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (
                                        selectedCategory == category
                                    ) RojoCliente else Color.LightGray,
                                    contentColor = if (
                                        selectedCategory == category
                                    ) Color.White else TextoCliente
                                )
                            ) {
                                Text(category)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (visibleProducts.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "No hay productos disponibles en esta categoría."
                            )
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(
                                items = visibleProducts,
                                key = { it.id }
                            ) { product ->
                                ClientProductCard(
                                    product = product,
                                    onAdd = {
                                        val index = cart.indexOfFirst {
                                            it.product.id == product.id
                                        }

                                        if (index >= 0) {
                                            cart[index] = cart[index].copy(
                                                quantity = cart[index].quantity + 1
                                            )
                                        } else {
                                            cart.add(
                                                CartItem(product, 1)
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // CARRITO
                Column(
                    modifier = Modifier
                        .weight(0.32f)
                        .fillMaxSize()
                        .background(
                            Color.White,
                            RoundedCornerShape(16.dp)
                        )
                        .padding(14.dp)
                ) {
                    Text(
                        text = "MI PEDIDO",
                        fontSize = 22.sp,
                        color = RojoCliente,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Cliente: $customerName",
                        fontSize = 14.sp,
                        color = TextoCliente
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    if (cart.isEmpty()) {
                        Text(
                            text = "Tu carrito está vacío",
                            color = Color.Gray
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(
                                items = cart,
                                key = { it.product.id }
                            ) { item ->
                                CartItemRow(
                                    item = item,
                                    onIncrease = {
                                        val index = cart.indexOfFirst {
                                            it.product.id == item.product.id
                                        }

                                        if (index >= 0) {
                                            cart[index] = item.copy(
                                                quantity = item.quantity + 1
                                            )
                                        }
                                    },
                                    onDecrease = {
                                        val index = cart.indexOfFirst {
                                            it.product.id == item.product.id
                                        }

                                        if (index >= 0) {
                                            if (item.quantity > 1) {
                                                cart[index] = item.copy(
                                                    quantity = item.quantity - 1
                                                )
                                            } else {
                                                cart.removeAt(index)
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "TOTAL: ${formatClientPrice(total)}",
                        fontSize = 22.sp,
                        color = VerdeCliente,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            if (cart.isNotEmpty() && !sendingOrder) {
                                sendingOrder = true

                                val details = cart.map { item ->
                                    OrderDetail(
                                        orderId = 0,
                                        productId = item.product.id,
                                        productName = item.product.name,
                                        unitPrice = item.product.price,
                                        quantity = item.quantity,
                                        subtotal = item.product.price * item.quantity
                                    )
                                }

                                orderViewModel.createOrder(
                                    customerName = customerName.trim(),
                                    details = details,
                                    onCreated = {
                                        cart.clear()
                                        customerName = ""
                                        selectedCategory = "Todos"
                                        started = false
                                        sendingOrder = false
                                    }
                                )
                            }
                        },
                        enabled = cart.isNotEmpty() && !sendingOrder,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VerdeCliente
                        )
                    ) {
                        Text(
                            if (sendingOrder) {
                                "GUARDANDO..."
                            } else {
                                "CONFIRMAR PEDIDO"
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Button(
                        onClick = {
                            cart.clear()
                            customerName = ""
                            started = false
                            selectedCategory = "Todos"
                            sendingOrder = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = RojoCliente
                        )
                    ) {
                        Text("CANCELAR PEDIDO")
                    }
                }
            }
        }
    }
}

@Composable
private fun ClientProductCard(
    product: Product,
    onAdd: () -> Unit
) {
    var focused by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(145.dp)
            .onFocusChanged { focused = it.isFocused },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = if (focused) {
            BorderStroke(
                3.dp,
                AmarilloCliente
            )
        } else {
            null
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = product.name,
                    color = TextoCliente,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = formatClientPrice(product.price),
                    color = VerdeCliente,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = onAdd,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RojoCliente
                )
            ) {
                Text("AGREGAR")
            }
        }
    }
}

@Composable
private fun CartItemRow(
    item: CartItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                FondoCliente,
                RoundedCornerShape(8.dp)
            )
            .padding(8.dp)
    ) {
        Text(
            text = item.product.name,
            fontWeight = FontWeight.Bold,
            color = TextoCliente,
            fontSize = 14.sp
        )

        Text(
            text = formatClientPrice(
                item.product.price * item.quantity
            ),
            color = VerdeCliente,
            fontWeight = FontWeight.Bold
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onDecrease,
                colors = ButtonDefaults.buttonColors(
                    containerColor = RojoCliente
                )
            ) {
                Text("-")
            }

            Text(
                text = item.quantity.toString(),
                fontWeight = FontWeight.Bold
            )

            Button(
                onClick = onIncrease,
                colors = ButtonDefaults.buttonColors(
                    containerColor = VerdeCliente
                )
            ) {
                Text("+")
            }
        }
    }
}

private fun formatClientPrice(price: Double): String {
    return String.format(
        Locale("es", "MX"),
        "$%.2f",
        price
    )
}