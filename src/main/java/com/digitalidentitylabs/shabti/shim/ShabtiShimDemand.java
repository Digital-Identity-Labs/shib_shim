package com.digitalidentitylabs.shabti.shim;

import java.util.UUID;
import javax.servlet.http.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: pete
 * Date: 24/11/2013
 * Time: 14:34
 * To change this template use File | Settings | File Templates.
 */
public class ShabtiShimDemand {

    protected JSONObject data;
    public    String     token;

    // Create an empty possibly pointless object
    public ShabtiShimDemand() {

         data = new JSONObject();
    }

    // Build object from raw JSON
    public ShabtiShimDemand(String rawJSON) {

        this.data = new JSONObject();

        Object obj= JSONValue.parse(rawJSON);
        JSONObject possibleDemand=(JSONObject)obj;

        if (possibleDemand != null) {

          this.data = possibleDemand;

        }

    }

    // Build new object from request
    @SuppressWarnings("unchecked")
    public ShabtiShimDemand(HttpServletRequest request) {

        this.data = new JSONObject();

        // Need a better source of random uniqueness than this, I think...
        String uuid  = UUID.randomUUID().toString();
        token = DigestUtils.md5Hex(uuid);

        // Core attributes
        this.data.put("forceAuthn",   request.getAttribute("forceAuthn")   );
        this.data.put("isPassive",    request.getAttribute("isPassive")    );
        this.data.put("authnMethod",  request.getAttribute("authnMethod")  );
        this.data.put("relyingParty", request.getAttribute("relyingParty") );

        // Copy of the token (this is also the key when stored)
        this.data.put("token", token);

        // Metadata
        this.data.put("created_at", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        // User agent verification data
        this.data.put("user_address", request.getRemoteAddr() );
        this.data.put("agent_hash",   DigestUtils.md5Hex(request.getHeader("User-Agent")));

        // Service information
        this.data.put("site_domain",  request.getServerName() );
        this.data.put("server_tag",   "indiid"                );
        this.data.put("component",    "core"                  );
        this.data.put("protocol",     "shibboleth"            );
        this.data.put("version",      1                       ); // FIXME: Needs to be elsewhere, surely?

        // A bit of glue info
        this.data.put("return_url",   request.getRequestURL() );


    }

    public Object get(String attrib) {

        return this.data.get(attrib);

    }

    // Returns a JSON string representation of the data
    public String toJSONString(){

       return this.data.toJSONString();

    }

    public boolean isValidAuthenticatedDemand() {

        // Check for an empty hashmap - null object equivalent
        if (this.data.isEmpty()) {
            return false;
        }

        return false;

    }
}
