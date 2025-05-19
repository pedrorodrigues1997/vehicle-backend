import paho.mqtt.client as mqtt
import time
import json

# MQTT broker config
BROKER = "localhost"  # or your broker IP
PORT = 1883           # listener port (change to your no-auth listener port if different)

# Use a username with the "reg_user_" prefix to match your ACL rules
USERNAME = "reg_user"
PASSWORD = "reg_password"
CLIENT_ID = "reg_client_123"

# Topics
REGISTER_TOPIC = "vehicle/registration/request"
##REGISTER_TOPIC = "vehicles/car-002/telemetry"##

def on_connect(client, userdata, flags, rc):
    if rc == 0:
        print("Connected successfully")
    else:
        print(f"Connect failed with code {rc}")

def on_publish(client, userdata, mid):
    print(f"Message {mid} published.")

def main():
    client = mqtt.Client(client_id=CLIENT_ID)
    client.username_pw_set(USERNAME, PASSWORD)  # Add password here

    client.on_connect = on_connect
    client.on_publish = on_publish

    client.connect(BROKER, PORT, 60)
    client.loop_start()

    # Sample registration payload
    registration_data = {
        "vin": "car-003",
        "model": "Model S",
        "manufacturer": "Tesla",
        "firmwareVersion": "v10.2.1",
        "hardwareId": "HW-12345",
        "secretToken": "password123",
        "timestamp": int(time.time())
    }
    payload = json.dumps(registration_data)

    # Publish the registration request
    result = client.publish(REGISTER_TOPIC, payload, qos=1)
    result.wait_for_publish()

    time.sleep(2)  # wait a bit before disconnecting
    client.loop_stop()
    client.disconnect()

if __name__ == "__main__":
    main()
