package guru.springframework.config;

import guru.springframework.config.models.GitInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppProperties {

    @Autowired
    private GitInfo gitInfo;

    public GitInfo getGitInfo() {
        return gitInfo;
    }
}
