import json
import requests
import os

# 要發送的 JSON 資料
data = {
    "name": "Alice",
    "age": 25,
    "email": "alice@example.com"
}

# Java 伺服器的 URL
url = "http://localhost:8080/api/upload"

# 發送 POST 請求
try:
    headers = {"Content-Type": "application/json"}  # 設置請求頭
    response = requests.post(url, json=data, headers=headers)

    # 檢查伺服器回應
    if response.status_code == 200:
        print("Success:", response.text)
    else:
        print("Failed:", response.status_code, response.text)
except Exception as e:
    print("Error sending request:", e)



# 檔案路徑
file_path = r"D:\JavaProject\SmartLegalSearch\py\legal literature\TPCM,89,台覆,175,20000824.json"

# 讀取 JSON 檔案
try:
    with open(file_path, "r", encoding="utf-8") as file:
        data = json.load(file)  # 解析 JSON
        print(data)  # 輸出檔案內容
except FileNotFoundError:
    print(f"檔案未找到：{file_path}")
except json.JSONDecodeError as e:
    print(f"JSON 格式錯誤: {e}")



file_path = r"D:\JavaProject\SmartLegalSearch\py\legal literature\TPCM,89,台覆,175,20000824.json"

if os.path.exists(file_path):
    print("檔案存在，準備讀取")
else:
    print("檔案不存在，請確認路徑")


import json
import requests

# JSON 檔案路徑
file_path = r"D:\JavaProject\SmartLegalSearch\py\legal literature\TPCM,89,台覆,175,20000824.json"

# Java API 的 URL
java_server_url = "http://localhost:8080/api/upload"  # 替換為您的 Java 伺服器 API 地址

# 從檔案讀取 JSON 資料
try:
    with open(file_path, "r", encoding="utf-8") as file:
        json_data = json.load(file)

    # 發送 POST 請求
    headers = {"Content-Type": "application/json"}  # 設置請求標頭
    response = requests.post(java_server_url, json=json_data, headers=headers)

    # 檢查回應
    if response.status_code == 200:
        print("成功發送 JSON！伺服器回應：", response.text)
    else:
        print(f"發送失敗！狀態碼：{response.status_code}, 回應：{response.text}")

except FileNotFoundError:
    print(f"檔案未找到：{file_path}")
except json.JSONDecodeError as e:
    print(f"JSON 格式錯誤：{e}")
except Exception as e:
    print(f"發送過程中出錯：{e}")

