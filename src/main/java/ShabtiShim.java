import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.UUID;



public class ShabtiShim extends HttpServlet {
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
            throws ServletException, IOException {

        String uuid = UUID.randomUUID().toString();

        String token = request.getPathInfo();

        PrintWriter out = response.getWriter();
        out.println( "Hello!" );
        out.println( uuid );
        out.println( token );
        out.flush();
        out.close();
    }
}
