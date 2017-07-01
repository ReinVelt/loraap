# Loraap



## Lora Receiver
The Lora receiver is created from a RFM95 Lora transceiver module and a ESP12E Devkit.
Both components are available at AliExpress for 6-7 euro. The complete kit  with antenna
and a nice casing will be available soon at http://mechanicape.nl. ![Lora receiver][receiver]

### Lora receiver Firmware
The receiver module uses an ESP8266 microcontroller to decode LoraWan messages to JSON and
send these JSON messages to a Android device using the USB-OTG port. The firmware for the
microcontroller is compatible with the Arduino development environment. The sourcecode and binaries
for the firmware are located in the Firmware folder in the root of this project.







## Android App
The Android app receives raw json messages from the hardware device. All decoding and crypto stuff is handled by the Android app. Also app-key management should be handled by the Android app to enable custom message handlers.





[receiver]:lora-receiver.jpg "lora receiver"
[receiver-detail]:lora-receiver-detail.jpg "lora receiver detail"
