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
        "vehicleId": "car-001"
    },
    {
        "client_id": "car-002",
        "username": "car-002",
        "password": "password123",
        "vehicleId": "car-002"
    },
    {
        "client_id": "car-003",
        "username": "car-003",
        "password": "password123",
        "vehicleId": "car-003"
    }
]

HEALTH_STATUSES = ["OK", "WARNING", "ERROR"]

def create_client(car):
    client = mqtt.Client(client_id=car["client_id"])
    client.username_pw_set(car["username"], car["password"])
    client.connect(broker, port)
    client.loop_start()
    return client

def generate_health_telemetry(vehicleId):
    return {
        "vehicleId": vehicleId,
        "timestamp": int(time.time() * 1000),
        "engineStatus": random.choice(HEALTH_STATUSES),
        "engineOilLevelPercent": round(random.uniform(0, 100), 2),
        "engineCheckEngineLight": random.choice([True, False]),
        "batteryStatus": random.choice(HEALTH_STATUSES),
        "batteryVoltage": round(random.uniform(0, 15), 2),
        "tireFrontLeftPsi": round(random.uniform(25, 40), 2),
        "tireFrontRightPsi": round(random.uniform(25, 40), 2),
        "tireRearLeftPsi": round(random.uniform(25, 40), 2),
        "tireRearRightPsi": round(random.uniform(25, 40), 2),
        "brakeStatus": random.choice(HEALTH_STATUSES)
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
                topic = f"vehicles/{car['client_id']}/status"
                print(f"\nNow simulating: {car['client_id']} for 60 seconds")

                start_time = time.time()
                while time.time() - start_time < 60:
                    telemetry = generate_health_telemetry(car["vehicleId"])
                    publish_telemetry(client, topic, telemetry)
                    time.sleep(5)

                client.loop_stop()
                client.disconnect()

    except KeyboardInterrupt:
        print("\nSimulation stopped.")

if __name__ == "__main__":
    main()