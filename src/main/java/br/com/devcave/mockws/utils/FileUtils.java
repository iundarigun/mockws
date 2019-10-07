package br.com.devcave.mockws.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

@Slf4j
public class FileUtils {

    public static String readFromFile(final String path, final String filename) {
        try {
            Optional<String> jsonResponse = Files
                    .lines(Paths.get(ResourceUtils
                            .getFile((StringUtils.isEmpty(path)?"classpath:json/":path) + filename)
                            .getAbsolutePath()), StandardCharsets.UTF_8)
                    .reduce(String::concat);
            return jsonResponse.orElse("");
        } catch (Exception e) {
            log.warn("readFromFile, file=" + filename, e);
        }
        return null;
    }
}
