package br.com.devcave.mockws.filter;

import br.com.devcave.mockws.domain.MockResponse;
import br.com.devcave.mockws.utils.FileUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.RequestFacade;
import org.apache.catalina.connector.ResponseFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Configuration
public class MockFilter implements Filter {

    public static List<MockResponse> mockResponseList = new ArrayList<>();

    @Value("${mock.files-path:}")
    private String path;

    @Autowired
    private ObjectMapper mapper;

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        RequestFacade requestFacade = (RequestFacade) servletRequest;

        getInfoFromRequest(requestFacade);

        String requestURI = ((RequestFacade) servletRequest).getRequestURI();
        String method = ((RequestFacade) servletRequest).getMethod();
        Optional<MockResponse> mockResponse = mockResponseList.stream()
                .filter(m -> requestURI.contains(m.getUrl()) && m.getVerbs().contains(method))
                .findFirst();
        if (mockResponse.isEmpty()) {
            log.warn("No definition found");
            ((ResponseFacade) servletResponse).setStatus(HttpStatus.OK.value());
        } else {
            if (mockResponse.get().getDelay() > 0) {
                try {
                    Thread.sleep(mockResponse.get().getDelay());
                } catch (InterruptedException ignored) {
                }
            }
            int randomNumber = new Random().nextInt(100) + 1;
            if (randomNumber <= mockResponse.get().getRatioError()) {
                log.warn("Random error");
                ((ResponseFacade) servletResponse).setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                servletResponse.getWriter().print("Random error");
            } else {
                ((ResponseFacade) servletResponse).setStatus(mockResponse.get().getStatus());
                servletResponse.setContentType("application/json");
                PrintWriter out = servletResponse.getWriter();
                out.print(FileUtils.readFromFile(path, mockResponse.get().getFile()));
            }
        }
    }

    private void getInfoFromRequest(RequestFacade request) throws JsonProcessingException {
        log.info("Request information: Url: {}, Method: {}", request.getRequestURL().toString(), request.getMethod());
        Map<String, List<String>> headersMap = Collections
                .list(request.getHeaderNames())
                .stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        h -> Collections.list(request.getHeaders(h))
                ));
        log.info("Request information: Headers: {}", mapper.writeValueAsString(headersMap));
        log.info("Request information: QueryString: {}", request.getQueryString());
        try {
            log.info("Request information: Body: {}", request.getReader().lines()
                    .map(String::trim)
                    .collect(Collectors.joining()));
        } catch (IOException e) {
            log.info("Request: No body");
        }
    }

}
