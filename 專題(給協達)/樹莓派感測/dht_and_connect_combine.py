
import threading
import socket
import sys
import time
import RPi.GPIO as GPIO
import time
import os

target_host = '172.20.10.3'
target_port = 8686



def picon():
    time.sleep(1)
    
    try:
        global cl
        cl=socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        cl.settimeout(2)
        cl.connect((target_host, target_port))
        
        
    except socket.timeout:
        print("Error")
        return 1
    except OSError:
        print("disconnect")
        return 1

    return 0


def consus():
    try:
        while(1):
            try:
                time.sleep(1)
                channel =4 #GPIO4
                data = []
                j = 0
                GPIO.setmode(GPIO.BCM)
                time.sleep(1)
                GPIO.setup(channel, GPIO.OUT)
                GPIO.output(channel, GPIO.LOW)
                time.sleep(0.02)
                GPIO.output(channel, GPIO.HIGH)
                GPIO.setup(channel, GPIO.IN)
                while GPIO.input(channel) == GPIO.LOW:
                  continue
                while GPIO.input(channel) == GPIO.HIGH:
                  continue
                while j < 40:
                  k = 0
                  while GPIO.input(channel) == GPIO.LOW:
                    continue
                  while GPIO.input(channel) == GPIO.HIGH:
                    k += 1
                    if k > 100:
                      break
                  if k < 8:
                    data.append(0)
                  else:
                    data.append(1)
                  j += 1
                print("sensor is working.")
                #print data
                humidity_bit = data[0:8]
                humidity_point_bit = data[8:16]
                temperature_bit = data[16:24]
                temperature_point_bit = data[24:32]
                check_bit = data[32:40]
                humidity = 0
                humidity_point = 0
                temperature = 0
                temperature_point = 0
                check = 0
                for i in range(8):
                  humidity += humidity_bit[i] * 2 ** (7-i)
                  humidity_point += humidity_point_bit[i] * 2 ** (7-i)
                  temperature += temperature_bit[i] * 2 ** (7-i)
                  temperature_point += temperature_point_bit[i] * 2 ** (7-i)
                  check += check_bit[i] * 2 ** (7-i)
                tmp = humidity + humidity_point + temperature + temperature_point
                
                return1=os.system('ping 8.8.8.8 -c 2')
                if return1:
                    print ("ping fail")
                    while (picon()):
                        pass
                else:
                    print ("ping ok")
                
                if check == tmp:
                  print("temperature :", temperature, "*C, humidity :", humidity, "%")
                  text = "re"+str(temperature)+str(humidity)
                  print (text)
                  b = bytes(text,"utf-8")
                  cl.sendall(b)
                else:
                  print("wrong")
                  print("temperature :", temperature, "*C, humidity :", humidity, "% check :", check, ", tmp :", tmp)
                  
                  print ("bit wrong")
                  
                                    
                
                GPIO.cleanup()

                
            except IOError:
                print("Send Error")
                while (picon()):
                    pass
            except socket.timeout:
                print("Disconnect")
                while (picon()):
                    pass
    except IOError:
        pass


def main():
    while(picon()):
        pass
  

    while(1):
        consus()
   # thc=threading.Thread(target=Picon)
   # thc.start()

main()


