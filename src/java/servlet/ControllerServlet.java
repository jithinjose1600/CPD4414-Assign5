/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package servlet;

import db.DBClass;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author c0648991
 */
@WebServlet("/servlet")
public class ControllerServlet extends HttpServlet{
    
    @Override
    protected  void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException
    {
        res.setHeader("Content-Type", "text/plain-text");
        
       StringBuilder sb = new StringBuilder();
       String query=null;
        PrintWriter out = res.getWriter();
        try (Connection conn = DBClass.getConnection()) {
            
            PreparedStatement pstmt=null;
            if (!req.getParameterNames().hasMoreElements()) {
                query="SELECT * FROM PRODUCTS";
                pstmt = conn.prepareStatement(query);
            } else {
                int id = Integer.parseInt(req.getParameter("id"));
                query="SELECT * FROM PRODUCTS WHERE ProductId=?";
                pstmt = conn.prepareStatement(query);
                pstmt.setString(1, String.valueOf(id));
            }
            
            ResultSet rs = pstmt.executeQuery();
            if(!rs.wasNull())
            {
              sb.append("[");
            while (rs.next()) {
                sb.append(String.format("{ \"productId\" : "+ rs.getInt("ProductID")+", \"name\" : \""+rs.getString("Name")+"\", \"description\" : \""+rs.getString("Description")+"\", \"quantity\" : "+rs.getInt("Quantity")+" },"));   
            }
            sb.append("]");
            out.println(sb.toString());
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(ControllerServlet.class.getName()).log(Level.SEVERE, null, ex);
        }  
    }
    
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
 

}
