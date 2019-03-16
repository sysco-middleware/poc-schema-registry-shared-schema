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

  private void register(String subjectName, Schema schema) throws Exception {
    if (client.getAllSubjects().contains(subjectName)) {
      if (client.testCompatibility(subjectName, schema)) {
        doRegister(subjectName, schema);
      } else {
        LOGGER.error("Schema {} is not compatible", subjectName);
      }
    } else {
      doRegister(subjectName, schema);
    }
  }

  private void doRegister(String subjectName, Schema schema) throws Exception {
    client.register(subjectName, schema);
    LOGGER.info("Schema {} registered", subjectName);
  }

  public static void main(String[] args) throws Exception {
    String schemaRegistryUrl = args[0];

    if (schemaRegistryUrl == null || schemaRegistryUrl.isEmpty()) {
      throw new RuntimeException("Schema Registry URL is empty, shutting down");
    }

    LOGGER.info("Schema Registry URL: {}", schemaRegistryUrl);

    SchemaRegistryClient client =
        new CachedSchemaRegistryClient(schemaRegistryUrl, 10_000);

    SchemaRegistry schemaRegistry = new SchemaRegistry(client);
    schemaRegistry.register("business-value", BusinessRecord.SCHEMA$);
  }
}
