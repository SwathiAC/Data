package com.intuit.accountant.services.dcm.resources;

import com.intuit.accountant.services.common.annotation.UserRealmTicketFilter;
import com.intuit.accountant.services.common.iam.IamAuthContext;
import com.intuit.accountant.services.dcm.services.DocumentServiceImpl;
import com.intuit.accountant.services.dcm.util.DCMUtil;
import com.intuit.support.auth.SupportFilter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static com.intuit.accountant.services.common.iam.IamAuthContextFactory.getIamAuth;

/**
 * Created by sshashidhar on 16/05/19.
 */
@Component
@Path("/admin/document")
@Api(value = "/admin/document", description = "Endpoint to upload and download document to/from FDP for Admin Console")
@UserRealmTicketFilter
public class DocumentSupportResource {

    private static final Logger logger = LoggerFactory.getLogger(DocumentSupportResource.class);

    @Autowired
    private DocumentServiceImpl documentService;

    @GET
    @Path("/upload")
    @Produces({MediaType.APPLICATION_JSON})
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server encountered an unexpected condition that prevented it from fulfilling this request"),
            @ApiResponse(code = 200, message = "Successfully processed the request.")
    })
    public Response upload(@Context HttpHeaders headers, @QueryParam("customer_realm") final String customerRealmId){

        try{
            return Response.ok(documentService.uploadDocument(customerRealmId, DCMUtil.ensureTID())).build();
        }catch (Exception e){
            logger.error("Failed to call Document Service Upload Request for realmId={}", customerRealmId);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON).build();
        }
    }

    @GET
    @Path("/download/{docId}")
    @Produces({MediaType.APPLICATION_JSON})
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server encountered an unexpected condition that prevented it from fulfilling this request"),
            @ApiResponse(code = 200, message = "Successfully processed the request.")
    })
    public Response download(@Context HttpHeaders headers, @PathParam("docId") String docId, @QueryParam("customer_realm") final String customerRealmId){


        try{
            return Response.ok(documentService.downloadDocument(customerRealmId, DCMUtil.ensureTID(), docId)).build();
        }catch (Exception e){
            logger.error("Failed to call Document Service Download Request for realmId={}", customerRealmId);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON).build();
        }
    }

    @DELETE
    @Path("/delete/{docId}")
    @Produces({MediaType.APPLICATION_JSON})
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server encountered an unexpected condition that prevented it from fulfilling this request"),
            @ApiResponse(code = 200, message = "Successfully processed the request.")
    })
    public Response deleteDocument(@Context HttpHeaders headers, @PathParam("docId") String docId, @QueryParam("customer_realm") final String customerRealmId){

        try{
            if(documentService.deleteDocument(customerRealmId, DCMUtil.ensureTID(), docId)){
                return Response.ok().build();
            }else {
                return Response.status(Response.Status.NOT_FOUND).type(MediaType.APPLICATION_JSON).build();
            }
        }catch (Exception e){
            logger.error("Failed to call Document Service Delete Request for realmId={}", customerRealmId);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON).build();
        }
    }
}
