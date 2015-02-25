/*
 * Copyright (C) 2014 Trillian Mobile AB.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.robovm.intellij.runconf;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import org.jdesktop.swingx.util.OS;
import org.jetbrains.annotations.NotNull;
import org.robovm.intellij.RoboVMProcessHandler;
import org.robovm.intellij.RoboVMRuntimeConfiguration;

import java.io.File;


/**
 * This class does the actual 'running' for the run configuration by invoking the gradlew shell script
 */
public class RoboVMCommandLineState extends CommandLineState {

    RoboVMRuntimeConfiguration configuration;
    ExecutionEnvironment environment;

    public RoboVMCommandLineState(RoboVMRuntimeConfiguration roboVMRuntimeConfiguration, ExecutionEnvironment environment) {
        super(environment);
        this.configuration = roboVMRuntimeConfiguration;
        this.environment = environment;
    }

    @NotNull
    @Override
    protected ProcessHandler startProcess() throws ExecutionException {

        String projectPath = configuration.getProject().getBasePath();
        String[] gradleTask = configuration.getGradleTask();
        String gradleBin = "gradlew";

        if (OS.isWindows()) {
            gradleBin = "gradlew.bat";
        }
        new File(projectPath + File.separatorChar + gradleBin).setExecutable(true,false);

        GeneralCommandLine commandLine = new GeneralCommandLine().withWorkDirectory(new File(projectPath));
        commandLine.setExePath(projectPath + File.separatorChar + gradleBin);
        commandLine.addParameters(gradleTask);

        return RoboVMProcessHandler.runCommand(commandLine) ;
    }

}
