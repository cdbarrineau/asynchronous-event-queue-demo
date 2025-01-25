# Asynchronous Event Queue Demo
This project contains an example of how to use an asynchronous queue listener that wait for events from a producer.  There are two ways to achieve this:
* Simple (basic) waiting where a listener waits for a specific class to be placed into a list.
* Advanced waiting that takes in a Function to compare properties of an object in a list and only returns an object that matches the given function.

There are many ways to accomplish this type of waiting for an asynchronous event but this project uses a Java Class name to base the initial checks.
Therefore, both of these methods can support classes of any type so that events placed on the queue can be in combination of events that, at least for
this project, inherit from a common base class.

The main functionality in each of the Basic and Advanced will wait for a given period of time before giving up waiting for an event.

I've found this functionality to be very useful in Integration Testing (and even unit testing) where there are asynchronous events
that the code must wait for in order to validate said event was sent.  Typically this can be achieved by a Thread.sleep(...) (as is the case of the
Basic Listener) however, using tools like SonarLint flags Thread.sleep(...) as an error.  That is the reason that the Advanced queue listener was
created, to get around these kinds of linting errors.  It uses a combination of a thread pool executor and a linked blocking queue.

## This is a maven project
To run this application, simply clone this repo then at the command line type:
* `mvn clean compile package`
* java -jar target/asynchronous-event-queue-0.0.1.jar

This will produce the following output.  As can be seen from the below output, the Basic Listener receives all events of a specific class
however the Advanced Listener only receives one that has a java Function to test for equality of the id (id of 2).  This is very useful if
the same event type is received but to verify a test, you need to check for specific properties.
```
Basic Listener received event: HealthEvent(id=1, name=Basic Listener Event number 1)
Advanced Listener received event: HealthEvent(id=2, name=Advanced Listener Event number 2)
Basic Listener received event: HealthEvent(id=2, name=Basic Listener Event number 2)
Basic Listener received event: HealthEvent(id=3, name=Basic Listener Event number 3)
Basic Listener received event: HealthEvent(id=4, name=Basic Listener Event number 4)
Basic Listener received event: HealthEvent(id=5, name=Basic Listener Event number 5)
Waiting for null event on Basic Listener.
Timeout waiting for event of type: com.cdb.eventqueue.event.HealthEvent, all received messages: []
Basic Listener received event: null
```