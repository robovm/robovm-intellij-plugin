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

import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.runners.DefaultProgramRunner;
import org.jetbrains.annotations.NotNull;

/**
 * Definition of the run configuration (ie. what we can/can't run)
 */
public class RoboVMRunner extends DefaultProgramRunner {
    @NotNull
    @Override
    public String getRunnerId() {
        return RoboVMConfigurationType.id;
    }

    @Override
    public boolean canRun(@NotNull String id, @NotNull RunProfile runProfile) {
        if (!id.equals("Run")) {
            return false;
        }
        return id.equals("Run"); // && runProfile instanceof RoboVMRuntimeConfiguration;
    }
}
