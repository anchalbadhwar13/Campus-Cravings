package com.example.campuscravings.ui.delivery

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.campuscravings.data.model.Order
import com.example.campuscravings.data.model.OrderStatus
import java.text.SimpleDateFormat
import java.util.*

// In your DeliveryDashboard Composable
import android.content.Intent
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import android.net.Uri


@Composable


fun DeliveryDashboardScreen(viewModel: DeliveryViewModel) {
    val availableOrders by viewModel.availableOrders.collectAsState()
    val activeOrders by viewModel.activeOrders.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }

    val context = LocalContext.current // get context once

    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "🚗 Delivery Dashboard",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Accept orders and start earning!",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                // ✅ Button to open Room Finder website
                Button(onClick = {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://roomfinder.sfu.ca/apps/sfuroomfinder_web/?q=ASB%209703")
                    )
                    context.startActivity(intent)
                }) {
                    Text("Open Room Finder")
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Tabs for Available / Active Orders
        TabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Available (${availableOrders.size})") }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Active (${activeOrders.size})") }
            )
        }

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            when (selectedTab) {
                0 -> AvailableOrdersList(
                    orders = availableOrders,
                    onAccept = { viewModel.acceptOrder(it) }
                )
                1 -> ActiveOrdersList(
                    orders = activeOrders,
                    onUpdateStatus = { orderId, status ->
                        viewModel.updateOrderStatus(orderId, status)
                    }
                )
            }
        }
    }
}

@Composable
fun AvailableOrdersList(
    orders: List<Order>,
    onAccept: (Order) -> Unit
) {
    if (orders.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No available orders",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(orders) { order ->
                AvailableOrderCard(
                    order = order,
                    onAccept = { onAccept(order) }
                )
            }
        }
    }
}

@Composable
fun AvailableOrderCard(
    order: Order,
    onAccept: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = order.restaurantName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "📍 ${order.restaurantLocation}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "$${String.format("%.2f", order.deliveryFee)}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Deliver to: ${order.deliveryLocation}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Customer: ${order.customerName}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (order.specialInstructions.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Note: ${order.specialInstructions}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onAccept,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Accept Order")
            }
        }
    }
}

@Composable
fun ActiveOrdersList(
    orders: List<Order>,
    onUpdateStatus: (String, OrderStatus) -> Unit
) {
    if (orders.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No active deliveries",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(orders) { order ->
                ActiveOrderCard(
                    order = order,
                    onUpdateStatus = { status -> onUpdateStatus(order.id, status) }
                )
            }
        }
    }
}

@Composable
fun ActiveOrderCard(
    order: Order,
    onUpdateStatus: (OrderStatus) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = order.restaurantName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "📍 ${order.restaurantLocation}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "$${String.format("%.2f", order.deliveryFee)}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Deliver to: ${order.deliveryLocation}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Customer: ${order.customerName}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            order.items.forEach { item ->
                Text(
                    text = "• ${item.name} x${item.quantity}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (order.specialInstructions.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Note: ${order.specialInstructions}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            when (order.status) {
                OrderStatus.ACCEPTED -> {
                    Button(
                        onClick = { onUpdateStatus(OrderStatus.PICKED_UP) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Mark as Picked Up")
                    }
                }
                OrderStatus.PICKED_UP -> {
                    Button(
                        onClick = { onUpdateStatus(OrderStatus.DELIVERED) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Mark as Delivered")
                    }
                }
                else -> {}
            }
        }
    }
}