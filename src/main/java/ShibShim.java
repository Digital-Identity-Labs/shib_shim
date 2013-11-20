import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.UUID;



public class ShibShim extends HttpServlet {
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
            throws ServletException, IOException {

        String uuid = UUID.randomUUID().toString();
        //System.out.println("uuid = " + uuid);

        PrintWriter out = response.getWriter();
        out.println( "Hello World!" );
        out.flush();
        out.close();
    }
}
