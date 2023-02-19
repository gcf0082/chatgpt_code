//mvn archetype:generate -DgroupId=com.example -DartifactId=my-plugin -DarchetypeArtifactId=maven-plugin-archetype -DinteractiveMode=false

import java.util.List;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "dependency-graph")
public class DependencyGraphMojo extends AbstractMojo {
    @Parameter(defaultValue = "${project.artifacts}", readonly = true, required = true)
    private List<Artifact> artifacts;

    public void execute() throws MojoExecutionException {
        for (Artifact artifact : artifacts) {
            String groupId = artifact.getGroupId();
            String artifactId = artifact.getArtifactId();
            String version = artifact.getVersion();
            getLog().info(String.format("%s:%s:%s", groupId, artifactId, version));
        }
    }
}


/*
<build>
    <plugins>
        <plugin>
            <groupId>com.example</groupId>
            <artifactId>my-plugin</artifactId>
            <version>1.0.0</version>
            <executions>
                <execution>
                    <id>dependency-graph</id>
                    <phase>compile</phase>
                    <goals>
                        <goal>dependency-graph</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
*/
