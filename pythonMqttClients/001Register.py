import paho.mqtt.client as mqtt
import time
import json

BROKER = "localhost"
PORT = 1883

USERNAME = "reg_user"
PASSWORD = "reg_password"
CLIENT_ID = "reg_client_123"

REGISTER_TOPIC = "vehicle/registration/request"

def on_connect(client, userdata, flags, rc):
    if rc == 0:
        print("Connected successfully")
    else:
        print(f"Connect failed with code {rc}")

def on_publish(client, userdata, mid):
    print(f"Message {mid} published.")

def main():
    client = mqtt.Client(client_id=CLIENT_ID)
    client.username_pw_set(USERNAME, PASSWORD)

    client.on_connect = on_connect
    client.on_publish = on_publish

    client.connect(BROKER, PORT, 60)
    client.loop_start()

    registration_data = {
        "vin": "car-005",
        "model": "Model S",
        "manufacturer": "Tesla",
        "firmwareVersion": "v10.2.1",
        "hardwareId": "HW-12345",
        "secretToken": "password123",
        "timestamp": int(time.time() * 1000)
    }
    payload = json.dumps(registration_data)

    result = client.publish(REGISTER_TOPIC, payload, qos=1)
    result.wait_for_publish()

    time.sleep(2)
    client.loop_stop()
    client.disconnect()

if __name__ == "__main__":
    main()
