// 7 segment pins
int a = 1; //0
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

// Variables
int n = 100;
int count = 0;

// Delay
int del = 5;

void setup() {
  // put your setup code here, to run once:
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

  clearLEDs();//clear the 7-segment display screen
  pickDigit(0);//Light up 7-segment display d1
  pickNumber((n/1000));// get the value of thousand
  delay(del);//delay 5ms

  //clearLEDs();//clear the 7-segment display screen
  pickDigit(1);//Light up 7-segment display d2
  pickNumber((n%1000)/100);// get the value of hundred
  delay(del);//delay 5ms

  //clearLEDs();//clear the 7-segment display screen
  pickDigit(2);//Light up 7-segment display d3
  pickNumber(n%100/10);//get the value of ten
  delay(del);//delay 5ms

  //clearLEDs();//clear the 7-segment display screen
  pickDigit(3);//Light up 7-segment display d4
  pickNumber(n%10);//Get the value of single digit
  delay(del);//delay 5ms
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
    case 2:
    digitalWrite(c3, HIGH);
    break;
    default:
    digitalWrite(c4, HIGH);
    break;
  }
}

void pickNumber(int number){
  switch(number){
    default:
    clearLED();
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

void add(){
  count++;
  if(count == 10){
    count = 0;
    n++;
    if(n == 10000){
      n = 0;
    }
  }
}
