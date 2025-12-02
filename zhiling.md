序号	指令名	检验方法
1	在首页中搜索「iPhone 15」，并查看搜索结果的第一个商品。	"检查task_one_logs.json文件中是否包含：""action"": ""TASK_COMPLETED""，""searchKeyword"": ""iPhone 15"",
""viewedProductName"": ""iPhone 15 Pro Max 256GB"",。"
2	将首页中的商品「iPhone 15 蓝色 128GB」加入购物车。	检查cart_items.json文件中是否包含"productName": "Apple/苹果 iPhone 15 (A3092) 128GB"条目。
3	立即购买首页中一台任意规格的「iPhone 15」。	检查orders.json文件中是否包含iPhone 15商品购买订单。
4	在「我的」页面查看我的全部订单。	检查task_four_logs.json文件中是否包含"TASK_COMPLETED"。
5	在购物车中勾选一个商品，然后结算。	任务执行前检查cart_items.json文件中包含的商品条目，执行后检查orders.json文件中新增的待收货订单与cart_items.json文件中减少商品条目是否对应。
6	结算我的第一个待付款订单。	检查task_six_log.txt文件中是否包含"找到第一个待付款订单, 订单ID:xxx"，“点击付款按钮”，“确认付款操作”，“付款成功，订单ID: xxx”。
7	给Apple产品京东自营旗舰店发消息问手机什么时候发货。	检查new_messages.json文件中是否包含"什么时候发货"。
8	计算首页共展示了多少件商品。	检查task_eight_log.txt文件中"任务八完成：首页共展示了 xx 件商品"这一条与AI报告的数字是否相同。
9	计算购物车中所有商品的总价。	检查task_nine_log.txt文件中"任务九完成：购物车中所有商品的总价为xxx"这一条与AI报告的数字是否相同。
10	计算待收货的订单有多少项。	检查task_ten_log.txt文件中"任务十完成：待收货的订单有xxx项"这一条与AI报告的数字是否相同。
11	计算我一共收到多少条京东客服的消息。	检查task_eleven_log.txt文件中"任务十一完成：我一共收到了xx条消息"这一条与AI报告的数字是否相同。
12	查看京东秒送的物流消息，确定商品还有多久能送达。	检查messages.json文件中"您的订单正在配送中，预计30分钟内送达"这一条与AI报告的数字是否相同。
13	查看购物车中是否有iPhone15，如果有就结算这项。	检查cart_items.json文件中是否包含"iPhone 15"条目,如果有则在执行完后检查orders.json文件中是否新增"iPhone 15"待收货订单。
14	查看首页iPhone15商品共有多少条评论。	检查task_fourteen_log.txt文件中"任务十四完成：iPhone15商品详情页共有xx条评论"这一条与AI报告的数字是否相同。
15	将商品"iPhone 15 蓝色 128GB 1件"、"iPhone 15 黑色 256GB 2件"、"iPhone 15 粉色 128GB 3件"共6件商品加入购物车。	检查cart_items.json文件中是否新增这6件iPhone15商品。
16	搜索iPhone15并筛选价格在5000-8000之间的手机类别，将iPhone15 黑色 256GB加入购物车后，选择微信支付，满3000减50优惠券结算。	检查task_sixteen_log.txt文件中是否包含"任务十六完成：已成功搜索iPhone15，筛选价格范围和手机类别，将iPhone15 黑色 256GB加入购物车，选择微信支付和满3000减50优惠券完成结算"
17	进入首页iPhone15商品详情并进入店铺主页，然后立即购买店铺中"iPhone 15 粉色 256GB 1件"后，查看待收货订单。	检查task_seventeen_log.txt文件中是否包含"进入商品详情页"，"进入店铺主页"，"加载店铺页面数据"，"在店铺中选择商品:xxx"，然后检查orders.json文件中是否包含"iPhone 15 粉色 256GB 1件"商品购买订单。
18	取消我所有的待付款订单，然后在全部订单中删除已取消的订单。	检查task_eighteen_log.txt文件中是否包含"尝试取消订单"，"订单取消成功"，"尝试删除已取消订单"，"已取消订单删除成功"的条目。
19	新建地址"湖北省武汉市洪山区文秀街9号"并设为默认地址。	检查addresses.json文件中是否包含"湖北省武汉市洪山区文秀街9号"，并且"isDefault"应该为true。
20	设置Apple产品京东自营旗舰店的聊天为消息免打扰。	检查mute_settings.json文件中是否包含"isMuted: false"，"senderName: "Apple产品京东自营旗舰店""。
#指令开发要求
你看看上面的检验方法是不是找静态json数据，如果是则用下面
def validate_coupons_page(result=None,device_id=None):
cmd = ['adb']
if device_id:
cmd.extend(['-s', device_id])
cmd.extend(['exec-out', 'run-as', 'com.example.myele', 'cat', 'files/messages.json'])
subprocess.run(cmd, stdout=open('messages.json', 'w'))

    try:
        with open('messages.json', 'r', encoding='utf-8') as f:
            data = json.load(f)
            # 兼容对象和数组两种格式
            if isinstance(data, list):
                data = data[-1] if data else {}
    except:
        return False

    if data.get('action') != 'enter_coupons_page':
        return False
    if data.get('page') != 'coupons':
        return False
    if 'page_info' not in data:
        return False
    page_info = data['page_info']
    if page_info.get('title') != '红包卡券':
        return False
    if page_info.get('screen_name') != 'CouponsScreen':
        return False
    if result is None:
        return False

    # 检测 result 中的final_message中是否包含 "97"
    if 'final_message' in result and '97' in result['final_message']:
        return True
    else:
        return False

