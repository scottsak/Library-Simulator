import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 *
 * @author Mimi Opkins with some tweaking from Dave Brown
 */
public class JDBCSampleSource {
    //  Database credentials
    static String DBNAME;
    //This is the specification for the printout that I'm doing:
    //each % denotes the start of a new field.
    //The - denotes left justification.
    //The number indicates how wide to make the field.
    //The "s" denotes that it's a string.  All of our output in this test are
    //strings, but that won't always be the case.
    static final String displayFormat="%-5s%-15s%-15s%-15s\n";
    static final String displayFormatOp1 = "%-25s%-20s%-20s%-15s\n";
    static final String displayFormatOp2 = "%-25s%-20s%-20s%-15s%-30s%-25s%-20s%-30s%-34s%-20s%-15s\n";
    static final String displayFormatOp3 = "%-30s%-24s%-20s%-15s\n";
    static final String displayFormatOp4 = "%-30s%-25s%-20s%-30s%-25s%-20s%-15s%-15s%-34s%-20s%-15s\n";
    static final String displayFormatOp5 = "%-34s%-20s%-15s%-30s%-25s%-20s%-30s%-25s%-20s%-15s%-15s\n";
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
                + "\n9) Enter a book title to remove"
                + "\n10) Exit");
        
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
            String sql;
            ResultSet rs;
            PreparedStatement pstmt = null;
            do
            {
                choice = menu();
                
                switch(choice){
                    case 1:
                        
                        sql = "SELECT GroupName, HeadWriter, YearFormed, Subject FROM WritingGroups";
                        rs = stmt.executeQuery(sql);

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
                        break;
                    
                    case 2:
              

                        System.out.print("Enter the WritingGroup Name: ");
                        String a = in.nextLine();

                        //shows what the user input
                        System.out.println("\nLooking for " + a + ", One moment please...");

                        stmt = conn.createStatement();
                        sql = "SELECT * FROM WritingGroups INNER JOIN Books USING (GroupName) INNER JOIN Publishers USING (PublisherName)"; 
                        rs = stmt.executeQuery(sql);        

                         System.out.printf(displayFormatOp2, "Group Name", "Head Writer", "Year Formed", "Subject", "Publisher Name", 
                                "Publisher Address", "Publisher Phone", "Publisher Email", "Book Title", "Year Published", "Num Of Pages");
                        while(rs.next()){
                             String GroupName = rs.getString("GroupName");
                             if(GroupName.equals(a)){
                                String HeadWriter = rs.getString("HeadWriter");
                                int YearFormed = rs.getInt("YearFormed");
                                String Subject = rs.getString("Subject");
                                String PublisherName = rs.getString("PublisherName");
                                String PublisherAddress = rs.getString("PublisherAddress");
                                String PublisherPhone = rs.getString("PublisherPhone");
                                String PublisherEmail = rs.getString("PublisherEmail");
                                String BookTitle = rs.getString("Booktitle");
                                int YearPublished = rs.getInt("Yearpublished");
                                int NumberPages = rs.getInt("NumberPages"); 

                                System.out.printf(displayFormatOp2, dispNull(GroupName), dispNull(HeadWriter), YearFormed, dispNull(Subject), 
                                    dispNull(PublisherName), dispNull(PublisherAddress), dispNull(PublisherPhone), dispNull(PublisherEmail), 
                                    dispNull(BookTitle), YearPublished, NumberPages);
                                }
                            }
                        System.out.println("");
                        break;
                     
                    case 3:
                        
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
                        break;

                
                    case 4:
                         

                        System.out.println("Enter a publisher's name ");
                        String pub = in.nextLine();

                        //shows what the user input
                        System.out.println("\nLooking for info on publisher " + pub + ", One moment please...");

                        stmt = conn.createStatement();
                        sql = "SELECT * FROM Publishers INNER JOIN Books USING (PublisherName) INNER JOIN WritingGroups USING (GroupName)";
                        rs = stmt.executeQuery(sql);

                        System.out.printf(displayFormatOp4, "Publisher Name", "Publisher Address", "Publisher Phone", "Publisher Email",
                                "Group Name", "Head Writer", "Year Formed", "Subject", "Book Title", "Year Published", "Num Of Pages");
                        while(rs.next()){
                            String PublisherName = rs.getString("PublisherName");
                            if(PublisherName.equals(pub)){
                                String HeadWriter = rs.getString("HeadWriter");
                                int YearFormed = rs.getInt("YearFormed");
                                String Subject = rs.getString("Subject");
                                String GroupName = rs.getString("GroupName");
                                String PublisherAddress = rs.getString("PublisherAddress");
                                String PublisherPhone = rs.getString("PublisherPhone");
                                String PublisherEmail = rs.getString("PublisherEmail");
                                String BookTitle = rs.getString("BookTitle");
                                int YearPublished = rs.getInt("YearPublished");
                                int NumberPages = rs.getInt("NumberPages");
                                System.out.printf(displayFormatOp4, dispNull(PublisherName), dispNull(PublisherAddress), dispNull(PublisherPhone),
                                        dispNull(PublisherEmail), dispNull(GroupName), dispNull(HeadWriter), YearFormed, dispNull(Subject),
                                        dispNull(BookTitle), YearPublished, NumberPages);
                            }
                        }
                        System.out.println();            
                        break;
                    
                    case 5:
                    
                        sql = "SELECT GroupName, BookTitle, PublisherName, YearPublished, NumberPages FROM Books";
                        rs = stmt.executeQuery(sql);

                        System.out.printf(displayFormat, "GroupName", "BookTitle", "PublisherName", "YearPublished", "NumberPages");
                        while(rs.next()) {
                        //Retrieve by column name
                            String GroupName = rs.getString("GroupName");
                            String BookTitle = rs.getString("BookTitle");
                            String PublisherName = rs.getString("PublisherName");
                            String YearPublished = rs.getString("YearPublished");
                            String NumberPages = rs.getString("NumberPages");

                            //Display values
                            System.out.printf(displayFormat,
                                dispNull(GroupName), dispNull(BookTitle), dispNull(PublisherName), dispNull(YearPublished), dispNull(NumberPages));
                        }
                        rs.close();
                        break;
                                                           
                
                    case 6:
                  
                    
                        System.out.println("Enter a book's name: ");
                        String book = in.nextLine();

                        //Shows the users input
                        System.out.println("\nLooking for the info book " + book + ", One moment please...");

                        stmt = conn.createStatement();
                        sql = "SELECT * FROM BOOKS INNER JOIN WritingGroups USING (GroupName) INNER JOIN Publishers USING (PublisherName)";
                        rs = stmt.executeQuery(sql);

                        System.out.printf(displayFormatOp5, "Book Title", "Year Published", "Num Of Pages", "Publisher Name", "Publisher Address",
                                "Publisher Phone", "Publisher Email", "Group Name", "Head Writer", "Year Formed", "Subject");
                        while(rs.next()){
                            String BookTitle = rs.getString("BookTitle");
                            if(BookTitle.equals(book)){
                                String HeadWriter = rs.getString("HeadWriter");
                                int YearFormed = rs.getInt("YearFormed");
                                String Subject = rs.getString("Subject");
                                String GroupName = rs.getString("GroupName");
                                String PublisherName = rs.getString("PublisherName");
                                String PublisherAddress = rs.getString("PublisherAddress");
                                String PublisherPhone = rs.getString("PublisherPhone");
                                String PublisherEmail = rs.getString("PublisherEmail");
                                int YearPublished = rs.getInt("YearPublished");
                                int NumberPages = rs.getInt("NumberPages");
                                System.out.printf(displayFormatOp5, dispNull(BookTitle), dispNull(HeadWriter), YearPublished, NumberPages, dispNull(PublisherName), dispNull(PublisherAddress),
                                        dispNull(PublisherPhone), dispNull(PublisherEmail), dispNull(GroupName), dispNull(HeadWriter), YearFormed, dispNull(Subject));     

                            }
                        }
                        System.out.println();
                        break;
                    
                
                    case 7:
                    
                       
                        

                        //get user input
                        System.out.println("Enter group name");
                        String groupname = in.nextLine();
                        System.out.println("Enter Book title");
                        String booktitle = in.nextLine();
                        System.out.println("Enter Publisher's Name");
                        String pname = in.nextLine();
                        System.out.println("Enter Year Published");
                        String ypublished = in.nextLine();
                        System.out.println("Enter the number of pages");
                        String numpages = in.nextLine();

                        //creating the sql statement
                        sql = "insert into books(groupname,booktitle,publishername, yearpublished,numberpages) values(?,?,?,?,?)";
                        pstmt = conn.prepareStatement(sql);
                        pstmt.setString(1, groupname);
                        pstmt.setString(2, booktitle);
                        pstmt.setString(3, pname);
                        pstmt.setString(4, ypublished);
                        pstmt.setString(5, numpages);
                        pstmt.execute();
                        break;
                
                    case 8:

                        //execute prepared input
                        

                        //get user input
                        System.out.println("Enter Publisher Name");
                        pname = in.nextLine();
                        System.out.println("Enter Publisher Address");
                        String paddress = in.nextLine();
                        System.out.println("Enter Publisher's Phone");
                        String pphone = in.nextLine();
                        System.out.println("Enter Publisher Email");
                        String pemail = in.nextLine();

                        //creating the sql statement
                        sql = "insert into publishers(PublisherName, PublisherAddress,PublisherPhone,PublisherEmail) values(?,?,?,?)";
                        pstmt = conn.prepareStatement(sql);
                        pstmt.setString(1, pname);
                        pstmt.setString(2, paddress);
                        pstmt.setString(3,pphone);
                        pstmt.setString(4, pemail);
                        pstmt.execute();
                        //updating old publisher
                        System.out.println("Input the publisher being bought out by " + pname);
                        String oldpname = in.nextLine();
                        sql = "update books set publishername = ? where publishername = ?";
                        pstmt = conn.prepareStatement(sql);
                        pstmt.setString(1,pname);
                        pstmt.setString(2,oldpname);
                        pstmt.execute();

                        //delete old publisher
                        sql = "delete from publishers where publishername = ?";
                        pstmt = conn.prepareStatement(sql);
                        pstmt.setString(1,oldpname);
                        pstmt.execute();
                        System.out.println("All books published by "+ oldpname +" is now published by "+ pname);
                        break;
                
                    case 9: 
                        System.out.println("Enter Group Name");
                        String gname = in.nextLine();
                        System.out.println("Enter Book Title");
                        String btitle = in.nextLine();
                        pstmt = null;
                        sql = "Delete from books where GroupName = ? and booktitle = ?";
                        pstmt = conn.prepareStatement(sql);
                        pstmt.setString(1,gname);
                        pstmt.setString(2, btitle);
                        pstmt.execute();
                        System.out.println();
                        System.out.println("The book " + btitle + " created by " + gname + " has been removed.");
                        break;
                
                    case 10:
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
