package com.chipandchill.smartmenu.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chipandchill.smartmenu.data.entities.OrderDetail
import com.chipandchill.smartmenu.data.repository.SmartMenuRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OrderViewModel(
    private val repository: SmartMenuRepository
) : ViewModel() {

    val pendingOrders = repository.pendingOrders.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allOrders = repository.allOrders.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun createOrder(
        customerName: String,
        details: List<OrderDetail>,
        onCreated: (Long) -> Unit = {}
    ) {
        viewModelScope.launch {
            val orderId = repository.createOrder(
                customerName,
                details
            )

            onCreated(orderId)
        }
    }

    fun markAsDelivered(orderId: Int) {
        viewModelScope.launch {
            repository.markOrderAsDelivered(orderId)
        }
    }
}