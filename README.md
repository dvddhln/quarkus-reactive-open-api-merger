# openapi-merger

This project uses Quarkus, the Supersonic Subatomic Java Framework.

This project demonstrates the usage of collecting a set of open api specifications urls, merging the models and presenting them through 
two different views, Swagger UI and Redoc. The latter makes use of custom extensions, with can be configured adding custom vendor group
tags.


## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

### Swagger UI

Swagger UI is locted at

http://localhost:8080

### Redoc UI
Redoc UI is located at

http://localhost:8080/redoc

The markdown files in the application.yaml are displayed through custom vendor tags for redoc and grouped
according to their 'group' key. The following img displays the result form the merged open api specifications.

<img src="https://raw.githubusercontent.com/dvddhln/quarkus-reactive-open-api-merger/master/img/merged_redoc.png" />