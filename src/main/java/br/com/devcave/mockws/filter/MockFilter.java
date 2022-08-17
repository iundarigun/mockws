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
import java.util.Arrays;
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


    private final String path;

    private final ObjectMapper mapper;

    public MockFilter(@Value("${mock.files-path:}")
                      final String path,
                      final ObjectMapper mapper) {
        this.path = path.lastIndexOf("/") == path.length() - 1 ? path : path + "/";
        this.mapper = mapper;
    }

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        RequestFacade requestFacade = (RequestFacade) servletRequest;

        getInfoFromRequest(requestFacade);


        Optional<MockResponse> optionalMockResponse = retrieveMockResponse(requestFacade);

        if (optionalMockResponse.isEmpty()) {
            log.warn("No definition found");
            ((ResponseFacade) servletResponse).setStatus(HttpStatus.OK.value());
        } else {
            final MockResponse mockResponse = optionalMockResponse.get();
            processDelay(mockResponse);
            if (!processRandomError(mockResponse, servletResponse)) {
                processResponse(mockResponse, servletResponse);
            }
        }
    }

    private void processResponse(final MockResponse mockResponse, final ServletResponse servletResponse) throws IOException {
        ((ResponseFacade) servletResponse).setStatus(mockResponse.getStatus());
        servletResponse.setContentType("application/json");
        PrintWriter out = servletResponse.getWriter();
        out.print(FileUtils.readFromFile(path, mockResponse.getFile()));
    }

    private void processDelay(final MockResponse response) {
        if (response.getDelay() > 0) {
            try {
                Thread.sleep(response.getDelay());
            } catch (InterruptedException ignored) {
            }
        }
    }

    private boolean processRandomError(final MockResponse response, final ServletResponse servletResponse) throws IOException {
        int randomNumber = new Random().nextInt(100) + 1;
        if (randomNumber <= response.getRatioError()) {
            log.warn("Random error");
            ((ResponseFacade) servletResponse).setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            servletResponse.getWriter().print("Random error");
            return true;
        }
        return false;
    }

    private Optional<MockResponse> retrieveMockResponse(RequestFacade requestFacade) {
        String requestURI = requestFacade.getRequestURI();
        String method = requestFacade.getMethod();
        final Map<String, String[]> params = requestFacade.getParameterMap();
        return mockResponseList.stream()
                .filter(m -> requestURI.contains(m.getUrl()) && m.getVerbs().contains(method))
                .filter(m -> {
                    if (m.getParam() != null) {
                        final String[] values = params.getOrDefault(m.getParam().getKey(), new String[]{});
                        if (values.length > 0) {
                            return Arrays.stream(values).toList().contains(m.getParam().getValue());
                        }
                    }
                    return true;
                })
                .findFirst();
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
