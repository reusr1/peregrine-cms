package com.peregrine.admin.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.models.factory.ModelFactory;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

import static com.peregrine.admin.servlets.ServletHelper.convertSuffixToParams;

@Component(
        service = Servlet.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=create template servlet",
                Constants.SERVICE_VENDOR + "=headwire.com, Inc",
                ServletResolverConstants.SLING_SERVLET_RESOURCE_TYPES + "=api/admin/createTemplate"
        }
)
@SuppressWarnings("serial")
public class CreateTemplateServlet extends SlingSafeMethodsServlet {

    private final Logger log = LoggerFactory.getLogger(CreateTemplateServlet.class);

    @Reference
    ModelFactory modelFactory;

    @Override
    protected void doGet(SlingHttpServletRequest request,
                         SlingHttpServletResponse response) throws ServletException,
            IOException {

        Map<String, String> params = convertSuffixToParams(request);
        String parentPath = params.get("path");
        log.debug(params.toString());

        ResourceResolver rr = request.getResourceResolver();
        Resource parent = rr.getResource(params.get("parentPath"));

        Session session = request.getResourceResolver().adaptTo(Session.class);
        try {
            Node node = session.getRootNode().addNode(parentPath.substring(1)+"/"+params.get("name"));
            node.setPrimaryType("per:Page");
            Node content = node.addNode("jcr:content");
            content.setPrimaryType("per:PageContent");
            content.setProperty("sling:resourceType", "example/components/page");
            content.setProperty("jcr:title", params.get("name"));
            session.save();
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (RepositoryException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            e.printStackTrace(response.getWriter());
        }
    }

}
