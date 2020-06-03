/*
 * FABIAN
 * Displays text sent over the serial port (e.g. from the Serial Monitor) on
 * an attached LCD.
 * YWROBOT
 * Compatible with the Arduino IDE 1.0
 * Library version:1.1
 */
#include <Wire.h> 
#include <LiquidCrystal_I2C.h>

LiquidCrystal_I2C lcd(0x27,20 ,4);  // set the LCD address to 0x27 for a 16 chars and 2 line display

void setup()
{ 
  Serial.begin(9600);
  lcd.init();                      // initialize the lcd 
  lcd.backlight();
 
}

void loop()
{
  // when characters arrive over the serial port...
  if (Serial.available()) {
    Serial.println(Serial.read());
    // wait a bit for the entire message to arrive
    delay(100);
    // clear the screen
    lcd.clear();
    // read all the available characters
    while (Serial.available() > 0) {
      // display each character to the LCD
       Serial.println(Serial.read());
      lcd.print("yeet");
    }
  }
}

/*
 * REDOUAN
 */
#include <Wire.h>
#include <LiquidCrystal_I2C.h>

// Set the LCD address to 0x27 for a 16 chars and 2 line display
LiquidCrystal_I2C lcd(0x27, 16, 2);

void setup()
{
	// initialize the LCD
	lcd.begin();
  lcd.backlight();
}

void loop()
{
	bool blinking = true;
	lcd.cursor();

	while (1) {
		if (blinking) {
			lcd.clear();
			lcd.print("No cursor blink");
			lcd.noBlink();
			blinking = false;
		} else {
			lcd.clear();
			lcd.print("Cursor blink");
			lcd.blink();
			blinking = true;
		}
		delay(4000);
	}
}
