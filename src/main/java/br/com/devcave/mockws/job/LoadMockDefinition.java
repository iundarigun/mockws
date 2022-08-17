package br.com.devcave.mockws.job;

import br.com.devcave.mockws.domain.MockResponse;
import br.com.devcave.mockws.filter.MockFilter;
import br.com.devcave.mockws.utils.FileUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
public class LoadMockDefinition {

    private final String definitionPath;

    private final ObjectMapper objectMapper;

    private final ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();

    public LoadMockDefinition(@Value("${mock.definition-path:}")
                              final String definitionPath,
                              final ObjectMapper objectMapper) {
        if (StringUtils.hasText(definitionPath)) {
            this.definitionPath = definitionPath.lastIndexOf("/") == definitionPath.length() - 1? definitionPath : definitionPath + "/";
        } else {
            this.definitionPath = "classpath:";
        }
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void postConstruct() {
        scheduledExecutor.scheduleAtFixedRate(this::fillMock, 0, 30, TimeUnit.SECONDS);
    }

    private void fillMock() {
        try {
            String definitions = FileUtils.readFromFile(definitionPath, "mockdefinitions.json");
            MockResponse[] mockResponseList = objectMapper.readValue(definitions, MockResponse[].class);
            MockFilter.mockResponseList.clear();
            MockFilter.mockResponseList.addAll(Arrays.asList(mockResponseList));
        } catch (Exception exception) {
            log.warn("No definition file found!");
        }
    }
}
