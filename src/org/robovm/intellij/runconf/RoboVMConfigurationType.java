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

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.robovm.intellij.RoboVMRuntimeConfiguration;

import javax.swing.*;

/**
 * Definition of the run configuration
 */
public class RoboVMConfigurationType implements ConfigurationType {

    private final RoboVMConfigurationFactory configurationFactory;
    private String displayName ="iOS Application";
    private String typeDescription = "iOS Application";
    private Icon icon = IconLoader.getIcon("/icons/robovm_small.png");
    public static String id = "robovm.config";

    public RoboVMConfigurationType() {
        configurationFactory = new RoboVMConfigurationFactory(this);

    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String getConfigurationTypeDescription() {
        return typeDescription;
    }

    @Override
    public Icon getIcon() {
        return icon;
    }

    @NotNull
    @Override
    public String getId() {
        return id;
    }

    @Override
    public ConfigurationFactory[] getConfigurationFactories() {
        return new ConfigurationFactory[] { configurationFactory};
    }

    public class RoboVMConfigurationFactory extends ConfigurationFactory {
        public RoboVMConfigurationFactory(RoboVMConfigurationType roboVMConfigurationType) {
            super (roboVMConfigurationType);
        }

        @Override
        public RunConfiguration createTemplateConfiguration(Project project) {
            return new RoboVMRuntimeConfiguration("Run Configuration", project, getInstance());
        }
    }

    public static RoboVMConfigurationType getInstance() {
        return ContainerUtil.findInstance(Extensions.getExtensions(CONFIGURATION_TYPE_EP), RoboVMConfigurationType.class);
    }


}
