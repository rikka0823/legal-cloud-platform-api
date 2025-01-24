import json

# JSON 檔案路徑
file_path = r"D:\JavaProject\SmartLegalSearch\py\legal literature\TPCM,89,台覆,175,20000824.json"

# 讀取 JSON 檔案
with open(file_path, "r", encoding="utf-8") as file:
    data = json.load(file)

# 遞歸函數：遍歷 JSON 結構，提取所有的值
def extract_values(data):
    values = []

    if isinstance(data, dict):  # 如果當前是字典，遍歷每一個鍵值對
        for key, value in data.items():
            values.append(value)
            values.extend(extract_values(value))  # 遞歸處理子項
    elif isinstance(data, list):  # 如果當前是列表，遍歷列表中的每個元素
        for item in data:
            values.extend(extract_values(item))  # 遞歸處理列表中的項目
    else:
        # 如果是基礎資料型別，直接加入列表
        values.append(data)
    
    return values

# 提取所有的值
values = extract_values(data)

# 輸出所有的值
print("Extracted Values:")
for value in values:
    print(value)