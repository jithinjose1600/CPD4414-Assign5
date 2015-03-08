/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package servlet;

import db.DBClass;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;
import javax.json.stream.JsonParser;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;


/**
 *
 * @author c0648991
 */
@Path("/servlet")
public class ControllerServlet {
    
    @GET
    @Produces("application/json")
    public  String doGetAll()  {
        int rowCount=0;
        StringBuilder sb= new StringBuilder();
        String query="SELECT * FROM PRODUCTS";
        try (Connection conn = DBClass.getConnection()) {            
            PreparedStatement pstmt=null;               
                pstmt = conn.prepareStatement(query);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                if(rowCount>0)
                {
                    sb.append(",\n ");
                }
                sb.append(String.format("{ \"productId\" : "+ rs.getInt("ProductID")+", \"name\" : \""+rs.getString("Name")+"\", \"description\" : \""+rs.getString("Description")+"\", \"quantity\" : "+rs.getInt("Quantity")+" }"));   
                rowCount+=1;
            }
            
          
          } catch (SQLException ex) {
            Logger.getLogger(ControllerServlet.class.getName()).log(Level.SEVERE, null, ex);
        }  
         if(rowCount>1)
            {
                String products="["+sb.toString()+"]";
                //return "[";
                //return sb.toString();
                return products;
            }
            else
            {
            return  sb.toString();
            } 
    } 
        
    
    @GET
    @Path("{id}")
    @Produces({"application/json"})
    public  String doGet(@PathParam("id") Integer id)  {
        StringWriter out = new StringWriter();
        JsonGeneratorFactory factory = Json.createGeneratorFactory(null);
        JsonGenerator gen = factory.createGenerator(out);
        try (Connection conn = DBClass.getConnection()) {
            
            PreparedStatement pstmt=null;
                String query="SELECT * FROM PRODUCTS WHERE ProductId=?";
                pstmt = conn.prepareStatement(query);
                pstmt.setString(1, String.valueOf(id));
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
    public void doPost(String str) {
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
        String query=null;
 
                      
                try (Connection conn = DBClass.getConnection()) {
                query="INSERT INTO PRODUCTS (Name, Description, Quantity) VALUES (?, ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setString(1, name);
                pstmt.setString(2, description);
                pstmt.setString(3, quantity);
                pstmt.executeUpdate();
                
                 } catch (SQLException ex) {
            Logger.getLogger(ControllerServlet.class.getName()).log(Level.SEVERE, null, ex);
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
    
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws IOException
    {
        PrintWriter out=res.getWriter();
        String query=null;
        int result=0;
        if(req.getParameter("id")!=null)
        {
            String id=req.getParameter("id");
        try (Connection conn = DBClass.getConnection()) {
                query="DELETE FROM PRODUCTS WHERE ProductID=?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setString(1, id);
                result=pstmt.executeUpdate();
                if(result>0)
                {
                   out.println("Successfully deleted!!");
                }
                else
                {
                    res.setStatus(500); 
                }
                 } catch (SQLException ex) {
            Logger.getLogger(ControllerServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
            } else {
               out.println("Error: Not enough data to input. Please use a URL of the form /servlet?id=XX");
                   }
    } */

}
