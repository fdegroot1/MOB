#include <PubSubClient.h>
#include <WiFi.h>
#include <LiquidCrystal_I2C.h>

// Zelf instellen voor je eigen WLAN
const char *WLAN_SSID = "HP laptop";
const char *WLAN_ACCESS_KEY = "qwerty123";

// CLIENT_ID moet uniek zijn, dus zelf aanpassen (willekeurige letters en cijfers)
const char *MQTT_CLIENT_ID = "MQTTExampleTryout_redouan";

// Gegevens van de MQTT broker die we in TI-1.4 kunnen gebruiken
const char *MQTT_BROKER_URL = "maxwell.bps-software.nl";
const int MQTT_PORT = 1883;
const char *MQTT_USERNAME = "androidTI";
const char *MQTT_PASSWORD = "&FN+g$$Qhm7j";

// Definieer de MQTT topics
const char *MQTT_TOPIC_LCD = "groep/a3/device/2C-6F-C9-54-B0-0C";
const char *MQTT_TOPIC_BUTTON1 = "Demo/Hans/Btn1";
const char *MQTT_TOPIC_BUTTON2 = "Demo/Hans/Btn2";

// Definieer de te gebruiken Quality of Service (QoS)
const int MQTT_QOS = 0;
const int LINE_LENGTH = 16;

//RGB led variabelen
const int redLED = 2; //red LED connects to digital pin 2
const int greenLED = 4; //green LED connects to digital pin 4
const int blueLED = 5; //blue LED connects to digital pin 5

const int buttonPin1 = 23;
const int buttonPin2 = 15;
const int buttonPin3 = 18;
const int buttonPin4 = 19;

class Button {
private:
    int pin;
    int value = 0;
    int previousValue = 0;
public:
    Button(int pin);

    int getValue();

    void reset();

    bool hasChanged();

    void read();
};

Button::Button(int pin) {
    this->pin = pin;
}

int Button::getValue() {
    return this->value;
}

void Button::reset() {
    this->value = 0;
    this->previousValue = 0;
}

bool Button::hasChanged() {
    return this->value != this->previousValue;
}

void Button::read() {
    this->value = digitalRead(this->pin);
}

WiFiClient wifiClient;
PubSubClient mqttClient(wifiClient);
LiquidCrystal_I2C lcd(0x27, 20, 4);  // set the LCD address to 0x27 for a 16 chars and 2 line display

const int MAX_ROUND = 5;

String gameStateText[] = {"Idle", "Choosing", "Result", "Finish"};
enum GameState {
    IDLE, CHOOSING, RESULT, FINISH
};

enum CardType {
    SWORD, BOW, SHIELD
};

CardType cards[] = {SWORD, BOW, SHIELD};
char *cardStrings[] = {"Sword", "Bow", "Shield"};

GameState gameState = IDLE;

int redChoice = 0;
int blueChoice = 0;

int redWins = 0;
int blueWins = 0;

int currentRound = 0;

Button redChoiceButton = Button(buttonPin1);
Button redConfirmButton = Button(buttonPin2);
Button blueChoiceButton = Button(buttonPin3);
Button blueConfirmButton = Button(buttonPin4);

void setState(GameState state) {
    Serial.println("Setting game state to: " + gameStateText[state]);
    gameState = state;
    switch (state) {
        case IDLE:
            lcd.clear();
            lcd.setCursor(0, 0);
            lcd.print("Beschikbaar");
            break;
    }
}

void mqttCallback(char *topic, byte *payload, unsigned int length) {
    // Logging
    Serial.print("MQTT callback called for topic ");
    Serial.println(topic);
    Serial.print("Payload length ");
    Serial.println(length);

    String myString = String(((char *) payload)).substring(0, length) + ":";

    String arguments[4];
    int r = 0, t = 0;

    for (int i = 0; i < myString.length(); i++) {
        if (myString.charAt(i) == ':') {
            arguments[t] = myString.substring(r, i);
            r = (i + 1);
            t++;
        }
    }

    Serial.println(arguments[0]);

    if (arguments[0].equals("ready")) {
        redWins = 0;
        redChoice = 0;
        redChoiceButton.reset();
        redConfirmButton.reset();

        blueWins = 0;
        blueChoice = 0;
        blueChoiceButton.reset();
        blueConfirmButton.reset();

        setState(CHOOSING);
    }
}

