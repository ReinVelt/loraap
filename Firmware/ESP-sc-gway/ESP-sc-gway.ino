/*******************************************************************************
   Copyright (c) 2016 Maarten Westenberg version for ESP8266
   Copyright (c) 2015 Thomas Telkamp for initial Raspberry Version

   All rights reserved. This program and the accompanying materials
   are made available under the terms of the Eclipse Public License v1.0
   which accompanies this distribution, and is available at
   http://www.eclipse.org/legal/epl-v10.html

   Notes:
   - Once call gethostbyname() to get IP for services, after that only use IP
 	 addresses (too many gethost name makes ESP unstable)
   - Only call yield() in main stream (not for background NTP sync).

 *******************************************************************************/

//
#define VERSION " ! V. 1.1.2, 160415"

#include <Esp.h>
#include <string.h>
#include <stdio.h>
#include <sys/types.h>
#include <unistd.h>
#include <fcntl.h>
#include <cstdlib>
#include <sys/time.h>
#include <cstring>
#include <SPI.h>
#include <Time.h>								// http://playground.arduino.cc/code/time
extern "C" {
#include "user_interface.h"
#include "lwip/err.h"
#include "lwip/dns.h"
}
#include <pins_arduino.h>
#include <gBase64.h>							// https://github.com/adamvr/arduino-base64 (I changed the name)
#include "ESP-sc-gway.h"						// This file contains configuration of GWay

int debug = 2;									// Debug level! 0 is no msgs, 1 normal, 2 is extensive

using namespace std;

byte currentMode = 0x81;
char message[256];
char b64[256];
bool sx1272 = true;								// Actually we use sx1276/RFM95
byte receivedbytes;

uint32_t cp_nb_rx_rcv;
uint32_t cp_nb_rx_ok;
uint32_t cp_nb_rx_bad;
uint32_t cp_nb_rx_nocrc;
uint32_t cp_up_pkt_fwd;

enum sf_t { SF7 = 7, SF8, SF9, SF10, SF11, SF12 };

uint8_t MAC_array[6];
char MAC_char[18];

/*******************************************************************************

   Configure these values if necessary!

 *******************************************************************************/

// SX1276 - ESP8266 connections
int ssPin = 15;									// GPIO15, D8
int dio0  = 5;									// GPIO5,  D1
int RST   = 0;									// GPIO16, D0, not connected

// Set spreading factor (SF7 - SF12)
sf_t sf = SF7;

// Set center frequency. If in doubt, choose the first one, comment all others
// Each "real" gateway should support the first 3 frequencies according to LoRa spec.
uint32_t  freq = 868100000; 					// Channel 0, 868.1 MHz
//uint32_t  freq = 868300000; 					// Channel 1, 868.3 MHz
//uint32_t  freq = 868500000; 					// in Mhz! (868.5)
//uint32_t  freq = 867100000; 					// in Mhz! (867.1)
//uint32_t  freq = 867300000; 					// in Mhz! (867.3)
//uint32_t  freq = 867500000; 					// in Mhz! (867.5)
//uint32_t  freq = 867700000; 					// in Mhz! (867.7)
//uint32_t  freq = 867900000; 					// in Mhz! (867.9)
//uint32_t  freq = 868800000; 					// in Mhz! (868.8)
//uint32_t  freq = 869525000; 					// in Mhz! (869.525)
// TTN defines an additional channel at 869.525Mhz using SF9 for class B. Not used

// Set location, description and other configuration parameters
// Defined in ESP-sc_gway.h
//
float lat			          = _LAT;						// Configuration specific info...
float lon			          = _LON;
int   alt			          = _ALT;
char  platform[24]	    = _PLATFORM; 			// platform definition
char  email[40]		      = _EMAIL;    			// used for contact email
char  description[64]   = _DESCRIPTION;		// used for free form description







uint32_t lasttime;




// ============================================================================
// Set all definitions for Gateway
// ============================================================================

