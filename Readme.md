
# Backend Task

## Project Structure

The project structure is as follows:

Here’s the tech stack I’m using:

- Java 21 with Spring Boot + Eclipse Paho MQTT client — Main backend logic
- PostgreSQL + TimescaleDB — For persistent and time-series data storage
- EMQX MQTT Broker — For all communication between vehicles and backend
- Python clients — Simulated vehicles used for testing
- Docker — Runs the broker and database


## Some design choices

All MQTT messages are expected to be in JSON format. Although no strict schema validation is enforced, Spring Boot with Jackson deserializes these into structured POJOs, allowing for clean separation of data handling logic.



Telemetry entries are stored in TimescaleDB using a vin + timestamp composite primary key. This ensures each vehicle has only one reading per timestamp and enables efficient querying by vehicle and time range.


Topic structure was always decided by either trying to isolate the VIN or the mission (Or both), and taking advantage of the wildcards.

---

## Initial Setup
### Docker setup

Make sure Docker is installed before starting.
To run the infrastructure:
- Open a terminal in the project root folder
- Type: docker-compose up

  - This starts 2 containers:

      -emqx: The MQTT Broker

    -timescaledb: PostgreSQL with TimescaleDB extension


The backend app runs from the IDE (IntelliJ) for easier debugging.

### Configurations EMQX

Access the dashboard here:

    EMQX Dashboard: http://localhost:18083
    User: admin
    Password: public
This is the dashboard where we will do our configurations

#### 1 - In the sidebar, go to the Authentication section (second option).
#### 2 - There are two mechanisms:
##### -The HTTP Server authentication is used to validate clients via the backend (MqttAuthController handles this).          
##### -The Built-in Database allows us to define fixed users. Set up two:          
#####  Used by the backend to connect and subscribe to topics.

      user: backendapp
      password: password123

#####  Used by all vehicles to register themselves.

      user: reg_user
      password: reg_password
##### This user can only publish to vehicle/registration/request and is blocked from all other topics via ACL.


### Access to Database

#### You can connect to the TimescaleDB instance with a tool like pgAdmin using:    
    user: postgres
    password: 123QWEasd
    host: localhost
    port: 5433


---

## Test Procedures

## Relevant info
This MQTT broker support dynamic authentication, so upon any client connection it will automatically request the backend to perform the authentication.
This is done in, **MqttAuthController.class** 
If the Authentication is accepted, then the MQTT Broker creates the session and no more authentication from that session is needed.
If for some reason the client disconnects, authentication is of course required again.


### 1. Register a Vehicle

In **pythonMqttClients**, use **RegisterTC.py** to simulate a vehicle registration.

The backend is subscribed to the following topic
Vehicles publish registration requests to this topic using the reg_user credentials.

    vehicle/registration/request

If successful, you’ll see a new entry in the database (table: vehicle), otherwise error logs will show up in the backend console.

This is the expected format for a **Register Request**

    registration_data = {
        "vin": "car-001",
        "model": "Model S",
        "manufacturer": "Tesla",
        "firmwareVersion": "v10.2.1",
        "hardwareId": "HW-12345",
        "secretToken": "password123",
        "timestamp": 123975203750
    }



### 2. Telemetry Data test
Run **car002PublishingData.py** to simulate telemetry data from multiple cars.
Each car publishes data like:

    {
        "vin": "car-002",
        "lat": 45.98,
        "lng": -28.3,
        "speed": 120,
        "timestamp": 12312452354
    }

#### Note 
Only authenticated vehicles (validated via the backend) can publish. Unauthorized clients are rejected before sending anything.


#### Topic used by vehicles:

    vehicles/+/telemetry, where the + is the VIN of the car

The Backend subscribes using a wildcard to all telemetry topics.
Stores data into a TimescaleDB hypertable.
The primary key is a combination of vin and timestamp.


### 3. Mission

To assign a mission to vehicles, use Postman to make a POST request to:

    http://localhost:8080/api/missions/start

The JSON Body should have the following format
        
    {
        "missionName": "Put out fire in the Hospital",
        "missionDescription": "Urgent dispatcher to hospital",
        "goal": "Estinguish Flames",
        "assignedVehicles": ["car-004", "car-003"],
        "waypoints": [
        {"lat": 50.712776, "lng": -60.005974},
        {"lat": 51.713776, "lng": -61.002974}
        ]
    }
assignedVehicles are the VIN of the vehicles that should perform the mission.
waypoints are the points at which the cars must go.

This Request is handled by **MissionController**
It is possible to have multiple mission in parallel, each with multiple cars.
If a car is already in one mission, i assume it cannot go on another.

The backend then publishes this to the corresponding vehicle topic:

    vehicles/car-002/mission/start

The vehicles will respond through MQTT with the following data format

    {
        "vin": car-002,
        "missionId": 3,
        "timestamp": 123123551,
        "status": IN_PROGRESS,
        "location": {
            "lat": 40.7128,
            "lng": -74.006
        },
        "speed": 76
    }
This is what i call a **VehicleMissionData** and it is saved in the database.
It is an entity inside the Mission entity, and it's solely used to represent each vehicle mission status.

The vehicles response is handled in the backend by **MissionHandler**, there is one instance for each Mission


#### Mission Topic Information
This is the topic the backend uses to publish the mission request for the cars.

    api/requests/mission/+/vehicles/+
                        /missionId   /VIN

I chose this format because it show that it is a request, separates by mission and by vin.
This way the car clients can be subscribed to the following topic:
    
    Clients are subscribed to: api/requests/mission/+/vehicles/+
    This way the cars are listening to ANY mission that may start for them (They know their own VIN)
    After receiving a message in this topic, they will know the missionId aswell and can then use it for the next topic.

There is a topic specifically for the Car to publish and where the Backend is subscribed

    api/mission/+/vehicles/+
    Cars publish their updates to this topic.
    Using these wildcards, the MissionManager in the backend is listening to updates from all cars in all missions.
    Using the values in the wildcard, it can extract the missionId and VIN to choose which MissionHandler instance to take action for a specific message.


#### Python script

There is a script called missionSim.py, where 4 clients are connected waiting for missions.
This will depend on the REST Request, but you could start 1-4 missions and see that each car is updating itself,
You can see logs in the backend console log and you can see all the values being updated in the database


