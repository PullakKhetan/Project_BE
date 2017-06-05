#include <SoftwareSerial.h>
#include <TinyGPS.h>
SoftwareSerial blu(2,3);
SoftwareSerial gp(4,5);
TinyGPS gps;
char command;
String st,lat1;
int s;
boolean led=false;
int lpin=12;
//int ledPin = 13; // choose the pin for the LED
int inPin = 7;   // choose the input pin (for a pushbutton)
int val = 0;     // variable for reading the pin status
int flag=0;



void setup(){
  gp.begin(9600);  
  blu.begin(9600);
  pinMode(lpin,OUTPUT);
  //pinMode(12,OUTPUT);

  //pinMode(ledPin, OUTPUT);  // declare LED as output
  digitalWrite(lpin,LOW);
  pinMode(inPin, INPUT); 

}


void loop(){
float flat, flon;
unsigned long age;
  //digitalWrite(12,HIGH);

  if(blu.available()>0){


    st="";

    command=(byte)blu.read();
    st+=command;
    if(st=="s"){
      digitalWrite(12,LOW);
    }

    if(st=="o"){
      //blu.write("LED ON#");


      noTone(8);
      digitalWrite(lpin,HIGH);
    }

    else if(st=="g"){

      blu.write("RECEIVING#");
      delay(100);
      digitalWrite(lpin,LOW);
    }
    else if(st=="r"){
      digitalWrite(lpin,HIGH);
      tone(8,155);
    }
    else if(st=="l"){
      gps.f_get_position(&flat, &flon, &age);
      print1_float(flat, TinyGPS::GPS_INVALID_F_ANGLE, 10, 6);
      print_float(flon, TinyGPS::GPS_INVALID_F_ANGLE, 11, 6);
      //blu.write("1.1$1.1#");

    }

 
    else{
      noTone(8);
      digitalWrite(lpin,LOW);

    }


  }

  val = digitalRead(inPin);  // read input value
  if (val==HIGH){

    if(flag==0){   // check if the input is HIGH (button released)
      //  digitalWrite(ledPin, HIGH);  // turn LED OFF
      blu.write("Ringing#");
      flag=1;

    }
    else{
      //digitalWrite(ledPin,LOW);
      flag=0;
    }

  }

}
static void print_float(float val, float invalid, int len, int prec)
{
  if (val == invalid)
  {
   
     blu.print("73.491318");
    blu.print(' ');
  }
  else
  {
    blu.print(val, prec);
    int vi = abs((int)val);
    int flen = prec + (val < 0.0 ? 2 : 1); // . and -
    flen += vi >= 1000 ? 4 : vi >= 100 ? 3 : vi >= 10 ? 2 : 1;
    for (int i=flen; i<len; ++i)
    blu.print(' ');
  }
  blu.write('#');
  
}
static void print1_float(float val, float invalid, int len, int prec)
{
  if (val == invalid)
  {
    
    blu.print("18.2760");
    blu.print(' ');
  }
  else
  {
    blu.print(val, prec);
    int vi = abs((int)val);
    int flen = prec + (val < 0.0 ? 2 : 1); // . and -
    flen += vi >= 1000 ? 4 : vi >= 100 ? 3 : vi >= 10 ? 2 : 1;
    for (int i=flen; i<len; ++i)
    blu.print(' ');
  }
  blu.write('$');
}