int compareCards(CardType card1, CardType card2) {
    if (card1 == SWORD && card2 == BOW) {
        return 1;
    } else if (card1 == SHIELD && card2 == SWORD) {
        return 1;
    } else if (card1 == BOW && card2 == SHIELD) {
        return 1;
    } else if (card2 == SWORD && card1 == BOW) {
        return 1;
    } else if (card2 == SHIELD && card1 == SWORD) {
        return 1;
    } else if (card2 == BOW && card1 == SHIELD) {
        return 1;
    }

    return 0;
}

void handleIdleState() {

}

void handleChoosingState() {
    redChoiceButton.read();
    redConfirmButton.read();
    blueChoiceButton.read();
    blueConfirmButton.read();

    bool valueChanged = false;
    int confirmations = 0;

    if (redChoiceButton.hasChanged() && redChoiceButton.getValue()) {
        Serial.println("Red cycled");
        if (++redChoice == 3) {
            redChoice = 0;
        }
        valueChanged = true;
        // cycle red
    }

    if (redConfirmButton.hasChanged() && redConfirmButton.getValue()) {
        Serial.println("Red confirmed");

        // red confirm
        confirmations++;
    }

    if (blueChoiceButton.hasChanged() && blueChoiceButton.getValue()) {
        Serial.println("Blue cycled");
        if (++blueChoice == 3) {
            blueChoice = 0;
        }
        valueChanged = true;
        // cycle blue
    }

    if (blueConfirmButton.hasChanged() && blueConfirmButton.getValue()) {
        Serial.println("Blue confirmed");
        // blue confirm
        confirmations++;
    }

    if (valueChanged) {
        lcd.clear();
        lcd.setCursor(0, 0);
        lcd.print("Red:" + String(cardStrings[redChoice]));
        lcd.setCursor(0, 1);
        lcd.print("Blue:" + String(cardStrings[redChoice]));
    }

    if (confirmations == 2) {
        setState(RESULT);
    }
}

void handleResultState() {
    int comparison = compareCards(cards[redChoice], cards[blueChoice]);

    lcd.clear();
    lcd.setCursor(0, 0);

    if (comparison != 0) {
        if (comparison == 1) {
            redWins++;
            lcd.print("Red has won");
        } else {
            blueWins++;
            lcd.print("Blue has won");
        }
    } else {
        lcd.print("It's a draw!");
    }

    delay(500);

    currentRound++;

    setState(currentRound >= 5 ? FINISH : CHOOSING);
}

void handleFinishState() {
    lcd.clear();
    lcd.setCursor(0, 0);
    lcd.print("Game finish");

    delay(500);

    lcd.clear();
    lcd.setCursor(0, 0);
    lcd.print("Red: " + String(redWins));
    lcd.setCursor(0, 1);
    lcd.print("Blue: " + String(blueWins));

    mqttClient.publish(MQTT_TOPIC_LCD, (String("finish:") + String(redWins) + String(":") + String(blueWins)).c_str());

    delay(500);
    setState(IDLE);
}

void connectToWiFi() {
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
}

void connectToMqtt() {
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
    if (!mqttClient.publish(MQTT_TOPIC_LCD, "connected")) {
        Serial.print("Failed to publish");
        Serial.println(MQTT_TOPIC_LCD);
    } else {
        Serial.print("Published to topic ");
        Serial.println(MQTT_TOPIC_LCD);
    }
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

    connectToWiFi();
    connectToMqtt();

    // initialize the lcd
    lcd.init();
    lcd.backlight();
    lcd.clear();
}

void loop() {
    switch (gameState) {
        case IDLE:
            handleIdleState();
            break;
        case CHOOSING:
            handleChoosingState();
            break;
        case RESULT:
            handleResultState();
            break;
        case FINISH:
            handleFinishState();
            break;
    }

    mqttClient.loop();
    delay(100);
}
