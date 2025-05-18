import json
import time
import threading
import paho.mqtt.client as mqtt

BROKER = "localhost"
PORT = 1883

cars = [
    {
        "client_id": "car-001",
        "username": "car-001",
        "password": "123TestPwd",
        "vin": "car-001"
    },
    {
        "client_id": "car-002",
        "username": "car-002",
        "password": "123TestPwd",
        "vin": "car-002"
    },
]

class VehicleSimulator:
    def __init__(self, car_config):
        self.client_id = car_config["client_id"]
        self.username = car_config["username"]
        self.password = car_config["password"]
        self.vin = car_config["vin"]

        self.client = mqtt.Client(client_id=self.client_id, protocol=mqtt.MQTTv311)
        self.client.username_pw_set(self.username, self.password)

        self.mission_id = None
        self.running = False

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
        # Optional: reconnect logic
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
                threading.Thread(target=self.send_status_updates, daemon=True).start()

    def send_status_updates(self):
        try:
            while self.running:
                status_topic = f"api/mission/{self.mission_id}/vehicles/{self.vin}"
                status_payload = json.dumps({
                    "vin": self.vin,
                    "missionId": self.mission_id,
                    "timestamp": time.strftime("%Y-%m-%dT%H:%M:%SZ", time.gmtime()),
                    "status": "en route",
                    "location": {
                        "latitude": 40.7128,
                        "longitude": -74.0060
                    },
                    "speed": 50
                })
                result = self.client.publish(status_topic, status_payload)
                if result.rc == mqtt.MQTT_ERR_SUCCESS:
                    print(f"[{self.vin}] Sent status update to {status_topic}", flush=True)
                else:
                    print(f"[{self.vin}] Failed to publish status: {result.rc}", flush=True)
                time.sleep(60)
        except Exception as e:
            print(f"[{self.vin}] Error in send_status_updates: {e}", flush=True)

if __name__ == "__main__":
    simulators = [VehicleSimulator(car) for car in cars]

    for sim in simulators:
        sim.start()

    try:
        while True:
            time.sleep(1)
    except KeyboardInterrupt:
        print("Stopping simulators...", flush=True)
        for sim in simulators:
            sim.stop()
