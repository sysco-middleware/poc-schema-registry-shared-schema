package no.sysco.avro;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;

public class Common {
  static final Schema schema = SchemaBuilder.record("CommonRecord")
      .namespace("no.sysco.avro")
      .doc("Common schema")
      .fields()
      .requiredString("field")
      .endRecord();
}
