import requests

# 發送 GET 請求到 Java API
response = requests.get("http://localhost:8080/")
print("GET Response:", response.json())

# 發送 POST 請求到 Java API
data = {"key": "value", "another_key": 123}
response = requests.post("http://localhost:8080/", json=data)
print("POST Response:", response.text)