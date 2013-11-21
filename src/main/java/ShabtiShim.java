import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.UUID;
import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;


public class ShabtiShim extends HttpServlet {

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
            throws ServletException, IOException {

        BasicConfigurator.configure();
        Logger logger = Logger.getLogger("Shabti.Shim");

        String uuid = UUID.randomUUID().toString();

        String token = request.getPathInfo();

        logger.debug(uuid);

        PrintWriter out = response.getWriter();
        out.println( "Hello!" );
        out.println( uuid );
        out.println( token );
        out.flush();
        out.close();
    }

}
