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
    
   public static int inputValidation(Scanner input){
        int user_input; 
        do {
            System.out.println("Please enter a number from the menu: ");
            while (!input.hasNextInt()) {
                System.out.println("\nPlease enter an integer number: ");
                input.next();
            }
            user_input = input.nextInt();
        } while (user_input < 1 || user_input > 10);

        input.nextLine();
        return user_input;
    };
   
   
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
        int choice=1001;
        while(choice >=11 || choice <=0){
            try{
                System.out.println("Input an Integer between 1-10");
                choice = in.nextInt();
                if(choice <=0 || choice >=11){
                    throw new IllegalAccessException();
                }
            }
            catch(InputMismatchException m){
                System.out.println("Not an Integer \nPlease Try Again");
                in.next();
            }
            catch(IllegalAccessException i){
                System.out.println("Not in Range \nPlease Try Again");
                
            }
        }
        //int choice = in.nextInt();
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
                    //list writing groups
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
                    
                    //Show writing group information
                    case 2: 
                        //case 2: list all data for a group specified by the user
                        System.out.print("Enter the WritingGroup Name: ");
                        String grp = in.nextLine();
                        
                        sql = "select groupname from writinggroups where groupname = ?";
                        pstmt = conn.prepareStatement(sql);
                        pstmt.setString(1,grp);
                        rs=pstmt.executeQuery();
                        
                        if(false==rs.next()){
                            System.out.println("Group Name Does Not Exist");
                            break;
                        }

                        //shows what the user input
                        System.out.println("\nLooking for " + grp + ", One moment please...");
                        System.out.println();
                        sql = "SELECT * FROM WritingGroups NATURAL JOIN Books NATURAL JOIN Publishers where GroupName = ?";    
                        pstmt = conn.prepareStatement(sql);
                        pstmt.setString(1, grp);
                        rs = pstmt.executeQuery();                        
                        System.out.printf(displayFormatOp5, "Publisher Name", "Group Name", "Head Writer", "Year Formed", "Subject", 
                            "Book Title", "Year Published", "Num of Pages", "Publisher Address", "Publisher Phone", "Publisher Email");
                        while(rs.next()){
                            grp = rs.getString(1);                            
                            String HeadWriter = rs.getString(2);
                            String YearFormed = rs.getString(3);
                            String Subject = rs.getString(4);
                            String PublisherName = rs.getString(5);
                            String PublisherAddress = rs.getString(6);
                            String PublisherPhone = rs.getString(7);
                            String PublisherEmail = rs.getString(8);
                            String BookTitle = rs.getString(9);
                            String YearPublished = rs.getString(10);
                            String NumberPages = rs.getString(11);                          

                            System.out.printf(displayFormatOp5, dispNull(grp), dispNull(HeadWriter), dispNull(YearFormed), dispNull(Subject), 
                                dispNull(PublisherName), dispNull(PublisherAddress), dispNull(PublisherPhone), dispNull(PublisherEmail), 
                                dispNull(BookTitle), dispNull(YearPublished), dispNull(NumberPages));                               
                            }
                        System.out.println("");
                        rs.close();
                        break;  
                        
                    //List publisher Names
                    case 3:
                        
                        sql = "SELECT PublisherName, PublisherAddress, PublisherPhone, PublisherEmail FROM Publishers";
                        rs = stmt.executeQuery(sql);

                        System.out.printf(displayFormat, "PublisherName", "PublisherAddress", "PublisherPhone", "PublisherEmail");
                        while(rs.next()) {
                        //Retrieve by column name
                            String PublisherName = rs.getString("PublisherName");
                            String PublisherAddress = rs.getString("PublisherAddress");
                            String PublisherPhone = rs.getString("PublisherPhone");
                            String PublisherEmail = rs.getString("PublisherEmail");                      

                            //Display values
                            System.out.printf(displayFormat,
                                dispNull(PublisherName), dispNull(PublisherAddress), dispNull(PublisherPhone), dispNull(PublisherEmail));
                        }
                        rs.close();
                        break;

                    //Show publisher Information
                    case 4:
                        //case 4: list all the data for a publisher specified by the user
                        
                        System.out.println("Enter a publisher's name ");
                        String pub = in.nextLine();
                        

                        //shows what the user input
                        System.out.println("Looking for info on publisher " + pub + ".\nOne moment please...");   
                        
                        sql = "select publishername from publishers where publishername = ?";
                        pstmt = conn.prepareStatement(sql);
                        pstmt.setString(1,pub);
                        rs=pstmt.executeQuery();
                        
                        if(false==rs.next()){
                            System.out.println("Publisher "+pub+" does Not Exist");
                            break;
                        }
                        
                        sql = "SELECT * FROM Publishers NATURAL JOIN Books Natural Join WritingGroups where PublisherName = ?";                        
                        pstmt = conn.prepareStatement(sql);
                        pstmt.setString(1, pub);
                        
                        rs = pstmt.executeQuery();                    

                        while(rs.next()){
                            pub = rs.getString(1);                            
                            String HeadWriter = rs.getString(2);
                            String YearFormed = rs.getString(3);
                            String Subject = rs.getString(4);
                            String GroupName = rs.getString(5);
                            String PublisherAddress = rs.getString(6);
                            String PublisherPhone = rs.getString(7);
                            String PublisherEmail = rs.getString(8);
                            String BookTitle = rs.getString(9);
                            String YearPublished = rs.getString(10);
                            String NumberPages = rs.getString(11);

                            System.out.printf(displayFormatOp4, "Publisher Name", "Publisher Address", "Publisher Phone", "Publisher Email",
                            "Group Name", "Head Writer", "Year Formed", "Subject", "Book Title", "Year Published", "Num Of Pages");

                            System.out.printf(displayFormatOp4, dispNull(pub), dispNull(PublisherAddress), dispNull(PublisherPhone),
                                dispNull(PublisherEmail), dispNull(GroupName), dispNull(HeadWriter), dispNull(YearFormed), dispNull(Subject),
                                dispNull(BookTitle), dispNull(YearPublished), dispNull(NumberPages));
                            
                        }
                        
                        
                        System.out.println();
                        rs.close();
                        break;                
                    
                    //show books
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
                                                           
                    //show book information
                    case 6:
                  
                    
                        System.out.println("Enter a book's name: ");
                        String book = in.nextLine();
                        
                        System.out.println("Enter the group name");
                        String gname = in.nextLine();

                        //Shows the users input
                        System.out.println("\nLooking for the info book " + book + ", One moment please...");

                        
                        sql = "SELECT * FROM Books NATURAL JOIN PUBLISHERS NATURAL JOIN WritingGroups where GroupName = ? and BookTitle = ?";
                        pstmt = conn.prepareStatement(sql);
                        pstmt.setString(1,gname);
                        pstmt.setString(2,book);
                        rs = pstmt.executeQuery();
                        
                        
                        if (rs.next()) {               
                            gname = rs.getString(1);        
                            String btitle = rs.getString(2);      
                            String pname = rs.getString(3);
                            String year = rs.getString(4);
                            String pages = rs.getString(5);
                            String paddy = rs.getString(6);
                            String pphone = rs.getString(7);
                            String pemail = rs.getString(8);
                            String hwriter = rs.getString(9);
                            String yformed = rs.getString(10);
                            String sub = rs.getString(11);
                            //System.out.println(btitle+" "+pname+" "+year+" "+pages);
                                                             // Print the column values
                            System.out.printf(displayFormatOp5, "Group Name", "Publisher Name", "Book Title", "Year Published", "Number of Pages","Publisher Address", "Publisher Phone","Publisher Email", "Head Writer", "year Formed","Subject");

                            System.out.printf(displayFormatOp5, dispNull(gname),dispNull(pname),dispNull(btitle),dispNull(year),dispNull(pages),dispNull(paddy),dispNull(pphone),dispNull(pemail),dispNull(hwriter),dispNull(yformed),dispNull(sub));  
                        }
                        else{
                            System.out.println("Could not find book " + book + " create by " + gname + " in the database. ");
                        }

                        System.out.println();
                        break;
                    
                    //input new book
                    case 7:
                    
                        //get user input
                        System.out.println("Enter group name");
                        String groupname = in.nextLine();
                        sql = "select groupname from writinggroups where groupname = ?";
                        pstmt = conn.prepareStatement(sql);
                        pstmt.setString(1,groupname);
                        rs=pstmt.executeQuery();
                        
                        if(false==rs.next()){
                            System.out.println("Group Name Does Not Exist");
                            break;
                        }
                        System.out.println("Enter Book title");
                        String booktitle = in.nextLine();
                        System.out.println("Enter Publisher's Name");
                        String pname = in.nextLine();
                        sql = "select publishername from publishers where publishername = ?";
                        pstmt = conn.prepareStatement(sql);
                        pstmt.setString(1,pname);
                        rs=pstmt.executeQuery();
                        
                        if(false==rs.next()){
                            System.out.println("Publisher Does Not Exist");
                            break;
                        }
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
                        
                        try{
                            pstmt.execute();
                        }
                        catch(SQLException r){
                            System.out.println("failed to insert book");  
                        }
                        break;
                
                        
                    //replace pubilsher    
                    case 8:

                        //execute prepared input
                        
                        
                            //updating old publisher
                        System.out.println("Input the publisher being bought out");
                        String oldpname = in.nextLine();
                        
                        sql = "select booktitle from books where publishername = ?";
                        pstmt = conn.prepareStatement(sql);
                        pstmt.setString(1,oldpname);
                        rs=pstmt.executeQuery();
                        
                        if(false==rs.next()){
                            System.out.println("Publisher Does Not Exist");
                            break;
                        }
                      
                        while(true){    
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
                            try{
                                pstmt.execute();
                                break;
                            }
                            catch(SQLIntegrityConstraintViolationException y){
                                System.out.println("Publisher Already Exists");
                                
                            }
                            catch(SQLException j){
                                System.out.println("Something Went Wrong");
                            }
                        }
                        
                        //update publisher
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
                
                    //remove book    
                    case 9:
                    
                        //get user input
                        System.out.println("Enter Group Name");
                        gname = in.nextLine();
                        System.out.println("Enter Book Title");
                        String btitle = in.nextLine();


                        sql = "Delete from books where GroupName = ? and booktitle = ?";
                        //checks to see if the book actually exists and the publisher itself
                        String sqlbook = "SELECT GroupName, BookTitle, PublisherName, YearPublished, NumberPages FROM Books NATURAL JOIN PUBLISHERS NATURAL JOIN WritingGroups where GroupName = ? and BookTitle = ?";                            

                        pstmt = conn.prepareStatement(sqlbook);
                        pstmt.setString(1, gname);
                        pstmt.setString(2, btitle);

                        rs = pstmt.executeQuery();
                        if(rs.next()){
                            pstmt = conn.prepareStatement(sql);
                            pstmt.setString(1, gname);
                            pstmt.setString(2, btitle);
                            pstmt.executeUpdate();
                            pstmt.close();
                            System.out.println("Deleted " + btitle + " successfully.");
                            break;
                        }else{                     
                            System.out.println("Could not find book " + btitle + " to delete.");
                            break;
                        }
                    
                    //end code
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
            
        }  catch (Exception e) {
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
