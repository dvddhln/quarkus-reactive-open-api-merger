# openapi-merger

This project uses Quarkus, the Supersonic Subatomic Java Framework.

This project demonstrates the usage of collecting a set of open api specifications urls, merging the models and presenting them through 
two different views, Swagger UI and Redoc. The latter makes use of custom extensions, with can be configured adding custom vendor group
tags.

Solution is partially presented here

[Building a Reactive polling mechanism with SmallRye Mutiny and Quarkus](https://dvddhln.medium.com/building-a-reactive-polling-mechanism-with-smallrye-mutiny-and-quarkus-f86802653140
)

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

### Swagger UI

Swagger UI is located at

http://localhost:8080

### Redoc UI
Redoc UI is located at

http://localhost:8080/redoc

The markdown files in the application.yaml are displayed through custom vendor tags for redoc and grouped
according to their 'group' key. The following img displays the result form the merged open api specifications.

<img src="https://raw.githubusercontent.com/dvddhln/quarkus-reactive-open-api-merger/master/img/merged_redoc.png" />

### Configuration
The configuration makes use of inlined markdown files for the custom extension groups.

    documentation:
      host: myserverhost
      base-url: /
      description: Description stores
      version: 1.0.0
      oas-version: 3.0.2
      title: Petstore Platform
      group: API Management
      openapi:
        services:
          - display-name: Petstore1
            url: https://petstore3.swagger.io/api/v3/openapi.json
            hide: false
          - display-name: Petstore2
            url: https://raw.githubusercontent.com/OAI/OpenAPI-Specification/main/examples/v3.0/petstore.json
            hide: false
    
    logo:
      background-color: "#fafafa"
      img-url: https://raw.githubusercontent.com/Redocly/redoc/master/docs/images/redoc-logo.png
    extensions:
      - group: Documentation2
        display-name: Changelog
        hide: false
        description: |
          All notable changes to this project will be documented in this file.
    
          The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/)
    
          ## [1.0.0] - 2021-06-09
    
          ### Added
    
          - New visual swagger experience by [@dvddhln](https://github.com/dvddhln).
          - Added Documentation and Changelog by [@dvddhln](https://github.com/dvddhln).
      - group: Documentation1
        display-name: Environments
        hide: false
        description: |
          * [Development](/development)
          * [Staging](/staging) 
          * [Production](/production)
    
          ##### Swagger Views
          * [SwaggerUI](/)
          * [Redoc](/redoc)
    polling:
      unit: "SECONDS"
      initialDelay: 5
      frequency: 15