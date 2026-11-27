
package com.chipandchill.smartmenu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.chipandchill.smartmenu.data.database.SmartMenuDatabase
import com.chipandchill.smartmenu.data.repository.SmartMenuRepository
import com.chipandchill.smartmenu.ui.restaurant.RestaurantScreen
import com.chipandchill.smartmenu.viewmodel.OrderViewModel
import com.chipandchill.smartmenu.viewmodel.ProductViewModel
import com.chipandchill.smartmenu.viewmodel.SmartMenuViewModelFactory
import com.chipandchill.smartmenu.ui.client.ClientScreen

private val Rojo = Color(0xFFE6372A)
private val Amarillo = Color(0xFFF5D83A)
private val Fondo = Color(0xFFFFFBF4)
private val Verde = Color(0xFF20A86B)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar base de datos
        val database = SmartMenuDatabase.getDatabase(
            applicationContext
        )

        // Crear repositorio
        val repository = SmartMenuRepository(
            database.menuDao()
        )

        // Crear fábrica de ViewModels
        val factory = SmartMenuViewModelFactory(repository)

        // Crear ProductViewModel
        val productViewModel = ViewModelProvider(
            this,
            factory
        )[ProductViewModel::class.java]

        // Crear OrderViewModel
        val orderViewModel = ViewModelProvider(
            this,
            factory
        )[OrderViewModel::class.java]

        setContent {
            SmartMenuApp(
                productViewModel = productViewModel,
                orderViewModel = orderViewModel
            )
        }
    }
}

enum class Pantalla {
    INICIO,
    RESTAURANTE,
    CLIENTE
}

@Composable
fun SmartMenuApp(
    productViewModel: ProductViewModel,
    orderViewModel: OrderViewModel
) {

    var pantalla by remember {
        mutableStateOf(Pantalla.INICIO)
    }

    BackHandler(enabled = pantalla != Pantalla.INICIO) {
        pantalla = Pantalla.INICIO
    }

    MaterialTheme {

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Fondo
        ) {

            when (pantalla) {

                Pantalla.INICIO -> {
                    InicioScreen(
                        onRestaurante = {
                            pantalla = Pantalla.RESTAURANTE
                        },
                        onCliente = {
                            pantalla = Pantalla.CLIENTE
                        }
                    )
                }

                Pantalla.RESTAURANTE -> {
                    RestaurantScreen(
                        productViewModel = productViewModel,
                        orderViewModel = orderViewModel
                    )
                }

                Pantalla.CLIENTE -> {
                    ClientScreen(
                        productViewModel = productViewModel,
                        orderViewModel = orderViewModel,
                        onRegresar = {
                            pantalla = Pantalla.INICIO
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun InicioScreen(
    onRestaurante: () -> Unit,
    onCliente: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(35.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Chip & Chill",
            color = Rojo,
            fontSize = 48.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Text(
            text = "SNACK",
            color = Amarillo,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "SMART MENU",
            color = Color.DarkGray,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(45.dp))

        Text(
            text = "Selecciona una vista para continuar",
            fontSize = 19.sp,
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(30.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(35.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            BotonMenu(
                texto = "RESTAURANTE",
                descripcion = "Administrar productos y órdenes",
                color = Rojo,
                onClick = onRestaurante
            )

            BotonMenu(
                texto = "CLIENTE",
                descripcion = "Consultar menú y realizar pedido",
                color = Verde,
                onClick = onCliente
            )
        }
    }
}

@Composable
fun BotonMenu(
    texto: String,
    descripcion: String,
    color: Color,
    onClick: () -> Unit
) {

    var seleccionado by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .width(270.dp)
            .height(175.dp)
            .border(
                BorderStroke(
                    width = if (seleccionado) 4.dp else 1.dp,
                    color = if (seleccionado) Amarillo
                    else Color.LightGray
                ),
                RoundedCornerShape(18.dp)
            )
            .background(
                color,
                RoundedCornerShape(18.dp)
            )
            .onFocusChanged {
                seleccionado = it.isFocused
            }
            .focusable()
            .padding(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = texto,
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = descripcion,
            color = Color.White,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = color
            )
        ) {
            Text("ENTRAR")
        }
    }
}
