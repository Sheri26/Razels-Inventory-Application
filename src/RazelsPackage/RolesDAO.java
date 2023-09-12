/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package RazelsPackage;


import java.sql.*;
import java.util.*;

/**
 *
 * @author user
 */
public class RolesDAO implements Database {
    
    Connection con;
   
    
    
    
    
    
    @Override
    public Connection createConnection() {
        String url="jdbc:mysql://localhost:3306/razels_schema";
        String dbUsername="root";
        String dbPassword="";
        
        try {
            Class.forName( "com.mysql.cj.jdbc.Driver");
            con=DriverManager.getConnection(url, dbUsername, dbPassword);
            return con;
            
        } catch (Exception ex) {
            System.out.println(ex);
        }
        
        return null;
    }
    
    
    
    
     public Collection<String> getRoleNames(){
        Collection<String> roles = new ArrayList<String>();
       
        try {
            con = createConnection();
            CallableStatement cst = con.prepareCall("{Call get_roles()}");
            Boolean hasResult = cst.execute();
            if (hasResult){
                ResultSet rs = cst.executeQuery();
                while (rs.next()){
                    roles.add(rs.getString("r_RoleName"));
                }
            }
            else
                return null;
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return roles;
    }
}
