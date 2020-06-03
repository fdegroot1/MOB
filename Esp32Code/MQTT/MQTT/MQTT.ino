#include <PubSubClient.h>
#include <WiFi.h>
#include <LiquidCrystal_I2C.h>

// Zelf instellen voor je eigen WLAN
const char* WLAN_SSID = "";
const char* WLAN_ACCESS_KEY = "";

// CLIENT_ID moet uniek zijn, dus zelf aanpassen (willekeurige letters en cijfers)
const char* MQTT_CLIENT_ID = "MQTTExampleTryout_dsjhaajksdhfjkhg";
// Gegevens van de MQTT broker die we in TI-1.4 kunnen gebruiken
const char* MQTT_BROKER_URL = "maxwell.bps-software.nl";
const int   MQTT_PORT = 1883;
const char* MQTT_USERNAME = "androidTI";
const char* MQTT_PASSWORD = "&FN+g$$Qhm7j";

// Definieer de MQTT topics
const char* MQTT_TOPIC_LCD = "Student/Fabian/Test";
const char* MQTT_TOPIC_BUTTON1 = "Demo/Hans/Btn1";
const char* MQTT_TOPIC_BUTTON2 = "Demo/Hans/Btn2";
// Definieer de te gebruiken Quality of Service (QoS)
const int   MQTT_QOS = 0;
const int LINE_LENGTH = 16;

WiFiClient wifiClient;
PubSubClient mqttClient(wifiClient);
LiquidCrystal_I2C lcd(0x27,20 ,4);  // set the LCD address to 0x27 for a 16 chars and 2 line display

void mqttCallback(char* topic, byte* payload, unsigned int length) {
  // Logging
  Serial.print("MQTT callback called for topic ");
  Serial.println(topic);
  Serial.print("Payload length ");
  Serial.println(length);
  // Genereer een korte piep
  // Kijk welk topic is ontvangen en handel daarnaar
  if (strcmp(topic, MQTT_TOPIC_LCD) == 0) {
    // De payload is een tekst voor op het LCD
    // Let op, geen null-terminated string, dus voeg zelf de \0 toe
    char txt[LINE_LENGTH + 1];
    for (int i = 0; i < LINE_LENGTH + 1; i++) { txt[i] = '\0'; }
    strncpy(txt, (const char*) payload, length > 16 ? 16 : length);
    // Laat de tekst zien in zowel log als op het LCD
    Serial.print("Text: ");
    Serial.println(txt); 
    lcd.clear();
    lcd.setCursor(0,0);
    lcd.print(txt);
  }

}
void setup() {
  // put your setup code here, to run once:
  Serial.begin(115200);
  Serial.println("ESP32 MQTT example");
  WiFi.mode(WIFI_STA);
  WiFi.disconnect();
  Serial.println("Connecting to ");
  Serial.println(WLAN_SSID);
  WiFi.begin(WLAN_SSID, WLAN_ACCESS_KEY);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("WiFi connected");
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());
  mqttClient.setServer(MQTT_BROKER_URL, MQTT_PORT);
  mqttClient.setCallback(mqttCallback);
  // Maak verbinding met de MQTT broker
  if (!mqttClient.connect(MQTT_CLIENT_ID, MQTT_USERNAME, MQTT_PASSWORD)) {
    Serial.println("Failed to connect to MQTT broker");
  } else {
    Serial.println("Connected to MQTT broker");
  }
  // Subscribe op de LCD topic
  if (!mqttClient.subscribe(MQTT_TOPIC_LCD, MQTT_QOS)) {
    Serial.print("Failed to subscribe to topic ");
    Serial.println(MQTT_TOPIC_LCD);
  } else {
    Serial.print("Subscribed to topic ");
    Serial.println(MQTT_TOPIC_LCD);
  }
  // Publish to topic
  if(!mqttClient.publish(MQTT_TOPIC_LCD, "yeet")){
    Serial.print("Failed to publish");
    Serial.println(MQTT_TOPIC_LCD);
  }
  else{
    Serial.print("Published to topic ");
    Serial.println(MQTT_TOPIC_LCD);
  }
  
  lcd.init();                      // initialize the lcd 
  lcd.backlight();
}

void loop() {
  // put your main code here, to run repeatedly:
   mqttClient.loop();
   mqttClient.publish(MQTT_TOPIC_LCD, "hoi");
   delay(100);
}