#define REG_FIFO                    0x00
#define REG_FIFO_ADDR_PTR           0x0D
#define REG_FIFO_TX_BASE_AD         0x0E
#define REG_FIFO_RX_BASE_AD         0x0F
#define REG_RX_NB_BYTES             0x13
#define REG_OPMODE                  0x01
#define REG_FIFO_RX_CURRENT_ADDR    0x10
#define REG_IRQ_FLAGS               0x12
#define REG_DIO_MAPPING_1           0x40
#define REG_DIO_MAPPING_2           0x41
#define REG_MODEM_CONFIG            0x1D
#define REG_MODEM_CONFIG2           0x1E
#define REG_MODEM_CONFIG3           0x26
#define REG_SYMB_TIMEOUT_LSB  		  0x1F
#define REG_PKT_SNR_VALUE			      0x19
#define REG_PAYLOAD_LENGTH          0x22
#define REG_IRQ_FLAGS_MASK          0x11
#define REG_MAX_PAYLOAD_LENGTH 		  0x23
#define REG_HOP_PERIOD              0x24
#define REG_SYNC_WORD				        0x39
#define REG_VERSION	  				      0x42

#define SX72_MODE_RX_CONTINUOS      0x85
#define SX72_MODE_TX                0x83
#define SX72_MODE_SLEEP             0x80
#define SX72_MODE_STANDBY           0x81

#define PAYLOAD_LENGTH              0x40

// LOW NOISE AMPLIFIER
#define REG_LNA                     0x0C
#define LNA_MAX_GAIN                0x23
#define LNA_OFF_GAIN                0x00
#define LNA_LOW_GAIN		    	0x20

// CONF REG
#define REG1                        0x0A
#define REG2                        0x84

#define SX72_MC2_FSK                0x00
#define SX72_MC2_SF7                0x70
#define SX72_MC2_SF8                0x80
#define SX72_MC2_SF9                0x90
#define SX72_MC2_SF10               0xA0
#define SX72_MC2_SF11               0xB0
#define SX72_MC2_SF12               0xC0

#define SX72_MC1_LOW_DATA_RATE_OPTIMIZE  0x01 	// mandated for SF11 and SF12

// FRF
#define REG_FRF_MSB					0x06
#define REG_FRF_MID					0x07
#define REG_FRF_LSB					0x08

#define FRF_MSB						0xD9		// 868.1 Mhz
#define FRF_MID						0x06
#define FRF_LSB						0x66

#define BUFLEN 2048  							//Max length of buffer

#define PROTOCOL_VERSION  1
#define PKT_PUSH_DATA 0
#define PKT_PUSH_ACK  1
#define PKT_PULL_DATA 2
#define PKT_PULL_RESP 3
#define PKT_PULL_ACK  4

#define TX_BUFF_SIZE  2048
#define STATUS_SIZE	  512						// This should(!) be enough based on the static text part.. was 1024


// ----------------------------------------------------------------------------
// DIE is not use actively in the source code anymore.
// It is replaced by a Serial.print command so we know that we have a problem
// somewhere.
// There are at least 3 other ways to restart the ESP. Pick one if you want.
// ----------------------------------------------------------------------------
void die(const char *s)
{
  Serial.println(s);
  delay(50);
  // system_restart();						// SDK function
  // ESP.reset();
  abort();									// Within a second
}

// ----------------------------------------------------------------------------
// Print leading '0' digits for hours(0) and second(0) when
// printing values less than 10
// ----------------------------------------------------------------------------
void printDigits(int digits)
{
  // utility function for digital clock display: prints preceding colon and leading 0
  if (digits < 10)
    Serial.print(F("0"));
  Serial.print(digits);
}





