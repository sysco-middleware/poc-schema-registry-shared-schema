package no.sysco.avro;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;

public class BusinessRecord {

  static final Schema schema = SchemaBuilder.record("BusinessRecord")
      .fields()
      .requiredString("id")
      .name("common")
      .doc("Common")
      .type(Common.schema)
      .noDefault()
      .endRecord();
}
