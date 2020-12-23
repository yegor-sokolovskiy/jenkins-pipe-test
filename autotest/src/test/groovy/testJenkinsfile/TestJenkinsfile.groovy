import static com.lesfurets.jenkins.unit.global.lib.LibraryConfiguration.library
import static com.lesfurets.jenkins.unit.global.lib.LocalSource.localSource


import org.junit.Rule



//import static com.lesfurets.jenkins.unit.MethodCall.callArgsToString
import static org.junit.Assert.assertEqual


import org.junit.Test
import org.junit.Before
import java.time.*
import com.lesfurets.jenkins.unit.BasePipelineTest

class TestJenkinsFile extends BasePipelineTest {

        @Override
        @Before
        public void setUp() throws Exception {
            super.setUp();
            this.binding.setVariable('docker', [               
                image: { imageName ->   
                    return [
                        inside: {  closure ->                            
                            closure()
                        }
                    ]
                }
            ])
            binding.setVariable('scm', [:])
            helper.registerAllowedMethod("git", [String], { String arg ->
                 println "MOCK: Git check repo '${arg}' " 
            })
        }

        @Test
        void check_Jenkinsfile() throws Exception {
            LocalDateTime t = LocalDateTime.now();
            println(t)
            println("!!!+++!!!")
            def script = runScript("../Jenkinsfile")
            assertJobStatusSuccess()
            printCallStack()
        }
}