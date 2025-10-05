package com.example.campuscravings.ui.customer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.campuscravings.data.model.Order
import com.example.campuscravings.data.model.OrderStatus
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderTrackingScreen(
    viewModel: CustomerViewModel,
    orderId: String,
    onBack: () -> Unit,
    onDone: () -> Unit,
    onViewMap: () -> Unit = {}
) {
    val orders by viewModel.orders.collectAsState()
    val order = orders.find { it.id == orderId }
    
    // Auto-navigate when delivered
    LaunchedEffect(order?.status) {
        if (order?.status == OrderStatus.DELIVERED) {
            delay(2000) // Show "Delivered" for 2 seconds
            onDone()
        }
    }
    
    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface
            ) {
                TopAppBar(
                    title = { Text("Track Order") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    modifier = Modifier.statusBarsPadding(),
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        }
    ) { padding ->
        if (order == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Order Status Icon
                when (order.status) {
                    OrderStatus.PENDING -> {
                        CircularProgressIndicator(
                            modifier = Modifier.size(80.dp),
                            strokeWidth = 6.dp
                        )
                    }
                    OrderStatus.ACCEPTED, OrderStatus.PICKED_UP -> {
                        CircularProgressIndicator(
                            modifier = Modifier.size(80.dp),
                            strokeWidth = 6.dp,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                    OrderStatus.DELIVERED -> {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Delivered",
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    OrderStatus.CANCELLED -> {
                        Text(
                            text = "❌",
                            fontSize = 80.sp
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Status Text
                Text(
                    text = getStatusText(order.status),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = getStatusColor(order.status)
                )
                
                // View Map Button (only show when order is active)
                if (order.status in listOf(OrderStatus.ACCEPTED, OrderStatus.PICKED_UP)) {
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = onViewMap,
                        modifier = Modifier.fillMaxWidth(0.6f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "View Map",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("View Live Map")
                    }
                }
                
                // Status Description
                Text(
                    text = getStatusDescription(order.status, order.deliveryPersonName),
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Order Details Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Order Details",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Divider()
                        
                        DetailRow("Restaurant", order.restaurantName)
                        DetailRow("Delivery to", order.deliveryLocation)
                        DetailRow("Total", "$${String.format("%.2f", order.total)}")
                        
                        if (order.deliveryPersonName != null) {
                            DetailRow("Delivery by", order.deliveryPersonName)
                        }
                    }
                }
                
                // Timeline
                OrderTimeline(order)
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Action Button
                if (order.status == OrderStatus.DELIVERED) {
                    Button(
                        onClick = onDone,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Done")
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun OrderTimeline(order: Order) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Order Timeline",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        
        TimelineItem(
            title = "Order Placed",
            isCompleted = true,
            isCurrent = order.status == OrderStatus.PENDING
        )
        
        TimelineItem(
            title = "Accepted by Delivery Partner",
            isCompleted = order.status in listOf(OrderStatus.ACCEPTED, OrderStatus.PICKED_UP, OrderStatus.DELIVERED),
            isCurrent = order.status == OrderStatus.ACCEPTED
        )
        
        TimelineItem(
            title = "Food Picked Up",
            isCompleted = order.status in listOf(OrderStatus.PICKED_UP, OrderStatus.DELIVERED),
            isCurrent = order.status == OrderStatus.PICKED_UP
        )
        
        TimelineItem(
            title = "Delivered",
            isCompleted = order.status == OrderStatus.DELIVERED,
            isCurrent = order.status == OrderStatus.DELIVERED,
            isLast = true
        )
    }
}

@Composable
fun TimelineItem(
    title: String,
    isCompleted: Boolean,
    isCurrent: Boolean,
    isLast: Boolean = false
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Timeline indicator
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(24.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Surface(
                        modifier = Modifier.size(16.dp),
                        shape = MaterialTheme.shapes.small,
                        color = if (isCurrent) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.surfaceVariant
                    ) {}
                }
            }
            
            if (!isLast) {
                Divider(
                    modifier = Modifier
                        .width(2.dp)
                        .height(32.dp),
                    color = if (isCompleted) 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }
        
        // Timeline text
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
            color = if (isCompleted || isCurrent) 
                MaterialTheme.colorScheme.onSurface 
            else 
                MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun getStatusColor(status: OrderStatus): androidx.compose.ui.graphics.Color {
    return when (status) {
        OrderStatus.PENDING -> MaterialTheme.colorScheme.secondary
        OrderStatus.ACCEPTED -> MaterialTheme.colorScheme.tertiary
        OrderStatus.PICKED_UP -> MaterialTheme.colorScheme.primary
        OrderStatus.DELIVERED -> MaterialTheme.colorScheme.primary
        OrderStatus.CANCELLED -> MaterialTheme.colorScheme.error
    }
}

fun getStatusText(status: OrderStatus): String {
    return when (status) {
        OrderStatus.PENDING -> "Finding Delivery Partner"
        OrderStatus.ACCEPTED -> "Order Accepted"
        OrderStatus.PICKED_UP -> "On the Way!"
        OrderStatus.DELIVERED -> "Delivered!"
        OrderStatus.CANCELLED -> "Cancelled"
    }
}

fun getStatusDescription(status: OrderStatus, deliveryPersonName: String?): String {
    return when (status) {
        OrderStatus.PENDING -> "We're looking for a delivery partner near you..."
        OrderStatus.ACCEPTED -> "$deliveryPersonName has accepted your order and is heading to the restaurant"
        OrderStatus.PICKED_UP -> "$deliveryPersonName picked up your food and is on the way to you!"
        OrderStatus.DELIVERED -> "Enjoy your meal! 🎉"
        OrderStatus.CANCELLED -> "Your order was cancelled"
    }
}
