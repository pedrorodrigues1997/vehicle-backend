import json
import time
import paho.mqtt.client as mqtt

broker = "localhost"
port = 1883

client_id = "car-002"
username = "car-002"
password = "123TestPwd"
topic = "vehicles/car-002/telemetry"

client = mqtt.Client(client_id=client_id)
client.username_pw_set(username, password)

def publish_telemetry():

    telemetry_data = {
        "speed": 72.5,
        "rpm": 3500,
        "battery": 58.3,
        "location": {"lat": 40.7128, "lon": -74.0060},
        "timestamp": int(time.time())
    }
    payload = json.dumps(telemetry_data)
    result = client.publish(topic, payload)


    status = result[0]
    if status == 0:
        print(f"Sent telemetry to topic `{topic}`: {payload}")
    else:
        print(f"Failed to send message to topic {topic}")

def main():
    client.connect(broker, port)
    client.loop_start()

    try:
        while True:
            publish_telemetry()
            time.sleep(5)
    except KeyboardInterrupt:
        print("Exiting...")
    finally:
        client.loop_stop()
        client.disconnect()

if __name__ == "__main__":
    main()