// ----------------------------------------------------------------------------
// Convert a float to string for printing
// f is value to convert
// p is precision in decimal digits
// val is character array for results
// ----------------------------------------------------------------------------
void ftoa(float f, char *val, int p) {
  int j = 1;
  int ival, fval;
  char b[6];

  for (int i = 0; i < p; i++) {
    j = j * 10;
  }

  ival = (int) f;								// Make integer part
  fval = (int) ((f - ival) * j);					// Make fraction. Has same sign as integer part
  if (fval < 0) fval = -fval;					// So if it is negative make fraction positive again.
  // sprintf does NOT fit in memory
  strcat(val, itoa(ival, b, 10));
  strcat(val, ".");							// decimal point

  itoa(fval, b, 10);
  for (int i = 0; i < (p - strlen(b)); i++) strcat(val, "0");
  // Fraction can be anything from 0 to 10^p , so can have less digits
  strcat(val, b);
}




// =================================================================================
// LORA GATEWAY FUNCTIONS
// The LoRa supporting functions are in the section below

// ----------------------------------------------------------------------------
// The SS (Chip select) pin is used to make sure the RFM95 is selected
// ----------------------------------------------------------------------------
void selectreceiver()
{
  digitalWrite(ssPin, LOW);
}

// ----------------------------------------------------------------------------
// ... or unselected
// ----------------------------------------------------------------------------
void unselectreceiver()
{
  digitalWrite(ssPin, HIGH);
}

// ----------------------------------------------------------------------------
// Read one byte value, par addr is address
// Returns the value of register(addr)
// ----------------------------------------------------------------------------
byte readRegister(byte addr)
{
  selectreceiver();
  SPI.beginTransaction(SPISettings(50000, MSBFIRST, SPI_MODE0));
  SPI.transfer(addr & 0x7F);
  uint8_t res = SPI.transfer(0x00);
  SPI.endTransaction();
  unselectreceiver();
  return res;
}

// ----------------------------------------------------------------------------
// Write value to a register with address addr.
// Function writes one byte at a time.
// ----------------------------------------------------------------------------
void writeRegister(byte addr, byte value)
{
  unsigned char spibuf[2];

  spibuf[0] = addr | 0x80;
  spibuf[1] = value;
  selectreceiver();
  SPI.beginTransaction(SPISettings(50000, MSBFIRST, SPI_MODE0));
  SPI.transfer(spibuf[0]);
  SPI.transfer(spibuf[1]);
  SPI.endTransaction();
  unselectreceiver();
}

// ----------------------------------------------------------------------------
// This LoRa function reads a message from the LoRa transceiver
// returns true when message correctly received or fails on error
// (CRC error for example)
// ----------------------------------------------------------------------------
bool receivePkt(char *payload)
{
  // clear rxDone
  writeRegister(REG_IRQ_FLAGS, 0x40);

  int irqflags = readRegister(REG_IRQ_FLAGS);

  cp_nb_rx_rcv++;											// Receive statistics counter

  //  payload crc: 0x20
  if ((irqflags & 0x20) == 0x20)
  {
    if (DEBUG) { Serial.println(F("CRC error")); }
    writeRegister(REG_IRQ_FLAGS, 0x20);
    return false;
  } else {

    cp_nb_rx_ok++;										// Receive OK statistics counter

    byte currentAddr = readRegister(REG_FIFO_RX_CURRENT_ADDR);
    byte receivedCount = readRegister(REG_RX_NB_BYTES);
    receivedbytes = receivedCount;

    writeRegister(REG_FIFO_ADDR_PTR, currentAddr);

    for (int i = 0; i < receivedCount; i++)
    {
      payload[i] = (char)readRegister(REG_FIFO);
    }
    //yield();
  }
  return true;
}

