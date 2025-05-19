import json
import time
import threading
import random
import paho.mqtt.client as mqtt

BROKER = "localhost"
PORT = 1883

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
    },
    {
        "client_id": "car-004",
        "username": "car-004",
        "password": "password123",
        "vin": "car-004"
    },
]

STATUS_FLOW = ["PENDING", "IN_PROGRESS", "IN_PROGRESS", "COMPLETED"]

class VehicleSimulator:
    def __init__(self, car_config, start_delay_minutes=0):
        self.client_id = car_config["client_id"]
        self.username = car_config["username"]
        self.password = car_config["password"]
        self.vin = car_config["vin"]

        self.start_delay_seconds = start_delay_minutes * 60

        self.client = mqtt.Client(client_id=self.client_id, protocol=mqtt.MQTTv311)
        self.client.username_pw_set(self.username, self.password)

        self.mission_id = None
        self.running = False
        self.status_index = 0

        self.client.on_connect = self.on_connect
        self.client.on_message = self.on_message
        self.client.on_disconnect = self.on_disconnect

    def start(self):
        try:
            self.client.connect(BROKER, PORT, 60)
            self.client.loop_start()
        except Exception as e:
            print(f"[{self.vin}] Failed to connect: {e}", flush=True)

    def stop(self):
        self.running = False
        self.client.loop_stop()
        self.client.disconnect()

    def on_connect(self, client, userdata, flags, rc):
        print(f"[{self.vin}] Connected with result code {rc}", flush=True)
        topic = f"api/requests/mission/+/vehicles/{self.vin}"
        client.subscribe(topic)
        print(f"[{self.vin}] Subscribed to {topic}", flush=True)

    def on_disconnect(self, client, userdata, rc):
        print(f"[{self.vin}] Disconnected with result code {rc}", flush=True)
        if rc != 0:
            print(f"[{self.vin}] Unexpected disconnect. Reconnecting...", flush=True)
            try:
                client.reconnect()
            except Exception as e:
                print(f"[{self.vin}] Reconnection failed: {e}", flush=True)

    def on_message(self, client, userdata, msg):
        print(f"[{self.vin}] Received message on {msg.topic}: {msg.payload.decode()}", flush=True)
        try:
            payload = json.loads(msg.payload.decode())
        except json.JSONDecodeError:
            print(f"[{self.vin}] Invalid JSON received.", flush=True)
            return

        if payload.get("command") == "start" and payload.get("vin") == self.vin:
            self.mission_id = payload.get("missionId")
            if not self.running:
                print(f"[{self.vin}] Starting mission {self.mission_id}", flush=True)
                self.running = True
                threading.Thread(target=self.delayed_status_start, daemon=True).start()

    def delayed_status_start(self):
        if self.start_delay_seconds > 0:
            print(f"[{self.vin}] Waiting {self.start_delay_seconds} seconds before sending updates.", flush=True)
            time.sleep(self.start_delay_seconds)
        self.send_status_updates()

    def send_status_updates(self):
        try:
            delay = random.uniform(1, 5)  # Optional small delay before first status
            time.sleep(delay)

            while self.running and self.status_index < len(STATUS_FLOW):
                current_status = STATUS_FLOW[self.status_index]
                status_topic = f"api/mission/{self.mission_id}/vehicles/{self.vin}"
                status_payload = json.dumps({
                    "vin": self.vin,
                    "missionId": self.mission_id,
                    "timestamp": int(time.time() * 1000),
                    "status": current_status,
                    "location": {
                        "lat": round(40.7128 + random.uniform(-0.01, 0.01), 6),
                        "lng": round(-74.0060 + random.uniform(-0.01, 0.01), 6)
                    },
                    "speed": random.randint(30, 80)
                })
                result = self.client.publish(status_topic, status_payload)
                if result.rc == mqtt.MQTT_ERR_SUCCESS:
                    print(f"[{self.vin}] Sent status '{current_status}' to {status_topic}", flush=True)
                else:
                    print(f"[{self.vin}] Failed to publish status: {result.rc}", flush=True)

                self.status_index += 1
                time.sleep(5)  # Time between status updates

            print(f"[{self.vin}] Mission complete.", flush=True)
            self.running = False

        except Exception as e:
            print(f"[{self.vin}] Error in send_status_updates: {e}", flush=True)

if __name__ == "__main__":
    simulators = []
    for i, car in enumerate(cars):
        sim = VehicleSimulator(car, start_delay_minutes=i)
        simulators.append(sim)

    for sim in simulators:
        sim.start()

    try:
        while True:
            time.sleep(1)
    except KeyboardInterrupt:
        print("Stopping simulators...", flush=True)
        for sim in simulators:
            sim.stop()
