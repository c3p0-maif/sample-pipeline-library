package org.foo

import com.cloudbees.hudson.plugins.folder.Folder
import hudson.plugins.git.GitSCM
import jenkins.plugins.git.GitSampleRepoRule
import org.foo.tools.LocalRetriever
import org.jenkinsci.plugins.workflow.cps.CpsScmFlowDefinition
import org.jenkinsci.plugins.workflow.job.WorkflowJob
import org.jenkinsci.plugins.workflow.libs.GlobalLibraries
import org.jenkinsci.plugins.workflow.libs.LibraryConfiguration
import org.junit.Before
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import org.jvnet.hudson.test.BuildWatcher
import org.jvnet.hudson.test.JenkinsRule
import org.jvnet.hudson.test.recipes.WithTimeout

class FooIT {

    static final int TIMEOUT = 1800

    @ClassRule
    public static BuildWatcher buildWatcher = new BuildWatcher()

    @Rule
    public JenkinsRule rule = new JenkinsRule()

    @Rule
    public GitSampleRepoRule repo = new GitSampleRepoRule()

    @Before
    void declareMaifPipelineLibrary() throws Exception {
        rule.timeout = TIMEOUT

        File workspace = new File('.')
        LibraryConfiguration lc = new LibraryConfiguration('sample-pipeline-library', new LocalRetriever(workspace))
        lc.setDefaultVersion("fixed")
        GlobalLibraries.get().setLibraries(Collections.singletonList(lc))

        System.out.println("Run on ${rule.URL}")
    }

    @Test
    @WithTimeout(value = 1800)
    void testMavenProject() throws Exception {
        repo.init()
        repo.write('Jenkinsfile', '''@Library('sample-pipeline-library') _
foo()''')
        repo.git('add', 'Jenkinsfile')
        repo.git('commit', '--message=init')

        Folder f = rule.jenkins.createProject(Folder.class, "f")
        WorkflowJob p = f.createProject(WorkflowJob.class, "p")

        p.setDefinition(new CpsScmFlowDefinition(new GitSCM(repo.toString()), 'Jenkinsfile'))
        rule.assertLogContains("hello from master", rule.buildAndAssertSuccess(p))
    }
}
