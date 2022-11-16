package com.ubs.demo.gatling;

import com.google.common.collect.Maps;
import com.hubspot.jinjava.Jinjava;
import io.swagger.models.*;
import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.parameters.PathParameter;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.RefProperty;
import io.swagger.models.refs.RefFormat;
import io.swagger.parser.SwaggerParser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class generateGatlingTests {
    static Map<String, Object> context = Maps.newHashMap();

    static Map<String, String> properties = Maps.newHashMap();
    static Map<String, String> pathParmeters = Maps.newHashMap();

    static String fileName = "src/test/resources/gatlingTestTemplate.jinja";
    static String template;
    static File gatTests = new File("");
    static FileWriter fileWriter;

    static {
        try {
            template = Files.readString(Paths.get(fileName));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    static Jinjava jinjava = new Jinjava();


    public generateGatlingTests() throws IOException {

    }

    public static void main(String[] args) throws IOException {
        Swagger swagger = new SwaggerParser().read("src/test/resources/swaggerDocs/swagger.json");


        for(Map.Entry<String, Path> entry : swagger.getPaths().entrySet()) {
            System.out.println("===Path====\n"+entry.getKey());
            context.put("package","com.ubs.demo.perfTests;");
            context.put("path",entry.getKey());
            printOperations(swagger, entry.getValue().getOperationMap());


            context.clear();
            properties.clear();
            pathParmeters.clear();
        }
    }

    private static void printOperations(Swagger swagger, Map<HttpMethod, Operation> operationMap) throws IOException {
        for(Map.Entry<HttpMethod, Operation> op : operationMap.entrySet()) {
            context.put("method",op.getKey().toString());
            context.put("OperationID", op.getValue().getOperationId());
            gatTests = new File("src/test/java/com/ubs/demo/perfTests/"+op.getValue().getOperationId()+".java");

            fileWriter = new FileWriter(gatTests);
            PrintWriter printWriter = new PrintWriter(fileWriter);

            System.out.println(op.getKey() + " - " + op.getValue().getOperationId());
            System.out.println("Parameters:");
            for(Parameter p : op.getValue().getParameters()) {
                if(p instanceof BodyParameter) {
                    printBody(swagger, (BodyParameter) p);
                } else {
                    String paramType = p.getClass().getSimpleName();
                    if(p instanceof PathParameter) {
                        paramType = "path";
                    }
                    System.out.println(p.getName() + " : " + paramType);
                    pathParmeters.put(p.getName(),paramType);
                }
            }
            context.put("properties", properties);
            System.out.println();
            printResponses(swagger, op.getValue().getResponses());
            System.out.println();

            printWriter.print(jinjava.render(template, context));
            printWriter.close();
        }
    }

    private static void printBody(Swagger swagger, BodyParameter p) {
        System.out.println("BODY: ");

        RefProperty rp = new RefProperty(p.getSchema().getReference());
        printReference(swagger, rp);
    }

    private static void printResponses(Swagger swagger, Map<String, Response> responseMap) {
        System.out.println("Responses:");
        for(Map.Entry<String, Response> response : responseMap.entrySet()) {
            System.out.println(response.getKey() + ": " + response.getValue().getDescription());

            if(response.getValue().getSchema() instanceof RefProperty) {
                RefProperty rp = (RefProperty)response.getValue().getSchema();
                printReference(swagger, rp);
            }

            if(response.getValue().getSchema() instanceof ArrayProperty) {
                ArrayProperty ap = (ArrayProperty)response.getValue().getSchema();
                if(ap.getItems() instanceof RefProperty) {
                    RefProperty rp = (RefProperty)ap.getItems();
                    System.out.println(rp.getSimpleRef() + "[]");
                    printReference(swagger, rp);
                }
            }
        }
    }

    private static void printReference(Swagger swagger, RefProperty rp) {
        if(rp.getRefFormat().equals(RefFormat.INTERNAL) &&
                swagger.getDefinitions().containsKey(rp.getSimpleRef())) {
            Model m = swagger.getDefinitions().get(rp.getSimpleRef());

            if(m instanceof ArrayModel) {
                ArrayModel arrayModel = (ArrayModel)m;
                System.out.println(rp.getSimpleRef() + "[]");
                if(arrayModel.getItems() instanceof RefProperty) {
                    RefProperty arrayModelRefProp = (RefProperty)arrayModel.getItems();
                    printReference(swagger, arrayModelRefProp);
                }
            }

            if(m.getProperties() != null) {
                for (Map.Entry<String, Property> propertyEntry : m.getProperties().entrySet()) {
                    System.out.println("  " + propertyEntry.getKey() + " : " + propertyEntry.getValue().getType());
                    properties.put(propertyEntry.getKey(),propertyEntry.getValue().getType());

                }
            }
        }
    }

}

