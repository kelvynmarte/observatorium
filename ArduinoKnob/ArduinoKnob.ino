void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);

}

void loop() {
  // put your main code here, to run repeatedly:
  if (Serial) {
    // Serial.write(map(analogRead(A0), 0, 1024, 0, 128));
    Serial.println(map(analogRead(A0), 0, 1024, 0, 360));
    delay(60);
  }
  

}
