package com.example.campuscravings.ui.customer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    viewModel: CustomerViewModel,
    onBack: () -> Unit,
    onOrderPlaced: (String) -> Unit
) {
    val restaurant by viewModel.selectedRestaurant.collectAsState()
    val menuItems by viewModel.menuItems.collectAsState()
    val cart by viewModel.cart.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    var deliveryLocation by remember { mutableStateOf("") }
    var specialInstructions by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var placedOrderId by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(placedOrderId) {
        placedOrderId?.let { orderId ->
            onOrderPlaced(orderId)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                modifier = Modifier.statusBarsPadding()
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Order Summary",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            items(cart.entries.toList()) { (itemId, quantity) ->
                val item = menuItems.find { it.id == itemId }
                if (item != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${item.name} x$quantity")
                        Text(
                            text = "$${String.format("%.2f", item.price * quantity)}",
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            
            item {
                Divider()
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Subtotal")
                    Text("$${String.format("%.2f", viewModel.getCartTotal())}")
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Delivery Fee")
                    Text("$${String.format("%.2f", restaurant?.deliveryFee ?: 0.0)}")
                }
                
                Divider()
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Total",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$${String.format("%.2f", viewModel.getCartTotal() + (restaurant?.deliveryFee ?: 0.0))}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Delivery Details",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            item {
                OutlinedTextField(
                    value = deliveryLocation,
                    onValueChange = { deliveryLocation = it },
                    label = { Text("Building / Location") },
                    placeholder = { Text("e.g., AQ Building, Room 3005") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            item {
                OutlinedTextField(
                    value = specialInstructions,
                    onValueChange = { specialInstructions = it },
                    label = { Text("Special Instructions (Optional)") },
                    placeholder = { Text("Any special requests?") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
            
            item {
                if (error != null) {
                    Text(
                        text = error!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                
                Button(
                    onClick = {
                        if (deliveryLocation.isNotBlank()) {
                            showDialog = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = deliveryLocation.isNotBlank() && !isLoading && placedOrderId == null
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Place Order")
                    }
                }
            }
        }
    }
    
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirm Order") },
            text = { Text("Are you sure you want to place this order?") },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false
                        viewModel.placeOrder(deliveryLocation, specialInstructions) { orderId ->
                            placedOrderId = orderId
                        }
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
