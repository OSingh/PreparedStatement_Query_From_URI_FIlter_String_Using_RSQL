package rsql.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.xml.ws.Response;

@Path("custodian-service/1/custodians")
public class RSQLResource {

    @GET
    public Response getCustodians(@QueryParam("filter") String filter) {
        return null;
    }
}
