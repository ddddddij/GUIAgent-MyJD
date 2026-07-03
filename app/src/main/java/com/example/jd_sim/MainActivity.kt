package com.example.jd_sim

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.jd_sim.ui.navigation.AppNavigation
import com.example.jd_sim.domain.repository.DataRepository
import com.example.jd_sim.ui.theme.MyJDTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyJDTheme {
                MainScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current
    val repository = remember { DataRepository.getInstance(context) }
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    // 使用StateFlow响应式获取购物车数量
    val cartCount by repository.cartCountFlow.collectAsStateWithLifecycle()
    
    // 日志记录购物车数量变化
    LaunchedEffect(cartCount) {
        android.util.Log.d("MainActivity", "Cart count updated via StateFlow: $cartCount")
    }
    
    
    val bottomNavItems = listOf(
        BottomNavItem("home", "首页", Icons.Filled.Home, selectedIcon = Icons.Filled.Home),
        BottomNavItem("video", "视频", Icons.Filled.SmartDisplay, selectedIcon = Icons.Filled.SmartDisplay),
        BottomNavItem("chat", "消息", Icons.Filled.Textsms, selectedIcon = Icons.Filled.Textsms),
        BottomNavItem("cart", "购物车", Icons.Filled.ShoppingCart, selectedIcon = Icons.Filled.ShoppingCart, badgeCount = cartCount),
        BottomNavItem("profile", "我的", Icons.Filled.PersonOutline, selectedIcon = Icons.Filled.Person)
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFFF4F5F7),
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 8.dp,
                tonalElevation = 0.dp
            ) {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 0.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    bottomNavItems.forEach { item ->
                        val isSelected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                        NavigationBarItem(
                            icon = { 
                                if (item.route == "cart" && cartCount > 0) {
                                    CartIconWithBadge(
                                        icon = if (isSelected) item.selectedIcon else item.icon,
                                        count = cartCount
                                    )
                                } else if (item.badgeCount > 0) {
                                    IconWithBadge(
                                        icon = if (isSelected) item.selectedIcon else item.icon,
                                        count = item.badgeCount
                                    )
                                } else {
                                    StandardNavIcon(
                                        imageVector = if (isSelected) item.selectedIcon else item.icon,
                                        contentDescription = item.label
                                    )
                                }
                            },
                            label = { Text(item.label, fontSize = 11.sp) },
                            selected = isSelected,
                            onClick = {
                                val shouldNavigate = when {
                                    item.route == "profile" -> true
                                    item.route == "home" -> currentDestination?.route != "home"
                                    else -> currentDestination?.route != item.route
                                }
                                
                                if (shouldNavigate) {
                                    navController.navigate(item.route) {
                                        if (item.route == "home") {
                                            popUpTo(0) { inclusive = true }
                                        } else {
                                            popUpTo("home") {
                                                saveState = item.route != "profile"
                                                inclusive = false
                                            }
                                        }
                                        launchSingleTop = true
                                        restoreState = item.route != "profile"
                                    }
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFFE34B49),
                                selectedTextColor = Color(0xFF222222),
                                indicatorColor = Color.Transparent,
                                unselectedIconColor = Color(0xFF474747),
                                unselectedTextColor = Color(0xFF656565)
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            AppNavigation(
                navController = navController
            )
        }
    }
}

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
    val badgeCount: Int = 0
)

@Composable
fun CartIconWithBadge(
    icon: ImageVector,
    count: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "购物车",
            modifier = Modifier.size(24.dp)
        )
        
        if (count > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 10.dp, y = (-6).dp)
                    .size(18.dp)
                    .background(
                        color = Color(0xFFE34B49),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (count > 99) "99+" else count.toString(),
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun IconWithBadge(
    icon: ImageVector,
    count: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 10.dp, y = (-6).dp)
                .background(Color(0xFFE34B49), RoundedCornerShape(999.dp))
                .padding(horizontal = 6.dp, vertical = 1.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (count > 99) "99+" else count.toString(),
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun StandardNavIcon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        modifier = modifier.size(24.dp)
    )
}
