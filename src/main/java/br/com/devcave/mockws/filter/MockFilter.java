package br.com.devcave.mockws.filter;

import br.com.devcave.mockws.domain.MockResponse;
import br.com.devcave.mockws.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.RequestFacade;
import org.apache.catalina.connector.ResponseFacade;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Configuration
public class MockFilter implements Filter {

    public static List<MockResponse> mockResponseList = new ArrayList<>();

    @Value("${mock.files-path:}")
    private String path;

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        log.info("filter");
        String requestURI = ((RequestFacade) servletRequest).getRequestURI();
        String method = ((RequestFacade) servletRequest).getMethod();

        Optional<MockResponse> mockResponse = mockResponseList.stream()
                .filter(m -> requestURI.contains(m.getUrl()) && m.getVerbs().contains(method))
                .findFirst();
        if (mockResponse.isEmpty()) {
            ((ResponseFacade) servletResponse).setStatus(HttpStatus.OK.value());
        } else {
            ((ResponseFacade) servletResponse).setStatus(mockResponse.get().getStatus());
            servletResponse.setContentType("application/json");
            PrintWriter out = servletResponse.getWriter();
            out.print(FileUtils.readFromFile(path, mockResponse.get().getFile()));
        }
    }

}
