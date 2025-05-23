import paho.mqtt.client as mqtt

vin = "car-002"
password = "password123"


broker_host = "localhost"
broker_port = 1883

def on_connect(client, userdata, flags, rc):
    if rc == 0:
        print("Connected successfully!")
    elif rc == 4:
        print("Bad username or password")
    else:
        print(f"Connection failed with code {rc}")


client = mqtt.Client(client_id=vin)
client.username_pw_set(username=vin, password=password)
client.on_connect = on_connect

print("Connecting to broker...")
client.connect(broker_host, broker_port)
client.loop_forever()
