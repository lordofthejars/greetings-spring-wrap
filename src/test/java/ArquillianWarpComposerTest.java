import org.jboss.arquillian.warp.Warp;
import org.jboss.arquillian.warp.WarpTest;
import org.superbiz.composer.configuration.HelloWorldConfiguration;
import org.superbiz.composer.configuration.HelloWorldInitializer;
import org.superbiz.composer.controller.HelloWorldController;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.arquillian.warp.Inspection;
import org.jboss.arquillian.warp.servlet.AfterServlet;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.web.servlet.ModelAndView;

import java.net.URL;

@WarpTest
@RunWith(Arquillian.class)
public class ArquillianWarpComposerTest {

    @Drone
    WebDriver browser;

    @ArquillianResource
    URL contextPath;

    @Deployment
    public static WebArchive createDeployment() {
        WebArchive war = ShrinkWrap.create(WebArchive.class)
                .addClasses(HelloWorldConfiguration.class, HelloWorldInitializer.class, HelloWorldController.class)
                .addAsLibraries(Maven.resolver().loadPomFromFile("pom.xml").resolve("org.springframework:spring-context", "org.springframework:spring-webmvc").withTransitivity().as(JavaArchive.class))
                .addAsWebInfResource("WEB-INF/views/welcome.jsp", "views/welcome.jsp");
        System.out.println(war.toString(true));
        return war;
    }

    @Test
    @RunAsClient
    public void testGetAllComposers() {
       Warp.initiate(() -> {
           browser.navigate().to(contextPath);
           WebElement dataTable = browser.findElement(By.id("greeting"));
           System.out.println("My Text"+dataTable.getText());
       }).inspect(new AllComposers());
    }
    public static class AllComposers extends Inspection {
        private static final long serialVersionUID = 1L;
        @ArquillianResource
        ModelAndView modelAndView;

        @AfterServlet
        public void testLogin() {
            System.out.println("My View"+modelAndView);
        }
    }
}
