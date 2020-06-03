#include <PubSubClient.h>
#include <WiFi.h>
#include <LiquidCrystal_I2C.h>

// Zelf instellen voor je eigen WLAN
const char* WLAN_SSID = "Eigen";
const char* WLAN_ACCESS_KEY = "Eigen";

// CLIENT_ID moet uniek zijn, dus zelf aanpassen (willekeurige letters en cijfers)
const char* MQTT_CLIENT_ID = "MQTTExampleTryout_dsjhaajksdhfjkhg";
// Gegevens van de MQTT broker die we in TI-1.4 kunnen gebruiken
const char* MQTT_BROKER_URL = "maxwell.bps-software.nl";
const int   MQTT_PORT = 1883;
const char* MQTT_USERNAME = "androidTI";
const char* MQTT_PASSWORD = "&FN+g$$Qhm7j";
const char* MQTT_DEVICE_ID = "card:4C11AECBE754";

// Definieer de MQTT topics
const char* MQTT_TOPIC_SUBSCRIBE = "groep/a3/device/4C11AECBE754";
const char* MQTT_TOPIC_CONNECT = "groep/a3/connect";
// Definieer de te gebruiken Quality of Service (QoS)
const int   MQTT_QOS = 0;
const int LINE_LENGTH = 16;

//Getal op 7-segment
int n = 1191;
int del = 5;
int count = 0;

//Esp32 gebruikte pins
int BUZZER = 5;
// 7 segment pins
int a = 27; //0
int b = 2;
int c = 3; //32
int d = 4;
int e = 15; 
int f = 25; //25
int g = 17;
int c1 = 23;
int c2 = 26;//26
int c3 = 19;
int c4 = 18;

WiFiClient wifiClient;
PubSubClient mqttClient(wifiClient);
LiquidCrystal_I2C lcd(0x27,20 ,4);  // set the LCD address to 0x27 for a 16 chars and 2 line display

