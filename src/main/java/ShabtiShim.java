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

        PrintWriter out = response.getWriter();

        String uuid = UUID.randomUUID().toString();

        String token = request.getPathInfo();

        // We are not interested in a lone slash or a leading slash.
        token = token.startsWith("/") ? token.substring(1) : token;

        logger.debug(uuid);

        // No token in path means we need to create one and redirect to external authenticator
        if (token == null || token.isEmpty() ) {

            out.println( "Outgoing!" );
            logger.debug("Outgoing! (redirecting out)");


        }

        // A token, so we should check it returns valid authenticated data
        else {

            out.println( "Incoming!" );
            logger.debug("Incoming!");
            logger.debug(token);

        }

        out.println( "Hello!" );
        out.println( uuid );
        out.println( token );
        out.flush();
        out.close();
    }

}
