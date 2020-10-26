import java.sql.*;
import java.util.Scanner;

/**
 *
 * @author Mimi Opkins with some tweaking from Dave Brown
 */
public class JDBCSampleSource {
    //  Database credentials
    static String USER;
    static String PASS;
    static String DBNAME;
    //This is the specification for the printout that I'm doing:
    //each % denotes the start of a new field.
    //The - denotes left justification.
    //The number indicates how wide to make the field.
    //The "s" denotes that it's a string.  All of our output in this test are
    //strings, but that won't always be the case.
    static final String displayFormat="%-5s%-15s%-15s%-15s\n";
// JDBC driver name and database URL
    static final String JDBC_DRIVER = "org.apache.derby.jdbc.ClientDriver";
    static String DB_URL = "jdbc:derby://localhost:1527/";
//            + "testdb;user=";
/**
 * Takes the input string and outputs "N/A" if the string is empty or null.
 * @param input The string to be mapped.
 * @return  Either the input string or "N/A" as appropriate.
 */
    public static String dispNull (String input) {
        //because of short circuiting, if it's null, it never checks the length.
        if (input == null || input.length() == 0)
            return "N/A";
        else
            return input;
    }
    
    public static int menu()
    {
        System.out.println("\nWelcome to library database!"
                + "\n1) List Writing Groups\n2) Enter specific writing group name"
                + "\n3) List all publishers \n4) Enter specific publisher name"
                + "\n5) List all book titles\n6) Enter a specific book"
                + "\n7) Enter a new book title\n8) Enter new publisher"
                + "\n9) Enter a book title to remove");
        
        Scanner in = new Scanner(System.in);
         int choice = in.nextInt();
         
         return choice;
       
    }

    public static void main(String[] args) {
        //Prompt the user for the database name, and the credentials.
        //If your database has no credentials, you can update this code to
        //remove that from the connection string.
        Scanner in = new Scanner(System.in);
        System.out.print("Name of the database (not the user account): ");
        DBNAME = in.nextLine();
        
        //Constructing the database URL connection string
        DB_URL = DB_URL + DBNAME;
        Connection conn = null; //initialize the connection
        Statement stmt = null;  //initialize the statement that we're using
        try {
            //STEP 2: Register JDBC driver
            Class.forName("org.apache.derby.jdbc.ClientDriver");

            //STEP 3: Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL);

            //STEP 4: Execute a query
            System.out.println("Creating statement...");
            stmt = conn.createStatement();
            
        
            int choice;
            do
            {
                choice = menu();
                
                if(choice == 1){
                    String sql;
                    sql = "SELECT GroupName, HeadWriter, YearFormed, Subject FROM WritingGroups";
                    ResultSet rs = stmt.executeQuery(sql);

                    //STEP 5: Extract data from result set
                    System.out.printf(displayFormat, "GroupName", "HeadWriter", "YearFormed", "Subject");
                    while (rs.next()) {
                        //Retrieve by column name
                        String GroupName = rs.getString("GroupName");
                        String HeadWriter = rs.getString("HeadWriter");
                        String YearFormed = rs.getString("YearFormed");
                        String Subject = rs.getString("Subject");

                        //Display values
                        System.out.printf(displayFormat,
                            dispNull(GroupName), dispNull(HeadWriter), dispNull(YearFormed), dispNull(Subject));
                    }
                    //STEP 6: Clean-up environment 
                    rs.close();
                    
                } 
                else if (choice == 3) {
                    String sql;
                    sql = "SELECT PublisherName, PublisherAddress, PublisherPhone, PublisherEmail FROM Publishers";
                    ResultSet rp = stmt.executeQuery(sql);
                    
                    System.out.printf(displayFormat, "PublisherName", "PublisherAddress", "PublisherPhone", "PublisherEmail");
                    while(rp.next()) {
                    //Retrieve by column name
                        String PublisherName = rp.getString("PublisherName");
                        String PublisherAddress = rp.getString("PublisherAddress");
                        String PublisherPhone = rp.getString("PublisherPhone");
                        String PublisherEmail = rp.getString("PublisherEmail");                      
                
                        //Display values
                        System.out.printf(displayFormat,
                            dispNull(PublisherName), dispNull(PublisherAddress), dispNull(PublisherPhone), dispNull(PublisherEmail));
                    }
                    rp.close();
                    
                }
                else if (choice == 5) {
                    String sql;
                    sql = "SELECT GroupName, BookTitle, PublisherName, YearPublished, NumberPages FROM Books";
                    ResultSet rb = stmt.executeQuery(sql);
                    
                    System.out.printf(displayFormat, "GroupName", "BookTitle", "PublisherName", "YearPublished", "NumberPages");
                    while(rb.next()) {
                    //Retrieve by column name
                        String GroupName = rb.getString("GroupName");
                        String BookTitle = rb.getString("BookTitle");
                        String PublisherName = rb.getString("PublisherName");
                        String YearPublished = rb.getString("YearPublished");
                        String NumberPages = rb.getString("NumberPages");
                
                        //Display values
                        System.out.printf(displayFormat,
                            dispNull(GroupName), dispNull(BookTitle), dispNull(PublisherName), dispNull(YearPublished), dispNull(NumberPages));
                    }
                    rb.close();
                                                           
                }
                else if(choice == 10)
                {
               
                    stmt.close();
                    conn.close();
                    System.out.print("Goodbye!");
                    return;
                } 
            }
           while(choice != 10);
            
        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            //finally block used to close resources
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException se2) {
            }// nothing we can do
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }//end finally try
        }//end try
        System.out.println("Goodbye!");
    }//end main
}//end FirstExample}