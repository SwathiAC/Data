package com.intuit.accountant.services.dcm.resources;

import com.intuit.accountant.services.common.annotation.UserRealmTicketFilter;
import com.intuit.accountant.services.dcm.model.MailRequest;
import com.intuit.accountant.services.dcm.notifications.*;
import com.intuit.support.auth.SupportFilter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sshashidhar on 26/02/19.
 */
@Component
@Path("/admin/internal/mail")
@Api(value = "/mail", description = "Service for sending custom emails from Admin Console")
@UserRealmTicketFilter
public class EmailResource {

    private static final Logger logger = LoggerFactory.getLogger(EmailResource.class);

    @Autowired
    private NotificationProviderFactory notificationProviderFactory;

    @POST
    @Path("/send")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Server encountered an unexpected condition that prevented it from fulfilling this request"),
            @ApiResponse(code = 400, message = "Bad Request. Please check the payload."),
            @ApiResponse(code = 200, message = "Successfully processed the request.")
    })
    public Response sendEmail(MailRequest mailRequest){
        logger.info("Sending email for jobId={} status={} ", mailRequest.getJobId(), mailRequest.getJobStatus());
        try{
            INotificationProvider notificationProvider = notificationProviderFactory.getNotificationProvider(NotificationProvider.OINP, NotificationType.EMAIL);;
            NotificationRequest notificationRequest = createNotificationRequest(mailRequest);
            NotificationResponse notificationResponse = notificationProvider.sendNotification(notificationRequest);
            if(notificationResponse.getValidRecipientResponses().get(0).getStatusCode().getStatusCode()== HttpStatus.SC_OK){
                return Response.ok().build();
            }
        }catch (RuntimeException re){
            re.printStackTrace();
            return Response.serverError().build();
        }
        return Response.noContent().build();
    }

    private NotificationRequest createNotificationRequest(MailRequest mailRequest) {
        NotificationRequest notificationRequest = new NotificationRequest();
        Map<String, String> templateVariablesMap = new HashMap<>();
        templateVariablesMap.put("jobId", StringUtils.substring(mailRequest.getJobId(),0,4));
        templateVariablesMap.put("fullName",StringUtils.defaultIfEmpty(mailRequest.getFullName(),"Data Conversions Customer"));
        templateVariablesMap.put("notifyEmailAddress",mailRequest.getNotifyEmailAddress());
        templateVariablesMap.put("sourceProduct", mailRequest.getSourceProduct());
        templateVariablesMap.put("destinationProduct", mailRequest.getDestinationProduct());
        templateVariablesMap.put("jobStatus", mailRequest.getJobStatus());
        templateVariablesMap.put("customMessage", mailRequest.getCustomMessage());
        notificationRequest.setAuthId(mailRequest.getAuthId());
        notificationRequest.setRealmId(mailRequest.getRealmId());
        notificationRequest.setTemplateVariables(templateVariablesMap);
        return notificationRequest;
    }

}
