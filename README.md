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

#### 💬 消息页面 (ChatTab)
- **顶部导航栏**: 显示"消息"标题，右侧购物车和更多功能图标
- **消息分类标签**: 客服、物流、提醒、优惠、点评五个分类
- **次级筛选标签**: 全部、购物、秒送、外卖四个子分类
- **消息列表**: 静态消息数据展示，包含头像、发送方、内容摘要、时间
- **官方标识**: 官方消息显示红色"官方"标签
- **空状态**: 无消息时显示占位图和提示文字
- **交互功能**: 
  - 支持消息分类和子分类切换
  - 点击消息项跳转到详情页面
  - 点击购物车图标跳转到购物车页面
  - 点击更多图标显示Toast提示

#### 🎨 UI设计
- **京东红色主题**: #E2231A 主色调
- **Material 3**: 现代化设计语言
- **响应式布局**: 适配不同屏幕尺寸

### 页面结构

```
MainActivity (底部导航)
├── 首页 (HomeScreen) ✅
├── 视频 (PlaceholderScreen) 🚧
├── 消息 (ChatScreen) ✅  
├── 购物车 (CartScreen) ✅
└── 我的 (PlaceholderScreen) 🚧
```

### 数据结构

项目使用本地JSON文件模拟数据，包含：

- **商品数据** (`products.json`): 手机、超市商品、数码产品等
- **Banner数据** (`banners.json`): 轮播图和活动信息
- **消息数据** (`messages.json`): 客服、物流、促销等各类消息
- **购物车数据**: 运行时动态管理

## 项目结构

```
app/src/main/java/com/example/MyJD/
├── model/              # 数据模型
│   ├── Product.kt     # 商品模型
│   ├── Banner.kt      # Banner模型
│   ├── CartItem.kt    # 购物车项模型
│   ├── Message.kt     # 消息模型
│   └── ...
├── repository/         # 数据仓库层
│   └── DataRepository.kt
├── viewmodel/          # ViewModel层
│   ├── HomeViewModel.kt
│   ├── ChatViewModel.kt
│   └── ViewModelFactory.kt
├── ui/
│   ├── screen/        # 页面
│   │   ├── HomeScreen.kt
│   │   ├── ChatScreen.kt
│   │   ├── CartScreen.kt
│   │   └── PlaceholderScreen.kt
│   ├── components/    # 组件
│   │   ├── HomeHeader.kt
│   │   ├── BannerSection.kt
│   │   ├── FunctionGrid.kt
│   │   ├── RecommendSection.kt
│   │   ├── ChatTopBar.kt
│   │   ├── MessageTabs.kt
│   │   ├── SubTabs.kt
│   │   ├── MessageItem.kt
│   │   └── MessageList.kt
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

### 💬 消息页面功能
- 点击底部导航栏"消息"标签进入消息页面
- 支持在不同消息类型间切换（客服、物流、提醒、优惠、点评）
- 支持次级筛选（全部、购物、秒送、外卖）
- 点击消息项可跳转到消息详情页面（占位页面）
- 点击右上角购物车图标可跳转到购物车页面
- 点击右上角更多图标显示"更多功能待开发"提示

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
- [ ] 消息详情页面

### 🔄 功能增强
- [ ] 商品筛选和排序
- [ ] 用户登录/注册
- [ ] 订单管理
- [ ] 地址管理
- [ ] 支付功能模拟

## 许可证

本项目仅用于学习和演示目的，不得用于商业用途。

## 更新日志

### v1.0.2 (2024-10-19)
- ✅ 完成消息页面(ChatTab)开发
- ✅ 实现消息分类标签和次级筛选功能
- ✅ 创建消息数据模型和静态数据文件
- ✅ 开发消息列表组件，支持头像、官方标识、时间显示
- ✅ 集成消息页面到主导航，支持跳转到消息详情
- ✅ 修复已弃用的Divider组件警告
- 🎨 消息页面UI完全遵循京东设计规范

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