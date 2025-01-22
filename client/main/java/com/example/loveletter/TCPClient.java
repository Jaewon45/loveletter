package org.example.loveletter;


import jakarta.json.Json;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import javassist.bytecode.SyntheticAttribute;
import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyWebTarget;
import org.glassfish.jersey.client.JerseyClientBuilder;
import jakarta.json.JsonObject;

import java.io.*;

import java.net.Socket;

@Path("/sysdev")
public class TCPclient {

    private static final int SERVER_PORT = 12345;

    @GET
    @Path("/dijkstra")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject dijkstra(
            @QueryParam("originLat") double originLat,
            @QueryParam("originLon") double originLon,
            @QueryParam("destinationLat") double destinationLat,
            @QueryParam("destinationLon") double destinationLon) {
        try {

            String response = sendRequest("dijkstra", originLat, originLon, destinationLat, destinationLon);
            JsonReader jsonReader = Json.createReader(new StringReader(response));
            JsonObject jsonObject1 = jsonReader.readObject();
            jsonReader.close();
            System.out.println("response to client in dijkstra is");
            System.out.println(jsonObject1);
            return jsonObject1;

            //return Response.ok(response, MediaType.APPLICATION_JSON).build();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
            //return Response.serverError().entity("Error while connecting to server").build();
        }
    }

    @GET
    @Path("/astar")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject aStar(
            @QueryParam("originLat") double originLat,
            @QueryParam("originLon") double originLon,
            @QueryParam("destinationLat") double destinationLat,
            @QueryParam("destinationLon") double destinationLon) {
        try {
            System.out.println("in client before sending");
            String response = sendRequest("astar", originLat, originLon, destinationLat, destinationLon);
            System.out.println("in client after sending");

            System.out.println("Response from client is");
            System.out.println(response);

            JsonReader jsonReader = Json.createReader(new StringReader(response));
            JsonObject jsonObject = jsonReader.readObject();
            jsonReader.close();

            /*
            final JsonObject jsonObject = Json.createReader(new StringReader(response)).readObject();
            System.out.println("new type is");
            System.out.println(jsonObject.getClass());
             */ return jsonObject;
            //return Response.ok(response, MediaType.APPLICATION_JSON).build();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
            //return Response.serverError().entity("Error while connecting to server").build();
        }

    }

    private String sendRequest(String algorithm, double originLat, double originLon, double destinationLat, double destinationLon) throws IOException {
        try (Socket socket = new Socket("localhost", SERVER_PORT); PrintWriter out = new PrintWriter(socket.getOutputStream(), true); BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Construct request
            String request = algorithm + " " + originLat + " " + originLon + " " + destinationLat + " " + destinationLon;

            System.out.println("REQEUST GETTING SENT");
            System.out.println(request);
            // Send request to server
            out.println(request);

            // Receive response from server
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
                System.out.println("Received partial response: " + line);
                response.append("\n");
                System.out.println("The type of line is: " + line.getClass());
                if (in.readLine().equals("")) {
                    System.out.println("About to break");
                    break;
                }
            }

            //String response = null;
            //response = in.readLine();
            // displaying server reply
            System.out.println("Server replied "
                    + response);

            System.out.println("The full response is");
            System.out.println(response.toString());
            return response.toString();
        }
    }

}
