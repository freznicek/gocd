/*
 * Copyright 2017 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thoughtworks.go.buildsession;

import com.thoughtworks.go.domain.ArtifactsRepositoryStub;
import com.thoughtworks.go.domain.BuildCommand;
import com.thoughtworks.go.domain.BuildStateReporterStub;
import com.thoughtworks.go.domain.JobResult;
import com.thoughtworks.go.helper.TestStreamConsumer;
import com.thoughtworks.go.remote.AgentIdentifier;
import com.thoughtworks.go.remote.work.HttpServiceStub;
import com.thoughtworks.go.util.TestingClock;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.text.StrLookup;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.thoughtworks.go.domain.BuildCommand.exec;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class BuildSessionBasedTestCase {
    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    protected BuildStateReporterStub statusReporter;
    protected Map<String, String> buildVariables;
    protected File sandbox;
    protected ArtifactsRepositoryStub artifactsRepository;
    protected TestStreamConsumer console;
    protected HttpServiceStub httpService;
    protected AgentIdentifier agentIdentifier;

    @Before
    public void superSetup() throws IOException {
        temporaryFolder.delete();
        temporaryFolder.create();
        statusReporter = new BuildStateReporterStub();
        buildVariables = new HashMap<>();
        artifactsRepository = new ArtifactsRepositoryStub();
        sandbox = temporaryFolder.newFolder();
        console = new TestStreamConsumer();
        httpService = new HttpServiceStub();
        agentIdentifier = new AgentIdentifier("hostname", "ipaddress", "uuid");
    }

    protected BuildSession newBuildSession() {
        return new BuildSession("build1",
                agentIdentifier, statusReporter,
                console,
                StrLookup.mapLookup(buildVariables),
                artifactsRepository, httpService, new TestingClock(), sandbox);
    }

    protected String buildInfo() {
        return "\n *** current build info *** \n"
                + "build status: " + statusReporter.status() + "\n"
                + "build result: " + statusReporter.results() + "\n"
                + "build console output: \n"
                + console.output()
                + "\n******";
    }

    protected void runBuild(BuildSession buildSession, BuildCommand command, JobResult expectedResult) {
        JobResult result = buildSession.build(command);
        assertThat(buildInfo(), result, is(expectedResult));
    }

    protected void runBuild(BuildCommand command, JobResult expectedResult) {
        runBuild(newBuildSession(), command, expectedResult);
    }

    protected String pathSystemEnvName() {
        return SystemUtils.IS_OS_WINDOWS ? "Path" : "PATH";
    }

    public static BuildCommand execSleepScript(int seconds) {
        if (SystemUtils.IS_OS_WINDOWS) {
            return exec("ping 1.1.1.1 -n 1 -w " + seconds * 1000 + " >NUL");
        } else {
            return exec("/bin/sleep", String.valueOf(seconds));
        }
    }

    protected String execSleepScriptProcessCommand() {
        return SystemUtils.IS_OS_WINDOWS ? "PING.EXE" : "sleep";
    }

}

