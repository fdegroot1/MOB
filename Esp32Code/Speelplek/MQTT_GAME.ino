#include <PubSubClient.h>
#include <WiFi.h>
#include <LiquidCrystal_I2C.h>

// Zelf instellen voor je eigen WLAN
const char* WLAN_SSID = "HP laptop";
const char* WLAN_ACCESS_KEY = "qwerty123";

// CLIENT_ID moet uniek zijn, dus zelf aanpassen (willekeurige letters en cijfers)
const char* MQTT_CLIENT_ID = "MQTTExampleTryout_redouan";

// Gegevens van de MQTT broker die we in TI-1.4 kunnen gebruiken
const char* MQTT_BROKER_URL = "maxwell.bps-software.nl";
const int   MQTT_PORT = 1883;
const char* MQTT_USERNAME = "androidTI";
const char* MQTT_PASSWORD = "&FN+g$$Qhm7j";

// Definieer de MQTT topics
const char* MQTT_TOPIC_LCD = "groep/a3/device/2C-6F-C9-54-B0-0C";
const char* MQTT_TOPIC_BUTTON1 = "Demo/Hans/Btn1";
const char* MQTT_TOPIC_BUTTON2 = "Demo/Hans/Btn2";

// Definieer de te gebruiken Quality of Service (QoS)
const int   MQTT_QOS = 0;
const int LINE_LENGTH = 16;

//RGB led variabelen
const int redLED = 2; //red LED connects to digital pin 2
const int greenLED = 4; //green LED connects to digital pin 4
const int blueLED = 5; //blue LED connects to digital pin 5

//knoppen voor de spelers
int redKeuzeState = 0;
int redConformState = 0;
int blueKeuzeState = 0;
int blueConformState = 0;

const int buttonPin1 = 23;
const int buttonPin2 = 15;
const int buttonPin3 = 18;
const int buttonPin4 = 19;

//spel variabelen
enum kaartSoorten{ZWAARD, SCHILD, BOOG};

boolean redConform = false;
boolean blueConform = false;

kaartSoorten keuzes[3] = {ZWAARD, SCHILD, BOOG};
kaartSoorten redKeuze = keuzes[1];
kaartSoorten blueKeuze = keuzes[1];
int redCounter = 0;
int blueCounter = 0;

//scores
int redScore = 0;
int blueScore = 0;
int rondes = 0;

enum spelStates{kiezen, uitslag};
spelStates state = kiezen;

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
    // Laat de tekst zien in zowel log als op het LCD
    
    char txt[LINE_LENGTH + 1];
    for (int i = 0; i < LINE_LENGTH + 1; i++) { txt[i] = '\0'; }
    strncpy(txt, (const char*) payload, length > 16 ? 16 : length);
  }

}

void spel(kaartSoorten keuze1, kaartSoorten keuze2) {
  if(rondes < 3){
    if(keuze1 == ZWAARD && keuze2 == BOOG){
      vergelijkWinnaar(1, 0);
    }
    if(keuze2 == ZWAARD && keuze1 == BOOG){
      vergelijkWinnaar(0, -1);
    }
    if(keuze1 == SCHILD && keuze2 == ZWAARD){
      vergelijkWinnaar(1, 0);
    }
    if(keuze2 == SCHILD && keuze1 == ZWAARD){
      vergelijkWinnaar(0, -1);
    }
    if(keuze1 == BOOG && keuze2 == SCHILD){
      vergelijkWinnaar(1, 0);
    }
    if(keuze2 == BOOG && keuze1 == SCHILD){
      vergelijkWinnaar(0, -1);
    }
  }else{
    mqttClient.publish(MQTT_TOPIC_LCD, "finish:{" + String(redScore) + "}:{" + String(blueScore) + "}");
    
    rondes = 0;
    state = kiezen;
    redKeuze = zwaard;
    blueKeuze = zwaard;
    redConform = false;
    blueConform = false;
  }
}

void vergelijkWinnaar(int redKeuze, int blueKeuze){
  int vergelijking = redKeuze - blueKeuze;

  if(vergelijking == -1){
    blueScore++;
  }

  if(vergelijking == 1){
    redScore++;
  }
  rondes++;
  lcdScoreTekst();
}

void lcdKeuzeTekst(){
  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print("RED:" + String(redKeuze));
  lcd.setCursor(0, 1);
  lcd.print("BLUE:" + String(blueKeuze));
}

void lcdScoreTekst(){
  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print("RED:" + String(redScore));
  lcd.setCursor(0, 1);
  lcd.print("BLUE:" + String(blueScore));
}

void setup() {
  // put your setup code here, to run once:
  pinMode(buttonPin1, INPUT);
  pinMode(buttonPin2, INPUT);
  pinMode(buttonPin3, INPUT);
  pinMode(buttonPin4, INPUT);

  pinMode(redLED, OUTPUT);
  pinMode(greenLED, OUTPUT);
  pinMode(blueLED, OUTPUT);
  
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
  if(!mqttClient.publish(MQTT_TOPIC_LCD, "connected")){
    Serial.print("Failed to publish");
    Serial.println(MQTT_TOPIC_LCD);
  }
  else{
    Serial.print("Published to topic ");
    Serial.println(MQTT_TOPIC_LCD);
  }
  
  // initialize the lcd 
  lcd.init();                     
  lcd.backlight();
  lcd.clear();
  lcd.setCursor(0,0);
  lcd.print("WELKOM SPELERS!");
}

void loop() {
  // put your main code here, to run repeatedly:
  redKeuzeState = digitalRead(buttonPin1);
  redConformState = digitalRead(buttonPin2);
  blueKeuzeState = digitalRead(buttonPin3);
  blueConformState = digitalRead(buttonPin4);
   
  switch(state){
    case kiezen: 
          if(redKeuzeState == HIGH){
            Serial.print("button1 pressed");
             if(redCounter < 3){
             redKeuze = keuzes[redCounter];
             redCounter++;
             Serial.print(redCounter);
             lcdKeuzeTekst();
          }else{
            redCounter = 0;
            lcdKeuzeTekst();
          }
         }

         if(blueKeuzeState == HIGH){
          Serial.print("button2 pressed");
           if(blueCounter < 3){
            blueKeuze = keuzes[blueCounter];
            blueCounter++;
            Serial.print(blueCounter);
            lcdKeuzeTekst();
           }else{
             blueCounter = 0;
             lcdKeuzeTekst();
           }
          }

          if(redConformState == HIGH){
            Serial.print("button3 pressed");
            redConform = !redConform;
          }

          if(blueConformState == HIGH){
            Serial.print("button4 pressed");
            blueConform = !blueConform;
          }

          if(redConform == true && blueConform == true){
            state = uitslag;
          }

          break;
     case uitslag:
          spel(redKeuze, blueKeuze);
          break;
   }
   mqttClient.loop();
   delay(100);
}
