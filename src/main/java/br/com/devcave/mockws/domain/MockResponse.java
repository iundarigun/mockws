package br.com.devcave.mockws.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class MockResponse {

    @EqualsAndHashCode.Include
    private String url;

    private int status;

    private String file;

    private List<String> verbs;

    private int ratioError = 0;

    private int delay = 0;

    public int getRatioError() {
        return Math.min(this.ratioError, 100);
    }
}
