# Proof of Concept: Schema Registry and Shared Schemas

As Schema Registry has a [known issue](https://github.com/confluentinc/schema-registry/issues/523) 
to publish Schemas that has shared Schemas, this project evaluates different approaches to publish
generated schemas.

## Approach 1: Avro JSON Schema to generated classes

Project: `schema-files`

This approach creates Avro schema files first (.avsc) and then uses `avro-maven-plugin` to create
Java classes, and register schemas from Java. 

## Approach 2: Schema classes to Avro JSON Schema files

Project: `schema-classes`

This approach create schemas from Java classes and register schemas using 
`kafka-schema-registry-maven-plugin`.

## How to run it

Start Docker environment:

```bash
docker-compose up -d
```

Build project:

```bash
mvn clean install
```

Check schemas:

```bash
curl localhost:8081/subjects | jq .
```