// ----------------------------------------------------------------------------
// Setup the LoRa environment on the connected transceiver.
// - Determine the correct transceiver type (sx1272/RFM92 or sx1276/RFM95)
// - Set the frequency to listen to (1-channel remember)
// - Set Spreading Factor (standard SF7)
// The reset RST pin might not be necessary for at least the RGM95 transceiver
// ----------------------------------------------------------------------------
void SetupLoRa()
{
  digitalWrite(RST, HIGH);
  delay(100);
  digitalWrite(RST, LOW);
  delay(100);

  byte version = readRegister(REG_VERSION);					// Read the LoRa chip version id
  if (version == 0x22) {
    // sx1272
    Serial.println(F("# SX1272 detected, starting."));
    sx1272 = true;
  } else {
    // sx1276?
    digitalWrite(RST, LOW);
    delay(100);
    digitalWrite(RST, HIGH);
    delay(100);
    version = readRegister(REG_VERSION);
    if (version == 0x12) {
      // sx1276
      Serial.println(F("# SX1276 detected, starting."));
      sx1272 = false;
    } else {
      Serial.print(F("# Unrecognized transceiver, version: "));
      Serial.println(version, HEX);
      //sx1272 = true;
      die("");
    }
  }

  writeRegister(REG_OPMODE, SX72_MODE_SLEEP);

  // set frequency
  uint64_t frf = ((uint64_t)freq << 19) / 32000000;
  writeRegister(REG_FRF_MSB, (uint8_t)(frf >> 16) );
  writeRegister(REG_FRF_MID, (uint8_t)(frf >> 8) );
  writeRegister(REG_FRF_LSB, (uint8_t)(frf >> 0) );

  writeRegister(REG_SYNC_WORD, 0x34); // LoRaWAN public sync word

  // Set spreading Factor
  if (sx1272) {
    if (sf == SF11 || sf == SF12) {
      writeRegister(REG_MODEM_CONFIG, 0x0B);
    } else {
      writeRegister(REG_MODEM_CONFIG, 0x0A);
    }
    writeRegister(REG_MODEM_CONFIG2, (sf << 4) | 0x04);
  } else {
    if (sf == SF11 || sf == SF12) {
      writeRegister(REG_MODEM_CONFIG3, 0x0C);
    } else {
      writeRegister(REG_MODEM_CONFIG3, 0x04);
    }
    writeRegister(REG_MODEM_CONFIG, 0x72);
    writeRegister(REG_MODEM_CONFIG2, (sf << 4) | 0x04);
  }

  if (sf == SF10 || sf == SF11 || sf == SF12) {
    writeRegister(REG_SYMB_TIMEOUT_LSB, 0x05);
  } else {
    writeRegister(REG_SYMB_TIMEOUT_LSB, 0x08);
  }
  writeRegister(REG_MAX_PAYLOAD_LENGTH, 0x80);
  writeRegister(REG_PAYLOAD_LENGTH, PAYLOAD_LENGTH);
  writeRegister(REG_HOP_PERIOD, 0xFF);
  writeRegister(REG_FIFO_ADDR_PTR, readRegister(REG_FIFO_RX_BASE_AD));

  // Set Continous Receive Mode
  writeRegister(REG_LNA, LNA_MAX_GAIN);  // max lna gain
  writeRegister(REG_OPMODE, SX72_MODE_RX_CONTINUOS);


}




