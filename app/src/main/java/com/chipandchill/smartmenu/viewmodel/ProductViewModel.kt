package com.chipandchill.smartmenu.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chipandchill.smartmenu.data.repository.SmartMenuRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProductViewModel(
    private val repository: SmartMenuRepository
) : ViewModel() {

    val products = repository.allProducts.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val availableProducts = repository.availableProducts.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        viewModelScope.launch {
            repository.initializeProducts()
        }
    }

    fun updateAvailability(
        productId: Int,
        available: Boolean
    ) {
        viewModelScope.launch {
            repository.updateProductAvailability(
                productId,
                available
            )
        }
    }
}
