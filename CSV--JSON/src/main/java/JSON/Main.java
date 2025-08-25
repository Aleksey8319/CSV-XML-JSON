package JSON;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class Main {
    public static void main(String[] args) {
        // CSV - JSON парсер
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> list = parseCSV(columnMapping, fileName);
        String fileJSON = "data.json";
        String json = listToJson(list);
        writeString(json, fileJSON);

        // XML - JSON парсер
        String fileName2 = "data.xml";
        List<Employee> list1 = parseXML(fileName2);
        String fileJSON2 = "data2.json";
        String json2 = listToJson(list1);
        writeString(json2, fileJSON2);
    }

    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        List<Employee> staff = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            staff = csv.parse();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return staff;
    }

    private static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        String json = gson.toJson(list, listType);
        return json;
    }

    private static void writeString(String json, String fileJSON) {
        try (FileWriter file = new FileWriter(fileJSON)) {
            file.write(json);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<Employee> parseXML(String fileName2) {
        List<String> elements = new ArrayList<>();
        List<Employee> list = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(fileName2));
            Node root = doc.getDocumentElement();
            NodeList nodeList = root.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (Node.ELEMENT_NODE == node.getNodeType()) {
                    Element employee = (Element) node;
                    for (int j = 0; j < nodeList.getLength(); j++) {
                        elements.add(0, (employee.getElementsByTagName("id").item(j).getTextContent()));
                        elements.add(1, (employee.getElementsByTagName("firstName").item(j).getTextContent()));
                        elements.add(2, (employee.getElementsByTagName("lastName").item(j).getTextContent()));
                        elements.add(3, (employee.getElementsByTagName("country").item(j).getTextContent()));
                        elements.add(4, (employee.getElementsByTagName("age").item(j).getTextContent()));


                        list.add(new Employee(
                                Long.parseLong(elements.get(0)),
                                elements.get(1),
                                elements.get(2),
                                elements.get(3),
                                Integer.parseInt(elements.get(4))));
                        elements.clear();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}