// ----------------------------------------------------------------------------
// Receive a LoRa package
//
// Receive a LoRa message and fill the buff_up char buffer.
// returns values:
// - returns the length of string returned in buff_up
// - returns -1 when no message arrived.
// ----------------------------------------------------------------------------
int receivepacket(char *buff_up) {

  long int SNR;
  int rssicorr;
  char cfreq[12] = {0};										// Character array to hold freq in MHz

  if (digitalRead(dio0) == 1)									// READY?
  {
    if (receivePkt(message)) {
      byte value = readRegister(REG_PKT_SNR_VALUE);
      if ( value & 0x80 ) // The SNR sign bit is 1
      {
        // Invert and divide by 4
        value = ( ( ~value + 1 ) & 0xFF ) >> 2;
        SNR = -value;
      }
      else
      {
        // Divide by 4
        SNR = ( value & 0xFF ) >> 2;
      }

      if (sx1272) {
        rssicorr = 139;
      } else {											// Probably SX1276 or RFM95
        rssicorr = 157;
      }

      if (DEBUG >= 1) {
        Serial.print("{\"RSSI\":[{");
        Serial.print(F("\"PacketRSSI\":"));
        Serial.print(readRegister(0x1A) - rssicorr);
        Serial.print(F(",\"RSSI\":"));
        Serial.print(readRegister(0x1B) - rssicorr);
        Serial.print(F(",\"SNR\":"));
        Serial.print(SNR);
        Serial.print(F(",\"Length\":"));
        Serial.print((int)receivedbytes);
        Serial.println("}]}");
        yield();
      }

      int j;
      // XXX Base64 library is nopad. So we may have to add padding characters until
      // 	length is multiple of 4!
      int encodedLen = base64_enc_len(receivedbytes);		// max 341
      base64_encode(b64, message, receivedbytes);			// max 341

      //j = bin_to_b64((uint8_t *)message, receivedbytes, (char *)(b64), 341);
      //fwrite(b64, sizeof(char), j, stdout);

      int buff_index = 0;

      // pre-fill the data buffer with fixed fields
      buff_up[0] = PROTOCOL_VERSION;
      buff_up[3] = PKT_PUSH_DATA;

      // XXX READ MAC ADDRESS OF ESP8266
      buff_up[4]  = MAC_array[0];
      buff_up[5]  = MAC_array[1];
      buff_up[6]  = MAC_array[2];
      buff_up[7]  = 0xFF;
      buff_up[8]  = 0xFF;
      buff_up[9]  = MAC_array[3];
      buff_up[10] = MAC_array[4];
      buff_up[11] = MAC_array[5];

      // start composing datagram with the header
      uint8_t token_h = (uint8_t)rand(); 					// random token
      uint8_t token_l = (uint8_t)rand(); 					// random token
      buff_up[1] = token_h;
      buff_up[2] = token_l;
      buff_index = 12; /* 12-byte header */

      // TODO: tmst can jump if time is (re)set, not good.
      struct timeval now;
      gettimeofday(&now, NULL);
      uint32_t tmst = (uint32_t)(now.tv_sec * 1000000 + now.tv_usec);

      // start of JSON structure that will make payload
      memcpy((void *)(buff_up + buff_index), (void *)"{\"rxpk\":[", 9);
      buff_index += 9;
      buff_up[buff_index] = '{';
      ++buff_index;
      j = snprintf((char *)(buff_up + buff_index), TX_BUFF_SIZE - buff_index, "\"tmst\":%u", tmst);
      buff_index += j;
      ftoa((double)freq / 1000000, cfreq, 6);						// XXX This can be done better
      j = snprintf((char *)(buff_up + buff_index), TX_BUFF_SIZE - buff_index, ",\"chan\":%1u,\"rfch\":%1u,\"freq\":%s", 0, 0, cfreq);
      buff_index += j;
      memcpy((void *)(buff_up + buff_index), (void *)",\"stat\":1", 9);
      buff_index += 9;
      memcpy((void *)(buff_up + buff_index), (void *)",\"modu\":\"LORA\"", 14);
      buff_index += 14;
      /* Lora datarate & bandwidth, 16-19 useful chars */
      switch (sf) {
        case SF7:
          memcpy((void *)(buff_up + buff_index), (void *)",\"datr\":\"SF7", 12);
          buff_index += 12;
          break;
        case SF8:
          memcpy((void *)(buff_up + buff_index), (void *)",\"datr\":\"SF8", 12);
          buff_index += 12;
          break;
        case SF9:
          memcpy((void *)(buff_up + buff_index), (void *)",\"datr\":\"SF9", 12);
          buff_index += 12;
          break;
        case SF10:
          memcpy((void *)(buff_up + buff_index), (void *)",\"datr\":\"SF10", 13);
          buff_index += 13;
          break;
        case SF11:
          memcpy((void *)(buff_up + buff_index), (void *)",\"datr\":\"SF11", 13);
          buff_index += 13;
          break;
        case SF12:
          memcpy((void *)(buff_up + buff_index), (void *)",\"datr\":\"SF12", 13);
          buff_index += 13;
          break;
        default:
          memcpy((void *)(buff_up + buff_index), (void *)",\"datr\":\"SF?", 12);
          buff_index += 12;
      }
      memcpy((void *)(buff_up + buff_index), (void *)"BW125\"", 6);
      buff_index += 6;
      memcpy((void *)(buff_up + buff_index), (void *)",\"codr\":\"4/5\"", 13);
      buff_index += 13;
      j = snprintf((char *)(buff_up + buff_index), TX_BUFF_SIZE - buff_index, ",\"lsnr\":%li", SNR);
      buff_index += j;
      j = snprintf((char *)(buff_up + buff_index), TX_BUFF_SIZE - buff_index, ",\"rssi\":%d,\"size\":%u", readRegister(0x1A) - rssicorr, receivedbytes);
      buff_index += j;
      memcpy((void *)(buff_up + buff_index), (void *)",\"data\":\"", 9);
      buff_index += 9;

      // Use gBase64 library
      encodedLen = base64_enc_len(receivedbytes);		// max 341
      j = base64_encode((char *)(buff_up + buff_index), message, receivedbytes);

      buff_index += j;
      buff_up[buff_index] = '"';
      ++buff_index;

      // End of packet serialization
      buff_up[buff_index] = '}';
      ++buff_index;
      buff_up[buff_index] = ']';
      ++buff_index;
      // end of JSON datagram payload */
      buff_up[buff_index] = '}';
      ++buff_index;
      buff_up[buff_index] = 0; 						// add string terminator, for safety    
     
      //write json data to console
      Serial.println((char *)(buff_up + 12));		// DEBUG: display JSON payload
      

      return (buff_index);

    } // received a message
  } // dio0=1
  return (-1);
}





