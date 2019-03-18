package no.sysco.avro;

import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import org.apache.avro.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SchemaRegistry {
  static final Logger LOGGER = LoggerFactory.getLogger(SchemaRegistry.class);

  final SchemaRegistryClient client;

  private SchemaRegistry(SchemaRegistryClient client) {
    this.client = client;
  }

  public static void main(String[] args) {
    String schemaRegistryUrl = args[0];

    if (schemaRegistryUrl == null || schemaRegistryUrl.isEmpty()) {
      throw new RuntimeException("Schema Registry URL is empty, shutting down");
    }

    LOGGER.info("Schema Registry URL: {}", schemaRegistryUrl);

    SchemaRegistryClient client =
        new CachedSchemaRegistryClient(schemaRegistryUrl, 10_000);

    SchemaRegistry schemaRegistry = new SchemaRegistry(client);

    String operation = args[1];

    String topicName = args[2]; // This could be improved, depending on subject name strategy.

    switch (operation) {
      case Operation.REGISTER_OP:
        Schemas.SCHEMA_SET.forEach(schema -> {
          String subjectName = String.format("%s-%s", topicName, schema.getFullName());
          schemaRegistry.register(subjectName, schema);
        });
        schemaRegistry.register("business-value", BusinessRecord.SCHEMA$);
      case Operation.TEST_COMPATIBILITY_OP:
        Schemas.SCHEMA_SET.forEach(schema -> {
          String subjectName = String.format("%s-%s", topicName, schema.getFullName());
          schemaRegistry.testCompatibility(subjectName, schema);
        });
    }
  }

  private void testCompatibility(String subjectName, Schema schema) throws RuntimeException {
    try {
      LOGGER.info("Testing subject schema compatibility: {}", subjectName);
      if (client.testCompatibility(subjectName, schema)) {
        LOGGER.info("Subject {} schema change is compatible", subjectName);
      } else {
        LOGGER.warn("Subject {} schema change is incompatible", subjectName);
      }
    } catch (Exception e) {
      LOGGER.info("Failure testing subject {} compatibility", subjectName);
      throw new RuntimeException(e);
    }
  }

  private void register(String subjectName, Schema schema) throws RuntimeException {
    try {
      LOGGER.info("Registering subject: {}", subjectName);
      client.register(subjectName, schema);
      LOGGER.info("Schema {} registered", subjectName);
    } catch (Exception e) {
      LOGGER.info("Failure registering subject {}", subjectName);
      throw new RuntimeException(e);
    }
  }

  static class Operation {
    static final String REGISTER_OP = "register";
    static final String TEST_COMPATIBILITY_OP = "test-compatibility";
  }
}
