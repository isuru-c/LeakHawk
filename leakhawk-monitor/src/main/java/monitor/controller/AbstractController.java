package monitor.controller;



import monitor.resource.ResponseResource;

import javax.ws.rs.core.Response;

/**
 * Abstract controller will contain the common methods for the Controller classes
 *
 * @author Sugeesh Chandraweera
 */
public abstract class AbstractController {

    /**
     * Returns a success response with status code 200
     *
     * @param object entity which needs to be set for response
     * @return built response Object
     */
    public Response sendSuccessResponse(Object object) {
        return Response.status(200).entity(object).header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT").build();
    }

    /**
     * Returns an unauthorized response with status code 401
     *
     * @return build response
     */
    public Response sendUnauthorizedResponse() {
        return Response.status(401).header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT").build();
    }

    /**
     * Returns a service exception
     *
     * @param e service exception which needs to be handled
     * @return response built for the particular exception
     */
    public Response handleServiceException(Exception e) {
        return Response
                .status(400)
                .header("Subject", e.getCause().getMessage())
                .entity(new ResponseResource(e.getMessage())).header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .build();
    }

    /**
     * Returns a service exception for custom message
     *
     * @param e service exception which needs to be handled
     * @return response built for the particular exception
     */
    public Response handleServiceException(Exception e, String customMessage) {
        return Response
                .status(400)
                .header("Subject", customMessage)
                .entity(new ResponseResource(customMessage)).header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .build();
    }
}