// ========================================================================
// MAIN PROGRAM (SETUP AND LOOP)

// ----------------------------------------------------------------------------
// Setup code (one time)
// ----------------------------------------------------------------------------
void setup () {
  Serial.begin(_BAUDRATE);						// As fast as possible for bus

  delay(1500);
  Serial.println();
  yield();
  Serial.println("# APE SINGLE CHANNEL LORA GATEWAY");
  yield();

  if (debug >= 1) {
    Serial.print(F("# debugging enabled, debug="));
    Serial.println(debug);
    yield();
  }




  pinMode(ssPin, OUTPUT);
  pinMode(dio0, INPUT);
  pinMode(RST, OUTPUT);

  SPI.begin();
  delay(1000);
  SetupLoRa();
  delay(500);

  

  uint32_t version = ESP.getChipId();
  MAC_array[0] = version & 15;
  MAC_array[1] = version >> 4 & 15;
  MAC_array[2] = version >> 8 & 15;
  MAC_array[3] = version >> 12 & 15;
  MAC_array[4] = version >> 16 & 15;
  MAC_array[5] = version >> 20 & 15;

  Serial.print("# Gateway ID: ");
  Serial.print(MAC_array[0], HEX);
  Serial.print(MAC_array[1], HEX);
  Serial.print(MAC_array[2], HEX);
  Serial.print(MAC_array[3], HEX);
  Serial.print(MAC_array[4], HEX);
  Serial.print(MAC_array[5], HEX);
  Serial.print(", Listening at SF");
  Serial.print(sf);
  Serial.print(" on ");
  Serial.print((double)freq / 1000000);
  Serial.println(" Mhz.");


  delay(1000);											// Wait after setup
  Serial.println("# ------------------------------------------------------------------");
}

// ----------------------------------------------------------------------------
// LOOP
// This is the main program that is executed time and time again.
// We need to geive way to the bacjend WiFi processing that
// takes place somewhere in the ESP8266 firmware and therefore
// we include yield() statements at important points.
//
// Note: If we spend too much time in user processing functions
//	and the backend system cannot do its housekeeping, the watchdog
// function will be executed which means effectively that the
// program crashes.
// ----------------------------------------------------------------------------
void loop ()
{
  int buff_index;
  char buff_up[TX_BUFF_SIZE]; 						// buffer to compose the upstream packet

  // Receive Lora messages
  if ((buff_index = receivepacket(buff_up)) >= 0) {	// read is successful
    yield();
    //sendUdp(buff_up, buff_index);					// We can send to multiple sockets if necessary
  }
  else {
    // No message received
  }
  yield();




}
