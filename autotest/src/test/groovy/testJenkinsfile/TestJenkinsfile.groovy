import org.assertj.core.api.Assertions
import org.junit.Test
import org.junit.Before
import org.junit.Rule
import org.apache.commons.io.FileUtils

import java.time.*
import groovy.io.FileType
import com.lesfurets.jenkins.unit.*

class MockFindFiles {
    String name
    String path
}

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
            helper.registerAllowedMethod("sh", [Map.class], {cmd->                
                if (cmd['script'].contains("runtests.pl")) {
                    return 'TESTDONE'
                }
            })
            helper.registerAllowedMethod("readFile", [Map.class], { arg->
                if (arg['file'] == "./tests/Makefile") {
                    File createdFile= new File("../tests/Makefile")
                    return FileUtils.readFileToString(createdFile)
                }
            })
            helper.registerAllowedMethod("writeFile", [Map.class], { arg->
                if (arg['file'] == "./tests/Makefile") {
                    File createdFile= new File("../tests/Makefile")
                    FileUtils.writeStringToFile(createdFile, arg['text'])
                }
            })
            helper.registerAllowedMethod("findFiles", [Map.class], { arg->
                mapAllUnits = []
                if (arg['glob'] == "tests/unit/unit*") {
                    def unitDir = new File("../tests/unit/")  
                    unitDir.eachFileMatch FileType.FILES, ~/.*unit.*/, { f->
                        MockFindFiles uf = new MockFindFiles()
                        uf.name = f
                        mapAllUnits << uf
                    }                                      
                }
                return mapAllUnits
            })
            binding.setVariable('WORKSPACE', System.getProperty("user.dir").replace("autotest", ""))          
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
            Assertions.assertThat(sn).isEqualTo('TEST = srcdir=$(srcdir) $(PERL) $(PERLFLAGS) <<< JenkinsFile Auto Test')

            println("Stage Z. Delete temporary files")
            def testsDir = new File("../tests")
            def res = testsDir.deleteDir()            
            assert res

        }
}