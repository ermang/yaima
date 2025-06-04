# YAIMA

yet another instant messaging application 

## subheading

something something

### subsubheading

something something 



How will clients discover each other? (Server as directory + signaling)

How will you handle NAT traversal? (Use STUN/TURN servers or similar)

can you go into detail for these two steps ?



---

CREATE TABLE app_user (
id SERIAL PRIMARY KEY,
username VARCHAR(25) not NULL
);

CREATE TABLE friend (
id SERIAL PRIMARY KEY,
user_id INTEGER NOT NULL,
friend_id INTEGER NOT NULL,
FOREIGN KEY (user_id) REFERENCES app_user(id),
FOREIGN KEY (friend_id) REFERENCES app_user(id),
UNIQUE (user_id, friend_id)
);

--drop table friend;  
--drop table app_user;

INSERT INTO app_user (username) values ('bob');
INSERT INTO app_user (username) values ('alice');

select * from app_user;
select * from friend;

INSERT INTO friend (user_id, friend_id) values (1, 2);  
INSERT INTO friend (user_id, friend_id) values (2, 1);

---

while true  
  read 2 bytes  
  read packet  

read 2 bytes => this tells how long the next packet is, maps to short
packets length is known from this step, read exactly that many bytes

packet format

packet => header + body  
header => 3 bytes String, lets you know the type of body.  
body => there are multiple types of body,  
serialization/deserialization logic should be extracted from header  

# Packet Types  

"STT"  => Status Packet

3 | V

3 byte string => ONL OFF  => online offline

variable length byte String => length of username 

---  

"SMS" => Send Message Packet

V | V | V

from  
to  
message  

these 3 string fields are converted to byteArray, and joined via seperator with ASCII code 0, not the number 0 or the string 0 but its byte representation, receiver should split based on that and extract fields

# TODO / QUIRKS

TextBox scroll to end doesnt work when readonly is set to true  

https://stackoverflow.com/questions/72553607/lanterna-how-to-make-a-textbox-scroll-to-the-bottom-upon-adding-more-text  

added an input filter to only accept navigational key presses, and ignore the rest  
hack solution but works  



