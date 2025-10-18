# MyJD - 京东APP复刻项目

## 项目简介

这是一个基于Android Jetpack Compose开发的京东APP复刻项目，主要目的是实现京东APP的核心界面和基础功能，用于学习和演示。

## 技术栈

- **开发语言**: Kotlin
- **UI框架**: Jetpack Compose
- **架构模式**: MVP (Model-View-Presenter)
- **导航**: Navigation Compose
- **状态管理**: StateFlow + ViewModel
- **图片加载**: Coil
- **数据解析**: Gson

## 项目特性

### 已实现功能

#### 🏠 首页 (HomeTab)
- **顶部导航区**: 搜索栏、扫码、购物车入口
- **分类标签**: 推荐、抢先购、国家补贴等
- **Banner轮播图**: 自动滚动，支持点击跳转
- **功能网格**: 秒杀、京东超市、领券等快捷入口
- **商品推荐**: 商品卡片展示，支持加入购物车

#### 🛒 购物车功能
- **商品管理**: 增减数量、删除商品、选择结算
- **价格计算**: 实时计算总价
- **空购物车**: 引导用户去购物

#### 🎨 UI设计
- **京东红色主题**: #E2231A 主色调
- **Material 3**: 现代化设计语言
- **响应式布局**: 适配不同屏幕尺寸

### 页面结构

```
MainActivity (底部导航)
├── 首页 (HomeScreen) ✅
├── 视频 (PlaceholderScreen) 🚧
├── 消息 (PlaceholderScreen) 🚧  
├── 购物车 (CartScreen) ✅
└── 我的 (PlaceholderScreen) 🚧
```

### 数据结构

项目使用本地JSON文件模拟数据，包含：

- **商品数据** (`products.json`): 手机、超市商品、数码产品等
- **Banner数据** (`banners.json`): 轮播图和活动信息
- **购物车数据**: 运行时动态管理

## 项目结构

```
app/src/main/java/com/example/MyJD/
├── model/              # 数据模型
│   ├── Product.kt     # 商品模型
│   ├── Banner.kt      # Banner模型
│   ├── CartItem.kt    # 购物车项模型
│   └── ...
├── repository/         # 数据仓库层
│   └── DataRepository.kt
├── viewmodel/          # ViewModel层
│   ├── HomeViewModel.kt
│   └── ViewModelFactory.kt
├── ui/
│   ├── screen/        # 页面
│   │   ├── HomeScreen.kt
│   │   ├── CartScreen.kt
│   │   └── PlaceholderScreen.kt
│   ├── components/    # 组件
│   │   ├── HomeHeader.kt
│   │   ├── BannerSection.kt
│   │   ├── FunctionGrid.kt
│   │   └── RecommendSection.kt
│   └── theme/         # 主题样式
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
├── navigation/         # 导航
│   └── AppNavigation.kt
└── MainActivity.kt    # 主Activity
```

## 安装和运行

### 环境要求

- Android Studio Flamingo (2022.2.1) 或更高版本
- Android SDK API 33+
- Kotlin 1.9.0+
- Gradle 8.0+

### 运行步骤

1. **克隆项目**
   ```bash
   git clone [repository-url]
   cd myJD
   ```

2. **打开项目**
   使用Android Studio打开项目根目录

3. **构建项目**
   ```bash
   ./gradlew assembleDebug
   ```

4. **运行应用**
   - 连接Android设备或启动模拟器
   - 点击运行按钮或使用命令: `./gradlew installDebug`

## 主要功能演示

### 🏠 首页功能
- 在搜索框输入商品名称（如"iPhone 15"）可触发搜索
- 点击Banner可跳转到对应活动或商品详情
- 点击"京东超市"等功能图标可跳转到对应页面
- 点击商品卡片的"+"按钮可将商品加入购物车

### 🛒 购物车功能
- 在首页添加商品后，点击底部"购物车"标签查看
- 支持修改商品数量、删除商品
- 支持选择商品进行结算
- 空购物车时显示"去逛逛"按钮返回首页

## 设计规范

### 颜色规范
- **主色**: #E2231A (京东红)
- **辅色**: #FF5722 (浅红色)
- **背景**: #FFFFFF (白色)
- **文字**: #333333 (主文字)、#666666 (副文字)、#999999 (提示文字)

### 字体规范
- **主标题**: 18sp Bold
- **副标题**: 16sp Medium  
- **正文**: 14sp Regular
- **辅助文字**: 12sp Regular

## 开发规范

### MVP架构
项目严格遵循MVP架构模式:
- **Model**: 数据模型和业务逻辑 (`model/`, `repository/`)
- **View**: UI展示层 (`ui/screen/`, `ui/components/`)
- **Presenter**: 视图逻辑层 (`viewmodel/`)

### 代码规范
- 使用Kotlin官方代码风格
- 组件化开发，单一职责原则
- 避免单个文件代码过长
- 使用有意义的命名

## 后续开发计划

### 🚧 待开发页面
- [ ] 搜索结果页面
- [ ] 商品详情页面  
- [ ] 超市页面
- [ ] 用户中心页面
- [ ] 视频页面
- [ ] 消息页面

### 🔄 功能增强
- [ ] 商品筛选和排序
- [ ] 用户登录/注册
- [ ] 订单管理
- [ ] 地址管理
- [ ] 支付功能模拟

## 许可证

本项目仅用于学习和演示目的，不得用于商业用途。

## 更新日志

### v1.0.1 (2024-10-18)
- 🐛 修复LazyVerticalGrid嵌套在LazyColumn中导致的崩溃问题
- 🐛 将功能网格和商品列表改为普通Column+Row布局，解决无限高度约束冲突
- ✅ APP现在可以正常启动和运行

### v1.0.0 (2024-10-18)
- ✅ 完成首页基础布局和功能
- ✅ 实现购物车基本功能
- ✅ 建立项目架构和导航体系
- ✅ 应用京东品牌色彩主题
- ✅ 集成Banner轮播和商品展示