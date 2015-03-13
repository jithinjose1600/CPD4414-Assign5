/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package servlet;

import db.DBClass;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;
import javax.json.stream.JsonParser;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;


/**
 *
 * @author c0648991
 */
@Path("/servlet")
public class ControllerServlet {
    
    @GET
    @Produces("application/json")
    public  String doGet()  {
        StringWriter out = new StringWriter();
        JsonGeneratorFactory factory = Json.createGeneratorFactory(null);
        JsonGenerator gen = factory.createGenerator(out);
        String query="SELECT * FROM PRODUCTS";
        try (Connection conn = DBClass.getConnection()) {            
            PreparedStatement pstmt;               
                pstmt = conn.prepareStatement(query);
                ResultSet rs = pstmt.executeQuery();
                   gen.writeStartArray();
                while (rs.next()) {
                    gen.writeStartObject()
                            .write("productId", rs.getInt("ProductID"))
                .write("name", rs.getString("Name"))
                        .write("description", rs.getString("Description"))
                        .write("quantity", rs.getInt("Quantity"))
              .writeEnd();              
            }
            gen.writeEnd();
        gen.close(); 
          } catch (SQLException ex) {
            Logger.getLogger(ControllerServlet.class.getName()).log(Level.SEVERE, null, ex);
        }  
        return out.toString();
    } 
        
    
    @GET
    @Path("{id}")
    @Produces({"application/json"})
    public  String doGet(@PathParam("id") int id)  {
        StringWriter out = new StringWriter();
        JsonGeneratorFactory factory = Json.createGeneratorFactory(null);
        JsonGenerator gen = factory.createGenerator(out);
        try (Connection conn = DBClass.getConnection()) {
            
            PreparedStatement pstmt;
                String query="SELECT * FROM PRODUCTS WHERE ProductId = ?";
                pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, id);
                ResultSet rs = pstmt.executeQuery();
                if(rs.next())
                {
                gen.writeStartObject()
                .write("productId", rs.getInt("ProductID"))
                .write("name", rs.getString("Name"))
                        .write("description", rs.getString("Description"))
                        .write("quantity", rs.getInt("Quantity"))
              .writeEnd();
        gen.close();
                }
                else{
                    return "Invalid Id..";
                }
                } catch (SQLException ex) {
            Logger.getLogger(ControllerServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        return out.toString();
    }
   
    @POST
    @Consumes("application/json")
    public Response doPost(JsonObject obj) {
 
         String name=obj.getString("name");
         String description=obj.getString("description");
         String quantity=obj.getString("quantity");

        int res=0;
        int pid=0;
                      
                try (Connection conn = DBClass.getConnection()) {
                String query="INSERT INTO PRODUCTS (Name, Description, Quantity) VALUES (?, ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setString(1, name);
                pstmt.setString(2, description);
                pstmt.setString(3, quantity);
                res=pstmt.executeUpdate();
                String qry="SELECT ProductId FROM PRODUCTS WHERE  Name= ? and Description= ?";
                pstmt = conn.prepareStatement(qry);
                pstmt.setString(1, name);
                pstmt.setString(2, description);
                ResultSet rs = pstmt.executeQuery();
                if(rs.next())
                {
                    pid=rs.getInt("ProductId");
                }
              
                 } catch (SQLException ex) {
            Logger.getLogger(ControllerServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
          if(res<=0)
          {
              return Response.status(500).build();
          }
          else 
          {
              return Response.ok("http://localhost:8080/CPD4414-Assign4/webresources/servlet/"+pid).build();
          }
            
    }
    
   @PUT
   @Path("{id}")
   @Consumes("{application/json}")
    protected void doPut(@PathParam("id") String id, String str)
    {
        JsonParser parser = Json.createParser(new StringReader(str));
        Map<String, String> map = new HashMap<>();
        String key = "", value;
         while (parser.hasNext()) {
             JsonParser.Event evt = parser.next();
            switch (evt) {
                case KEY_NAME:
                    key = parser.getString();
                    break;
                case VALUE_STRING:
                    value = parser.getString();
                    map.put(key, value);
                    break;
                case VALUE_FALSE: case VALUE_NULL:
                case VALUE_NUMBER: 
                     value=Integer.toString(parser.getInt());
                    map.put(key, value);
                    break;
                case VALUE_TRUE:
                    map.put(key, "Error: Not String Value");
                    break;
            }
        }
         String name=map.get("name");
         String description=map.get("description");
         String quantity=map.get("quantity");
         String query="";
        try (Connection conn = DBClass.getConnection()) {
                query="UPDATE PRODUCTS SET Name=?, Description=?, Quantity=? WHERE ProductID=?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setString(1, name);
                pstmt.setString(2, description);
                pstmt.setString(3, quantity);
                pstmt.setString(4, id);
                pstmt.executeUpdate();
               
                 } catch (SQLException ex) {
            Logger.getLogger(ControllerServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
            
    } 
    
    @DELETE
   @Path("{id}")
   @Consumes("{application/json}")
    protected void doDelete(@PathParam("id") String id)
    {
        String query=null;
        
        try (Connection conn = DBClass.getConnection()) {
                query="DELETE FROM PRODUCTS WHERE ProductID=?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setString(1, id);
                pstmt.executeUpdate();
                
                 } catch (SQLException ex) {
            Logger.getLogger(ControllerServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
            
    } 

}
