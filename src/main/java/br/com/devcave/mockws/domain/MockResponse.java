package br.com.devcave.mockws.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class MockResponse {

    @EqualsAndHashCode.Include
    private String url;

    private Map.Entry<String, String> param;

    private int status;

    private String file;

    private List<String> verbs;

    private int ratioError = 0;

    private int delay = 0;

    public int getRatioError() {
        return Math.min(this.ratioError, 100);
    }
}
