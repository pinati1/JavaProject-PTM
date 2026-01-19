package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class MyHTTPServer extends Thread implements HTTPServer{

    private         final int port;
    private         final int nThreads;
    private         final ExecutorService threadPool;
    private         volatile boolean stop = false;
    private         Map<String,Servlet> GETreq = new HashMap<>();
    private         Map<String,Servlet> POSTreq = new HashMap<>();
    private         Map<String,Servlet> DELETEreq = new HashMap<>();

    public MyHTTPServer(int port,int nThreads){
        this.port= port;
        this.nThreads =nThreads;
        this.threadPool = Executors.newFixedThreadPool(nThreads);
    }

    public void addServlet(String httpCommanmd, String uri, Servlet s){
        switch (httpCommanmd.toUpperCase()){
            case "GET"-> GETreq.put(uri,s);
            case "POST"-> POSTreq.put(uri,s);
            case "DELETE"->DELETEreq.put(uri,s);
        }
    }

    public void removeServlet(String httpCommanmd, String uri){
        switch (httpCommanmd.toUpperCase()){
            case "GET"->GETreq.remove(uri);
            case "POST"->POSTreq.remove(uri);
            case "DELETE"->DELETEreq.remove(uri);
        }
    }
    private void handleClient(Socket clientSocket){
        try(BufferedReader reader = new BufferedReader
                (new InputStreamReader(clientSocket.getInputStream()));
                OutputStream toClient = clientSocket.getOutputStream()) {
                RequestParser.RequestInfo reqInfo = RequestParser.parseRequest(reader);
                Map<String,Servlet> currentMap = switch (reqInfo.getHttpCommand().toUpperCase()){
                  case "GET" -> this.GETreq;
                  case "POST" ->this.POSTreq;
                  case "DELETE" ->this.DELETEreq;
                  default -> null;
                };
                if(currentMap!=null){
                    Servlet s = getLongestMatch(currentMap,reqInfo.getUri());
                    if(s!=null){
                        s.handle(reqInfo,toClient);
                    }
                }
        }catch (IOException e){
            if(!stop) e.printStackTrace();
        }finally {
            try {clientSocket.close();} catch (IOException ignored){}
        }
    }

    private Servlet getLongestMatch(Map<String,Servlet>map, String uri) {
        String bestMatch=null;
        for(String prefix : map.keySet()){
            if(uri.startsWith(prefix)){
                if(bestMatch==null||prefix.length()>bestMatch.length()){
                    bestMatch = prefix;
                }
            }
        }
        return bestMatch!=null ? map.get(bestMatch) : null;
    }

    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setSoTimeout(1000);
            while(!stop){
                try{
                    Socket clientSocket = serverSocket.accept();
                    threadPool.execute(()->handleClient(clientSocket));
                }catch (SocketTimeoutException e){}
            }
        } catch (IOException e) {
            if (!stop) e.printStackTrace();
        }
    }

    public void close(){
        stop = true;
        threadPool.shutdown();
        try {
            if(!threadPool.awaitTermination(2, TimeUnit.SECONDS)){
                threadPool.shutdownNow();
            }
        }catch (InterruptedException e){
            threadPool.shutdownNow();
        }
    }

}
