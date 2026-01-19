package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

public class RequestParser {

    public static RequestInfo parseRequest(BufferedReader reader) throws IOException {
        String requestLine = reader.readLine();
        if (requestLine == null || requestLine.isEmpty()) return null;

        String[] parts = requestLine.split(" ");
        if (parts.length < 2) return null;

        String httpCommand = parts[0];
        String fullUri = parts[1];
        Map<String, String> parameters = new HashMap<>();

        String[] uriParts = fullUri.split("\\?", 2);
        String path = uriParts[0];

        if (uriParts.length > 1) {
            String queryString = uriParts[1];
            for (String pair : queryString.split("&")) {
                String[] keyValue = pair.split("=", 2);
                if (keyValue.length == 2) {
                    parameters.put(keyValue[0], keyValue[1]);
                }
            }
        }


        List<String> segmentsList = new ArrayList<>();
        for (String segment : path.split("/")) {
            if (!segment.isEmpty()) {
                segmentsList.add(segment);
            }
        }
        String[] uriSegments = segmentsList.toArray(new String[0]);

        String line;
        int contentLength = 0;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            if (line.toLowerCase().startsWith("content-length:")) {
                contentLength = Integer.parseInt(line.split(":")[1].trim());
            }
        }

        if (reader.ready()) {
            reader.mark(1000);
            line = reader.readLine();

            if (line != null && line.startsWith("filename=")) {
                String filename = line.split("=", 2)[1];
                parameters.put("filename", filename);
                if (reader.ready()) reader.readLine();
            } else {
                try {
                    reader.reset();
                } catch (IOException e) {
                }
            }
        }
        StringBuilder contentBuilder = new StringBuilder();
        if (reader.ready()) {
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                contentBuilder.append(line).append("\n");
            }
        }

        byte[] content = contentBuilder.toString().getBytes();
        return new RequestInfo(httpCommand, fullUri, uriSegments, parameters, content);
    }
	
	// RequestInfo given internal class
    public static class RequestInfo {
        private final String httpCommand;
        private final String uri;
        private final String[] uriSegments;
        private final Map<String, String> parameters;
        private final byte[] content;

        public RequestInfo(String httpCommand, String uri, String[] uriSegments, Map<String, String> parameters, byte[] content) {
            this.httpCommand = httpCommand;
            this.uri = uri;
            this.uriSegments = uriSegments;
            this.parameters = parameters;
            this.content = content;
        }

        public String getHttpCommand() {
            return httpCommand;
        }

        public String getUri() {
            return uri;
        }

        public String[] getUriSegments() {
            return uriSegments;
        }

        public Map<String, String> getParameters() {
            return parameters;
        }

        public byte[] getContent() {
            return content;
        }
    }
}
