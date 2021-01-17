# CustomerServiceChat

Server and Client applications allow communication between a 
server (representative) and client. 

Server
Clicking 'Next Client' will initiate the chat between server and client. 
If no clients are waiting to talk to the server(representative), then 
clicking 'Next Client' will display "No clients waiting" in the server GUI. 
Once connected to client, you can only click 'Next Client' again once 
you click 'End Session'. 

Client
Once application started, client must wait for server to start 
the session. Once session is started, client can send messages 
to server.

Code, including the code for serverThread extends thread and 
clientThread extends thread, was taken from a college class. 
