/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package RazelsPackage;

import java.sql.*;
import javax.swing.*;
import java.awt.Component;






/**
 *
 * @author user
 */
public class ProductDAO implements Database{ 
        
    private String prodName;
    private int prodQuantity;
    Connection con;
    
    public ProductDAO(){
    
    }

    public ProductDAO(String prodName, int prodQuantity) {
        this.prodName = prodName;
        this.prodQuantity = prodQuantity;
    }

    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    public int getProdQuantity() {
        return prodQuantity;
    }

    public void setProdQuantity(int prodQuantity) {
        this.prodQuantity = prodQuantity;
    }

    @Override
    public Connection createConnection(){
        String url="jdbc:mysql://localhost:3306/razels_schema";
        String dbUsername="root";
        String dbPassword="";
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con  = DriverManager.getConnection(url,dbUsername,dbPassword);
            return con;
        } 
        catch (Exception ex) {
            System.out.println(ex);
        }
        
        return null;
    }
    
    //method to retreive data from stored procedure in mySql using JDBC
    public ResultSet getAllProducts(){
        ResultSet rs = null;
        CallableStatement cs = null;
        con = createConnection();
        
        try {
            cs = con.prepareCall("{CALL display_Products()}");
            boolean hasRst = cs.execute();
            if (hasRst){
                rs = cs.executeQuery(); 
            }
            
        } 
        catch (Exception ex) {
            System.out.println(ex);
        } 
       
        return rs;
    }
    
    //method to retrieve product and supplier names only from database using mySql Query and JDBC
    public ResultSet getAllProdSupp(){
        con = createConnection();
        String query;
        ResultSet rs = null;
        Statement st = null;
        
        try {
            query = "SELECT p.p_ProductName,s.s_SupplierName FROM products p INNER JOIN suppliers s ON p.p_SupplierId = s.s_SupplierId";
            st = con.createStatement();
            rs = st.executeQuery(query);     
        } 
        catch (Exception e) {
            System.out.println(e);
        } 
        
        return rs;
    }
   
    
    //method that adds new product and corresponding supplier to database using mysql  
    public void addProduct(String suppName, String prodName, int prodQuan, Component pComp){
        con = createConnection();
        String q1,q2;
        int suppId=0;
        try {
            q1 = "SELECT s_SupplierId FROM suppliers WHERE s_SupplierName = ?";
            PreparedStatement pst = con.prepareStatement(q1);
            pst.setString(1, suppName);
            ResultSet r1 = pst.executeQuery();
            while (r1.next()){
                suppId = r1.getInt("s_SupplierId");
            }
            q2 = "INSERT INTO products(p_ProductName,p_Quantity,p_SupplierId) VALUES (?,?,?)";
            PreparedStatement st = con.prepareStatement(q2);
            st.setString(1,prodName);
            st.setInt(2,prodQuan);
            st.setInt(3, suppId);
            int r2 = st.executeUpdate();
            JOptionPane.showMessageDialog(pComp,r2+" product added","SUCCESS",JOptionPane.INFORMATION_MESSAGE);
            pst.close();
            st.close();
            con.close();
        } 
        catch (Exception e) {
            System.out.println(e);
        }
    }
    
    //Take in component parameter and use .execute to see if product exist if not display error using component and JOptionPane
    public void removeProduct(String pName,String supp,Component pComp){
        con = createConnection();
        int prodId = 0,prodQuantity = 0;
        String prodName = null,suppName = null;
        
        try {
            String query = "SELECT p.p_ProductId,p.p_ProductName,p.p_Quantity,s.s_SupplierName FROM products p INNER JOIN suppliers s ON p.p_SupplierId = s.s_SupplierId WHERE p.p_ProductName = ? AND s.s_SupplierName = ?";
            PreparedStatement st = con.prepareStatement(query);
            st.setString(1,pName);
            st.setString(2,supp);
            if (st.execute()){
                ResultSet rs = st.executeQuery();
                while (rs.next()){
                    prodId = rs.getInt("p_ProductId");
                    prodName = rs.getString("p_ProductName");
                    prodQuantity = rs.getInt("p_Quantity");
                    suppName = rs.getString("s_SupplierName");
                }
                StringBuilder delMessage = new StringBuilder();
                delMessage.append("Are you sure you want to remove the following product?\n");
                delMessage.append("======================================================\n");
                delMessage.append(" \n");
                delMessage.append("Product : ");
                delMessage.append(prodName);
                delMessage.append("\n");
                delMessage.append("Quantity Remaining : ");
                delMessage.append(prodQuantity);
                delMessage.append("\n");
                delMessage.append("Supplier : ");
                delMessage.append(suppName);
                delMessage.append("\n");
                
                
                int choice = JOptionPane.showConfirmDialog(pComp,delMessage, "PRODUCT REMOVAL",JOptionPane.YES_NO_OPTION);
                if(choice == JOptionPane.YES_OPTION){
                    String query2 = "DELETE FROM products WHERE p_ProductId = ?";
                    st = con.prepareStatement(query2);
                    st.setInt(1,prodId);
                    int result = st.executeUpdate();
                    JOptionPane.showMessageDialog(pComp, result+" product deleted", "SUCCESSFUL REMOVAL" , JOptionPane.INFORMATION_MESSAGE);
                }
            }
            else{
                JOptionPane.showMessageDialog(pComp,"Product does not exist!","INFORMATION",JOptionPane.ERROR_MESSAGE);
            }
        } 
        catch (Exception e) {
            System.out.println(e);
        }
    }
    
    public void searchProduct(int prodId){
        con = createConnection();
        String query = "SELECT * FROM products WHERE p_ProductId = ?";
        try {
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1,prodId);
            ResultSet  rs = pst.executeQuery();
            while(rs.next()){
                this.setProdName(rs.getString("p_ProductName"));
                this.setProdQuantity(rs.getInt("p_Quantity"));
            }
        } 
        catch (Exception ex) {
            System.out.println(ex);
        }
    
    }
    
    
    public void updateProdQuantity(String prodName,String suppName,Component pComp){
        con = createConnection();
        String query = "SELECT p.p_ProductId FROM products p INNER JOIN suppliers s ON p.p_SupplierId = s.s_SupplierId WHERE p.p_ProductName = ? AND s.s_SupplierName = ?";
        int prodId = 0;
        try {
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1,prodName);
            pst.setString(2,suppName);
            ResultSet rs = pst.executeQuery();
            while (rs.next()){
                prodId = rs.getInt("p_ProductId");
            }
            searchProduct(prodId);
            String prodQuanAdd = JOptionPane.showInputDialog(pComp,"Enter number of received units:","UPDATE PRODUCT QUANTITY:",JOptionPane.OK_CANCEL_OPTION);
            if (prodQuanAdd == null){
                //DO NOTHING
            }
            else if ((prodQuanAdd != null) || (prodQuanAdd.isEmpty() == false)){
                int newQuantity = this.getProdQuantity() + Integer.parseInt(prodQuanAdd);
                String query2 = "UPDATE products SET p_Quantity = ? WHERE p_ProductId = ?";
                PreparedStatement pst2 = con.prepareStatement(query2);
                pst2.setInt(1, newQuantity);
                pst2.setInt(2,prodId);
                int result = pst2.executeUpdate();
                JOptionPane.showMessageDialog(pComp , result+" product/s updated" , "SUCCESSFUL UPDATE",JOptionPane.INFORMATION_MESSAGE);
            }
        } 
        catch (Exception ex) {
            System.out.println(ex);
        }
    
    }
}