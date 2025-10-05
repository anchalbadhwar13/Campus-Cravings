package com.example.campuscravings.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.campuscravings.data.model.UserRole
import com.example.campuscravings.ui.auth.AuthScreen
import com.example.campuscravings.ui.auth.AuthViewModel
import com.example.campuscravings.ui.customer.*
import com.example.campuscravings.ui.delivery.DeliveryDashboardScreen
import com.example.campuscravings.ui.delivery.DeliveryViewModel

sealed class Screen(val route: String) {
    object Auth : Screen("auth")
    object RestaurantList : Screen("restaurant_list")
    object Menu : Screen("menu")
    object Checkout : Screen("checkout")
    object Orders : Screen("orders")
    object DeliveryDashboard : Screen("delivery_dashboard")
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    
    val startDestination = if (currentUser != null) {
        when (currentUser?.role) {
            UserRole.CUSTOMER -> Screen.RestaurantList.route
            UserRole.DELIVERY -> Screen.DeliveryDashboard.route
            else -> Screen.Auth.route
        }
    } else {
        Screen.Auth.route
    }
    
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Auth.route) {
            AuthScreen(
                viewModel = authViewModel,
                onAuthSuccess = {
                    val user = authViewModel.currentUser.value
                    val destination = when (user?.role) {
                        UserRole.CUSTOMER -> Screen.RestaurantList.route
                        UserRole.DELIVERY -> Screen.DeliveryDashboard.route
                        else -> Screen.Auth.route
                    }
                    navController.navigate(destination) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.RestaurantList.route) {
            val parentEntry = remember(it) {
                navController.getBackStackEntry(Screen.RestaurantList.route)
            }
            val viewModel: CustomerViewModel = hiltViewModel(parentEntry)
            RestaurantListScreen(
                viewModel = viewModel,
                onRestaurantClick = { restaurant ->
                    viewModel.selectRestaurant(restaurant)
                    navController.navigate(Screen.Menu.route)
                },
                onOrdersClick = {
                    navController.navigate(Screen.Orders.route)
                }
            )
        }
        
        composable(Screen.Menu.route) {
            val parentEntry = remember(it) {
                navController.getBackStackEntry(Screen.RestaurantList.route)
            }
            val viewModel: CustomerViewModel = hiltViewModel(parentEntry)
            MenuScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onCheckout = { navController.navigate(Screen.Checkout.route) }
            )
        }
        
        composable(Screen.Checkout.route) {
            val parentEntry = remember(it) {
                navController.getBackStackEntry(Screen.RestaurantList.route)
            }
            val viewModel: CustomerViewModel = hiltViewModel(parentEntry)
            CheckoutScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onOrderPlaced = {
                    navController.navigate(Screen.RestaurantList.route) {
                        popUpTo(Screen.RestaurantList.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Orders.route) {
            val parentEntry = remember(it) {
                navController.getBackStackEntry(Screen.RestaurantList.route)
            }
            val viewModel: CustomerViewModel = hiltViewModel(parentEntry)
            OrdersScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.DeliveryDashboard.route) {
            val viewModel: DeliveryViewModel = hiltViewModel()
            DeliveryDashboardScreen(viewModel = viewModel)
        }
    }
}
