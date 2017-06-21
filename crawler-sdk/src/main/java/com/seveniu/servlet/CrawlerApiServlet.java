package com.seveniu.servlet;

import com.fasterxml.jackson.core.type.TypeReference;
import com.seveniu.service.CrawlerClientReceiver;
import com.seveniu.def.TaskStatus;
import com.seveniu.entity.data.Node;
import com.seveniu.service.RequestBody;
import com.seveniu.task.TaskStatistic;
import com.seveniu.util.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by dhlz on 1/4/17.
 * *
 */
public class CrawlerApiServlet extends HttpServlet {
    private Logger log = LoggerFactory.getLogger(this.getClass());
    private CrawlerClientReceiver crawlerClientReceiver;

    public CrawlerApiServlet(CrawlerClientReceiver crawlerClientReceiver) {
        this.crawlerClientReceiver = crawlerClientReceiver;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, config.getServletContext());
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {

        String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        log.debug("consumer request body : {}", body);
        RequestBody requestBody = Json.toObject(body, RequestBody.class);
        String action = requestBody.getAction();
        String data = requestBody.getData();
        Object result = null;
        switch (action) {
            case "has":
                result = crawlerClientReceiver.has(data);
                break;
            case "done":
                Node node = Json.toObject(data, Node.class);
                crawlerClientReceiver.done(node);
                break;
            case "statistic":
                TaskStatistic taskStatistic = Json.toObject(data, TaskStatistic.class);
                crawlerClientReceiver.statistic(taskStatistic);
                break;
            case "taskStatusChange":
                Map<String, Object> map = Json.toObject(data, new TypeReference<Map<String, Object>>() {
                });
                String taskId = (String) map.get("taskId");
                String status = (String) map.get("status");
                crawlerClientReceiver.taskStatusChange(taskId, TaskStatus.valueOf(status));
                break;
        }
        response.setContentType("application/json;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            // Write some content
            if (result != null) {
                out.println(Json.toJson(result));
            }
        }

    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            // Write some content
            out.println("<html>");
            out.println("<head>");
            out.println("<title>MyFirstServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h2>can't access get</h2>");
            out.println("</body>");
            out.println("</html>");
        }
    }
}
