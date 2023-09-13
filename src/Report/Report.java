/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Report;

import RazelsPackage.Database;
import java.sql.*;
import java.awt.Component;
import java.util.ArrayList;
import javax.swing.JOptionPane;
/**
 *
 * @author user
 */

public class Report implements Database {
    
    private Connection con;
    
    @Override
    public Connection createConnection(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String uname = "root";
            String password = "";
            String url = "jdbc:mysql://localhost:3306/razels_schema"; 
            con = DriverManager.getConnection(url,uname,password);
        } catch (Exception e) {
            System.out.println(e);
        }
        return con;
    }
    
    
    public ArrayList<Object> getLowQuanProducts(Component pComp){
        int minQuantity = 0;
        con = createConnection();
        ArrayList<Object> result = null;
        minQuantity = Integer.parseInt(JOptionPane.showInputDialog(pComp, "Enter low product quantity value: ","QUANTITY VALUE",JOptionPane.OK_CANCEL_OPTION));
        if (minQuantity != 0){
            try {
                String sqlSt = "SELECT p.p_ProductName,p.p_Quantity,s.s_SupplierName FROM products p INNER JOIN suppliers s ON p.p_SupplierId = s.s_SupplierId WHERE p.p_Quantity <= ?";
                PreparedStatement pst = con.prepareStatement(sqlSt);
                pst.setInt(1,minQuantity);
                ResultSet rs = pst.executeQuery();
                result = new ArrayList<>();
                while (rs.next()){
                    Object[] obj = {rs.getString("p_ProductName"),rs.getInt("p_Quantity"),rs.getString("s_SupplierName")};
                    result.add(obj);
                }
                pst.close();
            } catch (Exception e) {
                System.out.println(e);
            } finally {
                try {
                    con.close();
                } catch (Exception ex) {
                    System.out.println(ex);
                }
            }
        }
        return result;
    }
    
    
    
    //CREATE METHOD FOR CUSTOM SQL STATEMENTS USING SWITCH AND PREPARED STATEMENT MAYBE COMBO BOX AS WELL TO DISPLAY MANDATORY OPTIONS 
    //USE SHOWMESSAGE WITH COMBO BOX
}
