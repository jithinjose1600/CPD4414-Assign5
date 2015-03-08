/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package servlet;

import db.DBClass;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;
import javax.ws.rs.GET;
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
    @Produces("application/json")
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
                } catch (SQLException ex) {
            Logger.getLogger(ControllerServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        return out.toString();
    }
   /* }
     @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Set<String> keySet = request.getParameterMap().keySet(); 
        PrintWriter out = response.getWriter();
        String query=null;
        int res=0;
        if (keySet.contains("name") && keySet.contains("description") && keySet.contains("quantity")) {               
                String name = request.getParameter("name");
                String description= request.getParameter("description");
                String quantity=request.getParameter("quantity");
                try (Connection conn = DBClass.getConnection()) {
                query="INSERT INTO PRODUCTS (Name, Description, Quantity) VALUES (?, ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setString(1, name);
                pstmt.setString(2, description);
                pstmt.setString(3, quantity);
                res=pstmt.executeUpdate();
                if(res>0)
                {
                   out.println("Successfully inserted!!");
                }
                else
                {
                    response.setStatus(500); 
                }
                 } catch (SQLException ex) {
            Logger.getLogger(ControllerServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
            } else {
               out.println("Error: Not enough data to input. Please use a URL of the form /servlet?name=XXX&description=XXX&quantity=XX");
                   }
            
    }
    
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res) throws IOException
    {
        PrintWriter out=res.getWriter();
        Set<String> keySet=req.getParameterMap().keySet();
        String query=null;
        int result=0;
        if(keySet.contains("id") && keySet.contains("name") && keySet.contains("description") && keySet.contains("quantity"))
        {
            String id=req.getParameter("id");
            String name=req.getParameter("name");
            String description=req.getParameter("description");
            String quantity=req.getParameter("quantity");
        try (Connection conn = DBClass.getConnection()) {
                query="UPDATE PRODUCTS SET Name=?, Description=?, Quantity=? WHERE ProductID=?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setString(1, name);
                pstmt.setString(2, description);
                pstmt.setString(3, quantity);
                pstmt.setString(4, id);
                result=pstmt.executeUpdate();
                if(result>0)
                {
                   out.println("Successfully updated!!");
                }
                else
                {
                    res.setStatus(500); 
                }
                 } catch (SQLException ex) {
            Logger.getLogger(ControllerServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
            } else {
               out.println("Error: Not enough data to input. Please use a URL of the form /servlet?id=XX&name=XXX&description=XXX&quantity=XX");
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
