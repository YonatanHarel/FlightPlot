package me.drton.flightplot.export;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import java.io.*;
import java.util.*;

public class CSVUtils {
    final static String CSV_EXT = ".csv";

    public static void exportToCsv(String dirName, Map<String, List<Map<String, Object>>> ulogData) {
        for (Map.Entry<String, List<Map<String, Object>>> entry : ulogData.entrySet()) {
            File file = new File(dirName + entry.getKey() + CSV_EXT);
            try {
                Writer writer = new FileWriter(file, true);
                csvWriter(entry.getValue(), writer);
                Reader reader = new FileReader(file);
                csvReader(reader);
            } catch (IOException e) {
                System.out.println("failed to create file. \n cause:");
                System.out.println((e.getCause().getMessage()));
            }
        }
    }

    /**
     * @param listOfMap
     * @param writer
     * @throws IOException
     */
    public static void csvWriter(List<Map<String, Object>> listOfMap, Writer writer) throws IOException {
        CsvSchema schema = null;
        CsvSchema.Builder schemaBuilder = CsvSchema.builder();
        if (listOfMap != null && !listOfMap.isEmpty()) {
            for (String col : listOfMap.get(0).keySet()) {
                schemaBuilder.addColumn(col);
            }
            schema = schemaBuilder.build().withLineSeparator("\r").withHeader();
        }
        CsvMapper mapper = new CsvMapper();
        mapper.writer(schema).writeValues(writer).writeAll(listOfMap);
        writer.flush();
    }

    /**
     *
     * @param collection
     * @param writer
     * @param
     * @throws IOException
     */
    public static  void csvWriter(Collection collection, Writer writer) throws IOException {
        if (collection != null && collection.size() > 0) {
            CsvMapper mapper = new CsvMapper();
            Object[] objects = collection.toArray();
            Class type = objects[0].getClass();
            CsvSchema schema = mapper.schemaFor(type).withHeader();
            mapper.writer(schema).writeValues(writer).write(objects);
        } else {
            writer.write("No Data");
        }
        writer.flush();
    }


    /**
     * @param reader
     * @throws IOException
     */

    public static void csvReader(Reader reader) throws IOException {
        Iterator<Map<String, String>> iterator = new CsvMapper()
                .readerFor(Map.class)
                .with(CsvSchema.emptySchema().withHeader())
                .readValues(reader);
        while (iterator.hasNext()) {
            Map<String, String> keyVals = iterator.next();
            System.out.println(keyVals);
        }
    }
}
