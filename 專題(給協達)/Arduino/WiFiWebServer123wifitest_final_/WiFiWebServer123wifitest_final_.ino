#ifndef UNIT_TEST
#include <Arduino.h>
#endif
#include <IRremoteESP8266.h>
#include <IRsend.h>
#include <ESP8266WiFi.h>

const char* ssid = "Togo__";
const char* password = "togotogo";


WiFiServer server(80);
IRsend irsend(4);

void setup() {
  irsend.begin();
  Serial.begin(115200);
  delay(10);

  Serial.println();
  Serial.println();
  Serial.print("Connecting to ");
  Serial.println(ssid);
  
  WiFi.begin(ssid, password);
  
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("");
  Serial.println("WiFi connected");
  
  server.begin();
  Serial.println("Server started");

  Serial.println(WiFi.localIP());
}

void loop() {
  WiFiClient client = server.available();
  if (!client) {
    return;
  }
  
  Serial.println("new client");
  while(!client.available()){
    delay(5);
  }
  
  String req = client.readStringUntil('\r');
  Serial.println(req);
  client.flush();

  if (req.indexOf("/gpio/0") != -1)
  {
    irsend.sendNEC(0xAA5511EE, 32);
    Serial.println("led open");
    client.flush();
  }
  else if (req.indexOf("/gpio/1") != -1)
  {
    irsend.sendNEC(0xAA5533CC, 32);
    Serial.println("light high");
    client.flush();
  }
  else if (req.indexOf("/gpio/2") != -1)
  {
    irsend.sendNEC(0xAA5544BB, 32);
    Serial.println("light low");
    client.flush();
  }
  else{
    Serial.println("invalid request");
    client.stop();
    return;
  }

  client.flush();

  String s = "HTTP/1.1 200 OK\r\nContent-Type: text/html\r\n\r\n<!DOCTYPE HTML>\r\n<html>\r\nGPIO is now ";
  s += "working";
  s += "</html>\n";

  client.print(s);
  delay(1);
  Serial.println("Client disonnected");
  client.flush();
}

