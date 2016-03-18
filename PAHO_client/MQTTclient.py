import paho.mqtt.client as mqtt

def on_connect(client,userdata,rc):
	print("Connected with result code" +str(rc))

client=mqtt.Client()
client.on_connect=on_connect
client.connect("127.0.0.1",2099,60)

while(True):
	x=raw_input("data:")
	client.publish("TV",x)

client.loop_forever()
