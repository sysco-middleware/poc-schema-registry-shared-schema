package no.sysco.avro;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class AvroGenerator {

  public static void main(String[] args) throws Exception {
    String businessRecordSchema = BusinessRecord.schema.toString();
    Path path = Paths.get("src/main/avro/business2.avsc");
    //Files.createFile(path);
    Files.write(path, businessRecordSchema.getBytes(), StandardOpenOption.CREATE);
  }
}
