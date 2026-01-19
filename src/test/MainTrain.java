package test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import test.RequestParser.RequestInfo;


public class MainTrain { // RequestParser
    

    private static void testParseRequest() {
        // Test data
        String request = "GET /api/resource?id=123&name=test HTTP/1.1\n" +
                            "Host: example.com\n" +
                            "Content-Length: 5\n"+
                            "\n" +
                            "filename=\"hello_world.txt\"\n"+
                            "\n" +
                            "hello world!\n"+
                            "\n" ;

        BufferedReader input=new BufferedReader(new InputStreamReader(new ByteArrayInputStream(request.getBytes())));
        try {
            RequestParser.RequestInfo requestInfo = RequestParser.parseRequest(input);

            // Test HTTP command
            if (!requestInfo.getHttpCommand().equals("GET")) {
                System.out.println("HTTP command test failed (-5)");
            }

            // Test URI
            if (!requestInfo.getUri().equals("/api/resource?id=123&name=test")) {
                System.out.println("URI test failed (-5)");
            }

            // Test URI segments
            String[] expectedUriSegments = {"api", "resource"};
            if (!Arrays.equals(requestInfo.getUriSegments(), expectedUriSegments)) {
                System.out.println("URI segments test failed (-5)");
                for(String s : requestInfo.getUriSegments()){
                    System.out.println(s);
                }
            } 
            // Test parameters
            Map<String, String> expectedParams = new HashMap<>();
            expectedParams.put("id", "123");
            expectedParams.put("name", "test");
            expectedParams.put("filename","\"hello_world.txt\"");
            if (!requestInfo.getParameters().equals(expectedParams)) {
                System.out.println("Parameters test failed (-5)");
            }

            // Test content
            byte[] expectedContent = "hello world!\n".getBytes();
            if (!Arrays.equals(requestInfo.getContent(), expectedContent)) {
                System.out.println("Content test failed (-5)");
            } 
            input.close();
        } catch (IOException e) {
            System.out.println("Exception occurred during parsing: " + e.getMessage() + " (-5)");
        }        
    }


    public static void testServer() throws Exception {
        int port = 8000 + new Random().nextInt(1000);
        int nThreads = 5;
        MyHTTPServer server = new MyHTTPServer(port, nThreads);

        // 1. הרשמת Servlet שמחבר שני מספרים
        server.addServlet("GET", "/sum", new Servlet() {
            @Override
            public void handle(RequestInfo ri, OutputStream toClient) throws IOException {
                // חילוץ פרמטרים וביצוע חישוב
                String a = ri.getParameters().get("a");
                String b = ri.getParameters().get("b");
                int sum = Integer.parseInt(a) + Integer.parseInt(b);
                toClient.write(String.valueOf(sum).getBytes());
                toClient.flush();
            }
            @Override
            public void close() throws IOException {}
        });

        int threadsBefore = Thread.activeCount();
        server.start(); // הפעלת השרת ברקע [cite: 313, 331]
        Thread.sleep(100); // זמן קצר להתארגנות השרת

        // בדיקה שנפתח רק ת'רד אחד נוסף עבור השרת
        if (Thread.activeCount() != threadsBefore + 1) {
            System.out.println("Error: start() should open exactly one additional thread (-10)");
        }

        // 2. יצירת לקוח ובדיקת תוצאה
        try (Socket client = new Socket("localhost", port);
             PrintWriter out = new PrintWriter(client.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()))) {

            // שליחת בקשת HTTP שתואמת ל-Servlet
            out.println("GET /sum?a=5&b=10 HTTP/1.1");
            out.println("Host: localhost");
            out.println(); // שורה ריקה לסיום ה-Header

            String response = in.readLine();
            if (!"15".equals(response)) {
                System.out.println("Server response test failed (-30). Expected 15, got: " + response);
            }
        }

        // 3. סגירה וניקוי משאבים
        server.close();
        Thread.sleep(2100); // המתנה של כ-2 שניות לפי ה-PDF

        if (Thread.activeCount() > threadsBefore) {
            System.out.println("Error: Some threads were not closed after server.close() (-20)");
        }
    }
    
    public static void main(String[] args) {
        testParseRequest(); // 40 points
        try{
            testServer(); // 60
        }catch(Exception e){
            System.out.println("your server throwed an exception (-60)");
        }
        System.out.println("done");
    }

}