void mqttCallback(char* topic, byte* payload, unsigned int length) {
  // Logging
  Serial.print("MQTT callback called for topic ");
  Serial.println(topic);
  Serial.print("Payload length ");
  Serial.println(length);

  String myString = String(((char*)payload)).substring(0,length)+":";
  Serial.println(myString);
  
    String sa[4];
    int r=0, t=0;

    for (int i=0; i < myString.length(); i++){ 
      if(myString.charAt(i) == ':'){  
        sa[t] = myString.substring(r, i); 
        r=(i+1); 
        t++; 
      }
    }

    Serial.println(sa[0]);
    
    if(sa[0].equals("new")){
      lcd.clear();
      lcd.setCursor(0,0);
      lcd.print(sa[1]);
      lcd.setCursor(0,1);
      lcd.print(sa[2]);
      n = sa[3].toInt();
      Serial.println(n);
    }
    else if(sa[0].equals("decreased")){
      n--;
      Serial.println(n);
      
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
  if (!mqttClient.subscribe(MQTT_TOPIC_SUBSCRIBE, MQTT_QOS)) {
    Serial.print("Failed to subscribe to topic ");
    Serial.println(MQTT_TOPIC_SUBSCRIBE);
  } else {
    Serial.print("Subscribed to topic ");
    Serial.println(MQTT_TOPIC_SUBSCRIBE);
  }
  // Publish to topic
  if(!mqttClient.publish(MQTT_TOPIC_CONNECT, MQTT_DEVICE_ID)){
    Serial.print("Failed to publish");
    Serial.println(MQTT_TOPIC_CONNECT);
  }
  else{
    Serial.print("Published to topic ");
    Serial.println(MQTT_TOPIC_CONNECT);
  }
  
  lcd.init();                      // initialize the lcd 
  lcd.backlight();

  pinMode(BUZZER, OUTPUT);
  pinMode(a,OUTPUT);
  pinMode(b,OUTPUT);
  pinMode(c,OUTPUT);
  pinMode(d,OUTPUT);
  pinMode(e,OUTPUT);
  pinMode(f,OUTPUT);
  pinMode(g,OUTPUT);
  pinMode(c1,OUTPUT);
  pinMode(c2,OUTPUT);
  pinMode(c3,OUTPUT);
  pinMode(c4,OUTPUT);
}

void loop() {
  // put your main code here, to run repeatedly:
  mqttClient.loop();
 
  clearLEDs();//clear the 7-segment display screen
  pickDigit(0);//Light up 7-segment display d1
  pickNumber((n/1000));// get the value of thousand
  delay(del);//delay 5ms

  //clearLED();//clear the 7-segment display screen
  pickDigit(1);//Light up 7-segment display d2
  pickNumber((n%1000)/100);// get the value of hundred
  delay(del);//delay 5ms

  //clearLED();//clear the 7-segment display screen
  pickDigit(2);//Light up 7-segment display d3
  pickNumber(n%100/10);//get the value of ten
  delay(del);//delay 5ms

  //clearLED();//clear the 7-segment display screen
  pickDigit(3);//Light up 7-segment display d4
  pickNumber(n%10);//Get the value of single digit

  count++;
  
  delay(del);
}

void clearLEDs(){
  digitalWrite(c1,HIGH);
  digitalWrite(a, HIGH);
  digitalWrite(b, HIGH);
  digitalWrite(c, HIGH);
  digitalWrite(d, HIGH);
  digitalWrite(e, HIGH);
  digitalWrite(f, HIGH);
  digitalWrite(g, HIGH);

  digitalWrite(c2,HIGH);
  digitalWrite(a, HIGH);
  digitalWrite(b, HIGH);
  digitalWrite(c, HIGH);
  digitalWrite(d, HIGH);
  digitalWrite(e, HIGH);
  digitalWrite(f, HIGH);
  digitalWrite(g, HIGH);

  digitalWrite(c3,HIGH);
  digitalWrite(a, HIGH);
  digitalWrite(b, HIGH);
  digitalWrite(c, HIGH);
  digitalWrite(d, HIGH);
  digitalWrite(e, HIGH);
  digitalWrite(f, HIGH);
  digitalWrite(g, HIGH);

  digitalWrite(c4,HIGH);
  digitalWrite(a, HIGH);
  digitalWrite(b, HIGH);
  digitalWrite(c, HIGH);
  digitalWrite(d, HIGH);
  digitalWrite(e, HIGH);
  digitalWrite(f, HIGH);
  digitalWrite(g, HIGH);
}

void clearLED(){
  digitalWrite(a, HIGH);
  digitalWrite(b, HIGH);
  digitalWrite(c, HIGH);
  digitalWrite(d, HIGH);
  digitalWrite(e, HIGH);
  digitalWrite(f, HIGH);
  digitalWrite(g, HIGH);
  //delay(1000);
}

void pickDigit(int digit){
  digitalWrite(c1, LOW);
  digitalWrite(c2, LOW);
  digitalWrite(c3, LOW);
  digitalWrite(c4, LOW);

  switch(digit){
    case 0:
    digitalWrite(c1, HIGH);
    break;
    case 1:
    digitalWrite(c2, HIGH);
    break;
    case 2:
    digitalWrite(c3, HIGH);
    break;
    case 3:
    digitalWrite(c4, HIGH);
    break;
  }
}

void pickNumber(int number){
  switch(number){
    default:
    zero();
    break;
    case 0:
    zero();
    break;
    case 1:
    one();
    break;
    case 2:
    two();
    break;
    case 3:
    three();
    break;
    case 4:
    four();
    break;
    case 5:
    five();
    break;
    case 6:
    six();
    break;
    case 7:
    seven();
    break;
    case 8:
    eight();
    break;
    case 9:
    nine();
    break;
  }
}

void zero(){
  digitalWrite(a, LOW);
  digitalWrite(b, LOW);
  digitalWrite(c, LOW);
  digitalWrite(d, LOW);
  digitalWrite(e, LOW);
  digitalWrite(f, LOW);
  digitalWrite(g, HIGH);
}
void one(){
  digitalWrite(a, HIGH);
  digitalWrite(b, LOW);
  digitalWrite(c, LOW);
  digitalWrite(d, HIGH);
  digitalWrite(e, HIGH);
  digitalWrite(f, HIGH);
  digitalWrite(g, HIGH);
}

void two(){
  digitalWrite(a, LOW);
  digitalWrite(b, LOW);
  digitalWrite(c, HIGH);
  digitalWrite(d, LOW);
  digitalWrite(e, LOW);
  digitalWrite(f, HIGH);
  digitalWrite(g, LOW);
}

void three(){
  digitalWrite(a, LOW);
  digitalWrite(b, LOW);
  digitalWrite(c, LOW);
  digitalWrite(d, LOW);
  digitalWrite(e, HIGH);
  digitalWrite(f, HIGH);
  digitalWrite(g, LOW);
}

void four(){
  digitalWrite(a, HIGH);
  digitalWrite(b, LOW);
  digitalWrite(c, LOW);
  digitalWrite(d, HIGH);
  digitalWrite(e, HIGH);
  digitalWrite(f, LOW);
  digitalWrite(g, LOW);
}

void five(){
  digitalWrite(a, LOW);
  digitalWrite(b, HIGH);
  digitalWrite(c, LOW);
  digitalWrite(d, LOW);
  digitalWrite(e, HIGH);
  digitalWrite(f, LOW);
  digitalWrite(g, LOW);
}

void six(){
  digitalWrite(a, LOW);
  digitalWrite(b, HIGH);
  digitalWrite(c, LOW);
  digitalWrite(d, LOW);
  digitalWrite(e, LOW);
  digitalWrite(f, LOW);
  digitalWrite(g, LOW);
}

void seven(){
  digitalWrite(a, LOW);
  digitalWrite(b, LOW);
  digitalWrite(c, LOW);
  digitalWrite(d, HIGH);
  digitalWrite(e, HIGH);
  digitalWrite(f, HIGH);
  digitalWrite(g, HIGH);
}

void eight(){
  digitalWrite(a, LOW);
  digitalWrite(b, LOW);
  digitalWrite(c, LOW);
  digitalWrite(d, LOW);
  digitalWrite(e, LOW);
  digitalWrite(f, LOW);
  digitalWrite(g, LOW);
}

void nine(){
  digitalWrite(a, LOW);
  digitalWrite(b, LOW);
  digitalWrite(c, LOW);
  digitalWrite(d, LOW);
  digitalWrite(e, HIGH);
  digitalWrite(f, LOW);
  digitalWrite(g, LOW);
}
