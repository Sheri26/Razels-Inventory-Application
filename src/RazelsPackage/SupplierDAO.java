/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package RazelsPackage;


import java.sql.*;
import java.util.*;
import java.awt.Component;



/**
 *
 * @author user
 */
public class SupplierDAO implements Database {
    
    private int suppId;
    private String suppName;
    Connection con;
    
    public SupplierDAO(){
        
    }

    public SupplierDAO(int suppId, String suppName) {
        this.suppId = suppId;
        this.suppName = suppName;
    }

    public int getSuppId() {
        return suppId;
    }

    public void setSuppId(int suppId) {
        this.suppId = suppId;
    }

    public String getSuppName() {
        return suppName;
    }

    public void setSuppName(String suppName) {
        this.suppName = suppName;
    }

    
    
    @Override
    public Connection createConnection() {
        String url = "jdbc:mysql://localhost:3306/razels_schema";
        String dbUsername = "root";
        String dbPassword = "";
        
        try {
            Class.forName( "com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(url, dbUsername, dbPassword);
            return con;
            
        } catch (Exception ex) {
            System.out.println(ex);
        }
        
        return null;
    }
    
    public void registerSupplier(String suppName){
        
        try {
           con = createConnection();
           PreparedStatement st = con.prepareStatement("INSERT INTO suppliers(s_SupplierName) VALUES (?)");
           st.setString(1,suppName);
           st.executeUpdate();
           st.close();
           con.close();
        } catch (Exception ex) {
            System.out.println(ex);
       } 
    }
     
     
  
    public int removeSupplier(int suppId,Component pComp){
        
        int results = 0 ;
        con = createConnection();
        try {
            PreparedStatement st = con.prepareStatement("DELETE FROM suppliers WHERE s_SupplierId = ?");
            st.setInt(1,suppId);
            results = st.executeUpdate(); 
            st.close();
            con.close();
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return results;
        
    }
    
    
    
    
    public void updateSupplier(int suppId,String newSuppName){
        try {
            con = createConnection();
            PreparedStatement st = con.prepareStatement("UPDATE suppliers SET s_SupplierName = ? WHERE s_SupplierId = ? ");
            st.setString(1,newSuppName);
            st.setInt(2,suppId);
            int results = st.executeUpdate();
            st.close();
            con.close();
            
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
    
     
     
     

    
    public HashMap<String,Integer> searchSupplier(String suppName){
        con = createConnection();
        HashMap<String,Integer> suppDetails = new HashMap<>(); 
        
        try {
            PreparedStatement st = con.prepareStatement("SELECT * FROM suppliers WHERE s_SupplierName LIKE CONCAT(?,'%')");
            st.setString(1,suppName);
            ResultSet rs = st.executeQuery();
            
            
            while(rs.next()){
                this.setSuppId(rs.getInt("s_SupplierId"));
                this.setSuppName(rs.getString("s_SupplierName"));
                suppDetails.put(this.getSuppName(),this.getSuppId());
            }
            st.close();
            con.close();
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return suppDetails;
    }
    
    
    public ArrayList<String> getSupplierNames(){
        ArrayList<String> suppliers = new ArrayList<String>();
       
        try {
            con = createConnection();
            CallableStatement cst = con.prepareCall("{Call get_suppliers()}");
            Boolean hasResult = cst.execute();
            if (hasResult == true){
                ResultSet rs = cst.executeQuery();
                while (rs.next()){
                    suppliers.add((rs.getString("s_SupplierName")));
                }
               
            }

        } catch (Exception ex) {
            System.out.println(ex);
        }
        return suppliers;
    }
    
}
