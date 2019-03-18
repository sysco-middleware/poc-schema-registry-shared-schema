package no.sysco.avro;

import java.util.HashSet;
import java.util.Set;
import org.apache.avro.Schema;

public class Schemas {
  static final Set<Schema> SCHEMA_SET = new HashSet<>();

  static {
    SCHEMA_SET.add(BusinessRecord.SCHEMA$);
    // add more schemas here if needed.
  }
}
