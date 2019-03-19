package no.sysco.avro;

import java.util.HashMap;
import java.util.Map;
import org.apache.avro.Schema;
// todo: make it dynamic via args or config
public class Schemas {
  // Schema:isKey
  static final Map<Schema, Boolean> SCHEMA_MAP = new HashMap<>();

  static {
    SCHEMA_MAP.put(BusinessRecord.SCHEMA$, false);
    // add more schemas here if needed.
  }
}
