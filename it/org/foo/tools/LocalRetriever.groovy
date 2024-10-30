package org.foo.tools

import edu.umd.cs.findbugs.annotations.NonNull
import hudson.FilePath
import hudson.model.Run
import hudson.model.TaskListener
import org.jenkinsci.plugins.workflow.libs.LibraryRetriever

class LocalRetriever extends LibraryRetriever {
    private final File lib

    LocalRetriever(File lib) {
        this.lib = lib
    }

    @Override
    void retrieve(@NonNull String name, @NonNull String version, boolean changelog, @NonNull FilePath target, @NonNull Run<?, ?> run, @NonNull TaskListener listener) throws Exception {
        new FilePath(lib).copyRecursiveTo(target)
    }

    @Override
    void retrieve(@NonNull String name, @NonNull String version, @NonNull FilePath target, @NonNull Run<?, ?> run, @NonNull TaskListener listener) throws Exception {
        retrieve(name, version, false, target, run, listener)
    }
}
