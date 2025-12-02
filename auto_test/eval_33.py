import json
import os
import subprocess

def validate_task_thirty_three(result=None, device_id=None, backup_dir=None):
    """
    验证任务三十三：比较Apple产品京东自营旗舰店和华为官方旗舰店的粉丝数，告诉我粉丝量更高的店铺名。
    验证逻辑：检查模型返回的内容是否为“Apple产品京东自营旗舰店”。
    """
    if result and "final_message" in result and result["final_message"] is not None:
        message = result["final_message"]
        # 检查返回结果中是否包含预期的店铺名
        if "Apple产品京东自营旗舰店" in message:
            print("Validation Success: The model correctly identified the store with more followers.")
            return True
        else:
            print(f"Validation Failed: The model's output was '{message}', which does not contain the expected store name 'Apple产品京东自营旗舰店'.")
            return False
    
    print("Validation Failed: No valid final_message found in the result.")
    return False

if __name__ == "__main__":
    pass