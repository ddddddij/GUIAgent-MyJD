package com.example.MyJD

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.MyJD.navigation.AppNavigation
import com.example.MyJD.repository.DataRepository
import com.example.MyJD.ui.theme.MyJDTheme
import com.example.MyJD.utils.TaskLogger
import com.example.MyJD.utils.TaskVerifier

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
    
    // 初始化TaskLogger并显示调试信息（仅启动时执行一次）
    LaunchedEffect(Unit) {
        TaskLogger.initializeLogging(context)
        // 延迟一秒后执行任务验证，确保应用完全启动
        kotlinx.coroutines.delay(1000)
        // 执行所有任务验证并输出到logcat
        TaskVerifier.verifyAllTasksAndLogToLogcat(context)
    }
    
    val bottomNavItems = listOf(
        BottomNavItem("home", "首页", Icons.Filled.Home),
        BottomNavItem("video", "视频", Icons.Filled.PlayArrow),
        BottomNavItem("chat", "消息", Icons.Filled.ChatBubble),
        BottomNavItem("cart", "购物车", Icons.Filled.ShoppingCart),
        BottomNavItem("profile", "我的", Icons.Filled.Person)
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        icon = { 
                            if (item.route == "cart" && cartCount > 0) {
                                CartIconWithBadge(
                                    icon = item.icon,
                                    count = cartCount
                                )
                            } else {
                                Icon(item.icon, contentDescription = item.label)
                            }
                        },
                        label = { Text(item.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                        onClick = {
                            // 总是导航到目标页面，即使是当前页面也刷新（除非完全相同）
                            val shouldNavigate = when {
                                // 对于我的页面，始终导航（刷新）
                                item.route == "profile" -> true
                                // 对于首页，如果不在首页就导航
                                item.route == "home" -> currentDestination?.route != "home"
                                // 对于其他页面，如果不在当前页面就导航
                                else -> currentDestination?.route != item.route
                            }
                            
                            if (shouldNavigate) {
                                navController.navigate(item.route) {
                                    // 对于首页，总是清除所有回退栈
                                    if (item.route == "home") {
                                        popUpTo(0) { inclusive = true }
                                    } else {
                                        // 对于其他页面，回到首页层级
                                        popUpTo("home") {
                                            saveState = item.route != "profile"
                                            inclusive = false
                                        }
                                    }
                                    launchSingleTop = true
                                    restoreState = item.route != "profile"
                                }
                            }
                        }
                    )
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
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
fun CartIconWithBadge(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    count: Int,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Icon(
            imageVector = icon,
            contentDescription = "购物车"
        )
        
        if (count > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 8.dp, y = (-8).dp)
                    .size(18.dp)
                    .background(
                        color = Color.Red,
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