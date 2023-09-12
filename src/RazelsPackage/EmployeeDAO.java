/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package RazelsPackage;

import java.awt.Component;
import java.sql.*;
import javax.swing.JOptionPane;




/**
 *
 * @author user
 */
public class EmployeeDAO implements Database{
    
    private String emIDNumber,emName, emSurname,emUsername;
    private int emRole;
    Connection con;
    
    public EmployeeDAO(){}

    public EmployeeDAO(String emIDNumber, String emName, String emSurname, String emUsername, int emRole) {
        this.emIDNumber = emIDNumber;
        this.emName = emName;
        this.emSurname = emSurname;
        this.emUsername = emUsername;
        this.emRole = emRole;
    }

    
    
    public String getEmIDNumber() {
        return emIDNumber;
    }

    public void setEmIDNumber(String emIDNumber) {
        this.emIDNumber = emIDNumber;
    }

    public String getEmName() {
        return emName;
    }

    public void setEmName(String emName) {
        this.emName = emName;
    }

    public String getEmSurname() {
        return emSurname;
    }

    public void setEmSurname(String emSurname) {
        this.emSurname = emSurname;
    }

    public int getEmRole() {
        return emRole;
    }

    public void setEmRole(int emRole) {
        this.emRole = emRole;
    }

    public String getEmUsername() {
        return emUsername;
    }

    public void setEmUsername(String emUsername) {
        this.emUsername = emUsername;
    }
    
    
    
    
    
    @Override
    public Connection createConnection(){
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
    
        
    
   
    
    public void registerEmployee(Component pComp){
        
        String[] userData = searchEmployee(emIDNumber);
        StringBuilder messg = new StringBuilder();
        
        if(userData[0] != null){
            messg.append("ID Number Already Registered As :\n");
            messg.append("\nID Number : ");
            messg.append(userData[0]);
            messg.append("\n");
            messg.append("Name : ");
            messg.append(userData[1]);
            messg.append("\n");
            messg.append("Surname : ");
            messg.append(userData[2]);
            messg.append("\n");
            messg.append("Username : ");
            messg.append(userData[3]);
            messg.append("\n");
            messg.trimToSize();
            messg.toString();
            JOptionPane.showMessageDialog(pComp,messg,"INFORMATION",JOptionPane.INFORMATION_MESSAGE);
        }
        
        else{
            try {
                con = createConnection();
                PreparedStatement st = con.prepareStatement("INSERT INTO employees VALUES (?,?,?,?,?)");
                st.setString(1,emIDNumber);
                st.setString(2,emName);
                st.setString(3,emSurname);
                st.setInt(4,emRole);
                st.setString(5,emUsername);
                st.executeUpdate();
                st.close();
                con.close();
                userData = searchEmployee(emIDNumber);
                String results = "Username : "+userData[3]+"\n"+"Password : "+userData[0];
                JOptionPane.showMessageDialog(pComp,results,"NEW CREDENTIALS",JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                System.out.println(ex);
            } 
        }
        
    }
    
    public void removeEmployee(String idNumber, Component pComp){
        
        String rst[] = searchEmployee(idNumber);
        if (rst[0] == null){
            JOptionPane.showMessageDialog(pComp,"Invalid Details","ERROR",JOptionPane.ERROR_MESSAGE);
        }
        else if (rst[0].equals(idNumber)){
            try {
                con=createConnection();
            
                PreparedStatement st = con.prepareStatement("DELETE FROM employees WHERE e_IdNumber = ?");
                st.setString(1,idNumber);
                String delUser = rst[3];
                StringBuilder confMessage = new StringBuilder();
                confMessage.append("Are you sure you want to remove:\n ");
                confMessage.append("Username:");
                confMessage.append("\t");
                confMessage.append(delUser);
                int response = JOptionPane.showConfirmDialog(pComp,confMessage,"CONFIRM USER REMOVAL",JOptionPane.YES_NO_OPTION);
                if (response == JOptionPane.YES_OPTION){
                    int results = st.executeUpdate();
                    JOptionPane.showMessageDialog(pComp,results + " user removed", "REMOVE ACCESS", JOptionPane.INFORMATION_MESSAGE);
                }
                
                st.close();
                con.close();
            } catch (Exception ex) {
                System.out.println(ex);
            }
            
        } else{
            JOptionPane.showMessageDialog(pComp,"Invalid Details","ERROR",JOptionPane.ERROR_MESSAGE);
        }
    }
    
  
    
    public String[] searchEmployee(String idNumber){ 
        String eIdNumber = null,eName = null,eSurname = null,eUsername = null;
        String[] row = null;                                           
        try {
            con=createConnection();
            PreparedStatement st = con.prepareStatement("SELECT * FROM employees WHERE e_IdNumber=?");
            st.setString(1,idNumber);
            ResultSet rs = st.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            row = new String[rsmd.getColumnCount()];
            
            while (rs.next()){
                eIdNumber = rs.getString("e_IdNumber");
                eName = rs.getString("e_Name");
                eSurname = rs.getString("e_Surname");
                eUsername = rs.getString("e_Username");
            }
           
            st.close();
            con.close();
           
        } catch (Exception ex) {
            System.out.println(ex);
        }
 
        row[0] = eIdNumber;

        row[1] = eName;

        row[2] = eSurname;

        row[3] = eUsername;
        return row;
    }
    
    //Use auth function on sign in for faster sign-in
    //MySql indexes on IdNumber and Username Columns as well to speed up queries 
    
     public Boolean authEmployee(String idNumber,String username){
        Boolean bAuth = null; 
        String rslt[] = searchEmployee(idNumber);
        String recIdNumber = rslt[0];
        String recUsername = rslt[3];
        
            
        if ((idNumber.equals(recIdNumber)) && (username.equals(recUsername))){
            bAuth = true;
        }
        else{
            bAuth = false;
        }
        return bAuth;
    }
    
    public int updateEmployee(String nName,String nSurname,String nUsername, String idNumber){
        con = createConnection();
        int result = 0;
        try {
            PreparedStatement st = con.prepareStatement("UPDATE employees SET e_Name = ?,e_Surname = ?,e_Username = ? WHERE e_IdNumber = ?");
            st.setString(1,nName);
            st.setString(2,nSurname);
            st.setString(3,nUsername);
            st.setString(4,idNumber);
            result = st.executeUpdate();
            st.close();
            con.close();
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return result;
    }
    
}
