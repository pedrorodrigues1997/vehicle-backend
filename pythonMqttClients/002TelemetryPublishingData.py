import json
import time
import random
import paho.mqtt.client as mqtt

broker = "localhost"
port = 1883

cars = [
    {
        "client_id": "car-001",
        "username": "car-001",
        "password": "password123",
        "vin": "car-001"
    },
    {
        "client_id": "car-002",
        "username": "car-002",
        "password": "password123",
        "vin": "car-002"
    },
    {
        "client_id": "car-003",
        "username": "car-003",
        "password": "password123",
        "vin": "car-003"
    }
]

def create_client(car):
    client = mqtt.Client(client_id=car["client_id"])
    client.username_pw_set(car["username"], car["password"])
    client.connect(broker, port)
    client.loop_start()
    return client

def generate_telemetry_data(vin):
    return {
        "vin": vin,
        "timestamp": int(time.time() * 1000),
        "lat": round(random.uniform(40.0, 41.0), 6),
        "lng": round(random.uniform(-75.0, -73.0), 6),
        "speed": round(random.uniform(20, 100), 2)
    }

def publish_telemetry(client, topic, telemetry_data):
    payload = json.dumps(telemetry_data)
    result = client.publish(topic, payload)
    status = result[0]
    if status == 0:
        print(f"Sent to `{topic}`: {payload}")
    else:
        print(f"Failed to send message to {topic}")

def main():
    try:
        while True:
            for car in cars:
                client = create_client(car)
                topic = f"vehicles/{car['client_id']}/telemetry"
                print(f"\n Now simulating: {car['client_id']} for 60 seconds")

                start_time = time.time()
                while time.time() - start_time < 60:
                    telemetry = generate_telemetry_data(car["vin"])
                    publish_telemetry(client, topic, telemetry)
                    time.sleep(5)

                client.loop_stop()
                client.disconnect()

    except KeyboardInterrupt:
        print("\nSimulation stopped.")

if __name__ == "__main__":
    main()