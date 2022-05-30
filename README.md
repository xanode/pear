# Pear - Distributed Hash Table
Java implementation of Tox, the peer-to-peer instant-messaging and video-calling protocol.
This project take place in the IT development project at Télécom SudParis.

This branch concerns the development of the Kademlia-based distributed hash table.

## Compilation instructions
This project use libsodium for encryption. In order to build the project, you need to have the library
and to be sure that the link written in the class DHT.java is the good one (it is still platform dependant).
Then, to build the project, you only have to run the command `mvn package` (you'll need maven!).
The JAR file (`pear-0.1-jar-with-dependencies.jar`) will be in the `target/` directory.