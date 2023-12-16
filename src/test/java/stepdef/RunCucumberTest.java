package stepdef;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import lombok.extern.slf4j.Slf4j;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features = {"src/test/resources/features"},
        glue = "stepdef",
        plugin = {"pretty","html:target/cucumber-reports/index.html"}

)
@Slf4j
public class RunCucumberTest {

}
