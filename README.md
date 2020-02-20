# vs-OwlNet-chat
CIS 4307 - Logical Clocks and Vector Clocks - Luke Jakielaszek - 915377673 - tug52339

## Application Overview
The andorid application has been split into three activities: MainActivity, SettingsAvtivity, and ChatActivity. All socket communication
is handled through a static SocketManager independent of activities which makes use of vectorclocks for message ordering.

### MainActivity
On the activities creation, a handler for the join button and settings button is created. The activity also sets the default IP and port
for socket communication through the SocketManager. 

If the join button is clicked, the registration function is initiated through 
the SocketManager's transmitMessage function. In addition, the SocketManager is initialized with the current user that has just 
registered. On successful registration, a success message is displayed to the user and the ChatActivity is launched. On failure, 
the failure message is displayed (timeout failure or failure indicated by server).

If the settings button is clicked, the SettingsActivity is launched.

### SettingsActivity
The SettingsActivity displays the current IP and port of the SocketManager on launch. The user can fill out the new IP and port if
desired. Clicking the submit button will store the current text fields into the socket for later use. An error is displayed if any of
the text fields have malformed information.

### ChatActivity
On successfuly registration, the chatActivity is launched. Two buttons are displayed: Deregister and initiate chat. 

If the user clicks the deregister button, the SocketManager's transmitMessage function is called with the parameter deregister. On 
successful deregistration, the success is displayed to the user and an error message will appear if the user attempts to deregister
or initiate chat after successful deregistration. On failure, the failure message is displayed (timeout failure or failure 
indicated by server).

If the user clicks the initiate chat button, a retrieve_chat_log message is constructed through the socketManager and then sent to the
server. A priority queue is also initialized to hold message objects (server responses). The client will then receive server messages
until a socketTimeoutException occurs. Each received message is added to the priority queue. After receiveing all messages, the 
priority queue is looped through and converted to a string to be displayed for the user. If a timestamp conflict is detected (when
the messageComparator returns 0), a conflict indicator is added next to the message on the display.

### SocketManager
The socketManager is used to store all active socket information and send / receive messages from the server independent of the
activities. The socketmanage uses static methods and variables to store username, uuid and other information. The createoutputmessage
function simply creates a message that follows the server's format with a specified message type, username and uuid. The SendMessage
sends a message to the server without any additional overhead such as waiting for responses and ACKs. The receiveMessage attempts
to receive a message from the server and indicates if a timeout occurs. The getstringError function is used to convert an error message 
sent by the server to a string representation for user display. The transmitMessage function implements the 5 message timeout register
/ deregister functionality. It sends a message up to 5 times, only sending the next message after a timeout occurs. This method will
construct the toast to display to a user to indicate success / failure. 

### LamportClock
The lamport clock class implements all lamport clock functionality by using a single integer to track logical time. All unit tests were
passed with 100%. 

The happenedBefore(Clock other) function within this class returns false if the logical time (single integer) of the other clock
if less than or equal to the integer representation of the current clock. If this clocks time is strictly greater than the other clock's
time, true is returned. 

The function update(Clock other), will set this clock's integer to the largest of the clock in which we are comparing to.

The function tick(integer pid) will increment this clock's logical time by 1 and ignore the pid since lamportclocks do not use pids.

ToString and setclockfromstring simply perform string manipulations to send/receive clocks from the server as strings.

### VectorClock
The vector clock class implements all vector clock functionality by using a hashmap (mapping process id to the logical time) to track 
logical time of each process. All unit tests were passed with 100%. 

The happenedBefore(Clock other) function within this class will loop through each 
key of the other clock and check if this clock has it. If this clock does not contain all other keys within the other clock, false is
returned. If any of the keys within this clock map to a value greater than or equal to the other clock's key value pair, false is
returned. Finally, this function loops through the all keys of this clock and check to ensure that they also exist in the other clock. 
If a key does not exist in the other clock but exists in this clock, false is returned. If all these conditions pass, true is returned.

The addProcess(pid, time) will simply add a process to the vectorClocks mapping with the indicated time.

The Update(Clock other) will loop through every key in other. If that key deos not exist in this clock, it is added with the 
corresponding time value. If that key does exist, the key/value pairs for this clock and other clock are compared and the maximum of
the two are taken.

The tick(integer pid) will increment the key/value pair by 1 for the process if it exists in this clock or set the pid to 1 if the pid 
did not exist as a key in the key/value mapping for this clock.

ToString and setclockfromstring simply perform string manipulations to send/receive clocks from the server as strings.

### Message_A
The Message_A class is used to store all information of messages received from the server along with the Clock representation of their
timestamp. A message comparator was implemented which returns -1 if a the left message happens before the right message, 1 if the
right message happens before the left message, or 0 if the messages conflict.

## Limitations
The android application does not follow the activty lifecycle followed by android. Therefore, if you rotate the screen
during a connected session, the socket and current user information will be lost. Additionally, this application assumes
that the user does not spam click the buttons. Therefore, if a user does spam click, an error may occur since the
server received multiple response before timeout and the client may be expecting multiple responses for client / server
interaction. Also, if the built in back button is pressed on the ChatActivity screen without first derigestering, the 
user will remain registered forever. Other users are still able to register / derigester though. (Therefore, it is useful for testing
multiple users).
