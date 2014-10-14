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
package org.robovm.intellij;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessTerminatedListener;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;


/**
 * This class creates a bridge between the command runner and the IDE
 */
public class RoboVMProcessHandler extends OSProcessHandler {

    public RoboVMProcessHandler(@NotNull Process process) throws IOException {
        super(process);
    }

    public RoboVMProcessHandler(Process process , String command) throws ExecutionException {
        super(process, command);
    }

    public static RoboVMProcessHandler runCommand(final GeneralCommandLine commandLine) throws ExecutionException {
        final RoboVMProcessHandler roboVMProcessHandler = new RoboVMProcessHandler(commandLine.createProcess(), commandLine.getCommandLineString());
        ProcessTerminatedListener.attach(roboVMProcessHandler);
        return roboVMProcessHandler;
    }
}
