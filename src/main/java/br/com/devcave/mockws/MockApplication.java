package br.com.devcave.mockws;

import br.com.devcave.mockws.domain.MockResponse;
import br.com.devcave.mockws.filter.MockFilter;
import br.com.devcave.mockws.utils.FileUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Arrays;

@Slf4j
@SpringBootApplication
public class MockApplication {

    @Autowired
    private ObjectMapper objectMapper;

    public static void main(String[] args) {
        SpringApplication.run(MockApplication.class, args);
    }

    @PostConstruct
    public void fillMock() throws IOException {
        String definitions = FileUtils.readFromFile("classpath:", "mockdefinitions.json");
        MockResponse[] mockResponseList = objectMapper.readValue(definitions, MockResponse[].class);
        MockFilter.mockResponseList.addAll(Arrays.asList(mockResponseList));
    }
}
