package no.sysco.avro;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.avro.Schema;

public class Schemas {
  // Schema:isKey
  static final Map<Schema, Boolean> SCHEMA_MAP = new HashMap<>();

  static {
    SCHEMA_MAP.put(BusinessRecord.SCHEMA$, false);
    // add more schemas here if needed.
  }
}
