package no.sysco.avro;

import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.serializers.subject.RecordNameStrategy;
import io.confluent.kafka.serializers.subject.TopicNameStrategy;
import io.confluent.kafka.serializers.subject.TopicRecordNameStrategy;
import io.confluent.kafka.serializers.subject.strategy.SubjectNameStrategy;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Consumer;
import org.apache.avro.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SchemaRegistry {
  private static final Logger LOGGER = LoggerFactory.getLogger(SchemaRegistry.class);

  private final SchemaRegistryClient client;

  private SchemaRegistry(SchemaRegistryClient client) {
    this.client = client;
  }

  public static void main(String[] args) {
    LOGGER.info("args: {}", Arrays.asList(args));
    String schemaRegistryUrl = args[0];

    if (schemaRegistryUrl == null || schemaRegistryUrl.isEmpty()) {
      throw new RuntimeException("Schema Registry URL is empty, shutting down");
    }

    LOGGER.info("Schema Registry URL: {}", schemaRegistryUrl);

    SchemaRegistryClient client =
        new CachedSchemaRegistryClient(schemaRegistryUrl, 10_000);

    SchemaRegistry schemaRegistry = new SchemaRegistry(client);

    String operation = args[1];

    String strategy = args[2];
    SubjectNameStrategy<Schema> subjectNameStrategy =
        schemaRegistry.getSubjectNameStrategy(strategy);

    String topicName = args[3]; // This could be improved, depending on subject name strategy.

    Consumer<Map<Schema, Boolean>> registerFunc =  schemaMap -> schemaMap.forEach((schema, isKey) -> {
      String subjectName = subjectNameStrategy.subjectName(topicName, isKey, schema);
      schemaRegistry.register(subjectName, schema);
    });

    Consumer<Map<Schema, Boolean>> testCompatibilityFunc =  schemaMap -> schemaMap.forEach((schema, isKey) -> {
      String subjectName = subjectNameStrategy.subjectName(topicName, isKey, schema);
      schemaRegistry.testCompatibility(subjectName, schema);
    });

    switch (operation) {
      case Operation.REGISTER_OP: {
        testCompatibilityFunc.accept(Schemas.SCHEMA_MAP);
        registerFunc.accept(Schemas.SCHEMA_MAP);
        break;
      }
      case Operation.TEST_COMPATIBILITY_OP: {
        testCompatibilityFunc.accept(Schemas.SCHEMA_MAP);
        break;
      }
    }
  }

  private SubjectNameStrategy<Schema> getSubjectNameStrategy(String strategy) {
    switch (strategy) {
      case Strategy.TOPIC_RECORD_NAME_STRATEGY:
        return new TopicRecordNameStrategy();
      case Strategy.RECORD_NAME_STRATEGY:
        return new RecordNameStrategy();
      case Strategy.TOPIC_NAME_STRATEGY:
        return new TopicNameStrategy();
      default:
        return new TopicNameStrategy();
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

  static class Strategy {
    static final String RECORD_NAME_STRATEGY = "record-name";
    static final String TOPIC_RECORD_NAME_STRATEGY = "topic-record-name";
    static final String TOPIC_NAME_STRATEGY = "topic-name";
  }
}
