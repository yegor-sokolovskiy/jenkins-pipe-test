//import static com.lesfurets.jenkins.unit.global.lib.LibraryConfiguration.library
//import static com.lesfurets.jenkins.unit.global.lib.LocalSource.localSource


//import static com.lesfurets.jenkins.unit.MethodCall.callArgsToString

import org.assertj.core.api.Assertions
//import static org.junit.Assert.*


//import junit.framework.Assert
import org.junit.Test
import org.junit.Before
import org.junit.Rule
//import org.junit.rules.TemporaryFolder
import org.apache.commons.io.FileUtils

import java.time.*
import com.lesfurets.jenkins.unit.*
//import com.lesfurets.jenkins.unit.BasePipelineTest
//import com.lesfurets.jenkins.unit.PipelineTestHelper

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
            //helper.addShMock('chuchu', 'TESTDONE', 0)            
            helper.registerAllowedMethod("sh", [Map.class], {cmd->                
                if (cmd['script'].contains("runtests.pl")) {
                    return 'TESTDONE'
                }
            })  
            binding.setVariable('WORKSPACE', System.getProperty("user.dir"))          
        } 

        @Before
        public void createTmpFiles() throws IOException {
            println("Stage 0. Prepare temporary files")
            File createdFile= new File("../tests/Makefile")
            FileUtils.writeStringToFile(createdFile, 'TEST = srcdir=$(srcdir) $(PERL) $(PERLFLAGS) $(srcdir)/runtests.pl <<< JenkinsFile Auto Test')
            def passUnits = [1000, 1100, 1200, 1300, 1320, 1500, 1600]
            def skipUnits = [1307, 1330, 1660]
            def allUnits = passUnits + skipUnits
           
            allUnits.each {
                File unitFile= new File("../tests/unit/unit" + it)
                FileUtils.writeStringToFile(unitFile, "Num unit test: " + it)
            }
        }     

        @Test
        void check_Jenkinsfile() throws Exception {
            LocalDateTime t = LocalDateTime.now()
            println(t)

            File createdFile= new File("../tests/Makefile")
            
            println("!!!+++!!!")
            
            def script = runScript("../Jenkinsfile")

            assertJobStatusSuccess()
            printCallStack()

            println("Stage N. Check temporary files")
            final String sn = FileUtils.readFileToString(createdFile)            
            Assertions.assertThat(sn).isEqualTo('TEST = srcdir=$(srcdir) $(PERL) $(PERLFLAGS) $(srcdir) <<< JenkinsFile Auto Test')

            println("Stage Z. Delete temporary files")
            def testsDir = new File("../tests")
            def res = testsDir.deleteDir()            
            assert res

        }
}