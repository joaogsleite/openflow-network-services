JAR=target/floodlight.jar

tests:
	ant

build:
	ant 

clean:
	ant clean

debug:
	java -Xdebug -Xrunjdwp:transport=dt_socket,address=8123,server=y,suspend=y -jar $(JAR)

run:
	java -jar $(JAR)