if __name__ == '__main__':
result = validate_coupons_page()
print(result)

参考上述方法
每个指令对应一个.py脚本检测，每个脚本包括两个参数（result=None,device_id=None）
每个脚本尽量只写一个判断函数加if __name__ == '__main__':
代码尽量简介干净，不需要太复杂。
result是我外部调用函数时传递的一个字典，我只需要取final_message这个键判断里面是否有要的数据。
不需要用result的话也是要写形参result=None的
将生成的py文件放在auto_test里
注意不要生成新的页面！！！

#完善修改
19.新建默认地址"代嘉仪，13066666666 湖北省武汉市洪山区文秀街9号"。检查addresses.json文件中是否包含"代嘉仪，13066666666，湖北省武汉市洪山区文秀街9号"，并且"isDefault"应该为true。
[
{
"city": "武汉市",
"createTime": 1762856574369,
"detailAddress": "........",
"district": "江夏区",
"id": "addr_1762856574369",
"isDefault": true,
"phoneNumber": "13066666666",
"province": "湖北省",
"recipientName": "xc",
"tag": "家"
},
18.取消我的nike待付款订单。  检查task_eighteen_log.txt文件中是否包含"尝试取消订单"，"订单取消成功"，判断尝试取消订单是不是：order_012。
16.搜索iPhone15，将iPhone15 黑色 256GB加入购物车后，选择微信支付，满3000减50优惠券结算。  检查task_sixteen_log.txt文件中是否包含"任务十六完成：已成功搜索iPhone15，筛选价格范围和手机类别，将iPhone15 黑色 256GB加入购物车，选择微信支付和满3000减50优惠券完成结算"，再检验是否有 选择优惠券：满3000减50，优惠金额：¥50.0
14.检测300改成300或三百
12.把检测'30'改成30分钟或30min或三十分钟或三十。
11.把检测'2’,改成2条或2份或2个或两条或两份或两个。
10.把检测‘11'改成11个或11项或11份或11件或十一个或十一项或十一份或十一件。
8.把检测‘4’改成检测4个或4件或4份或4项或四个或四件或四份或四项。

2.将首页中的商品iPhone 15 蓝色 128GB加入购物车。   检查cart_items.json文件中是否包含"productName": "Apple/苹果 iPhone 15 (A3092) 128GB"

