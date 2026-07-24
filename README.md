# Features

HTPP file streaming server allows the user to host a file streaming service, with any client compatible with HTTP streaming.

These are some of its features:
* Directories and files management
* Linux and windows compatible
* File transfering between clients (it allows the user to start downloading a file that other client is currently uploading)

# Configuration
The server is built with Spring Boot, `application.properties` file will need to have the next two configurations:

`fileServer.rootPath = /path/example` -> absolute path to the root folder that will contain all the files\
`fileServer.cors = http://example:0000` -> http client that will be used to interact with the server

Since the project uses Hibernate, you must also configure the appropriate database and JPA properties (such as the datasource, dialect, username, and password) according to your environment.
