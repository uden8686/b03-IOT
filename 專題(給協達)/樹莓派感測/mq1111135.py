import threading
import socket
import sys
import time
import RPi.GPIO as GPIO
import os



target_host = '172.20.10.3'
target_port = 8686


def picon():
    time.sleep(1)

    try:
        global cl
        cl=socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        cl.settimeout(5)
        cl.connect((target_host, target_port))
        
        
    except socket.timeout:
        print("Error")
        return 1
    except OSError:
        print("Disconnect")
        return 1

    return 0


def consus():
    try:
        while(1):
            
            try:
                GPIO.setmode(GPIO.BCM)
                GPIO.setup(16, GPIO.IN, pull_up_down=GPIO.PUD_DOWN)

                def action(pin):
                    return1=os.system('ping 8.8.8.8 -c 2')
                    if return1:
                        print ("ping fail")
                        while(picon()):
                            pass
                    else:
                        print ("ping ok")
                    time.sleep(3)
                    print ("Danger!")
                    text = "da"
                    b = bytes(text,"utf-8")
                    
                    
                    cl.sendall(b)
                    return

                GPIO.add_event_detect(16, GPIO.RISING)
                GPIO.add_event_callback(16, action)

                try:
                    while True:
                        return1=os.system('ping 8.8.8.8 -c 2')
                        if return1:
                            print ("ping fail")
                            while(picon()):
                                pass
                        else:
                            print ("ping ok")                        
                        text = "Safe"
                        print(text)
                        b = bytes(text,"utf-8")
                        cl.sendall(b)                        
                        time.sleep(3)

                except IOError:
                    print("Send Error")
                    GPIO.cleanup()
                    while (picon()):
                        pass
                    
            except IOError:
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

