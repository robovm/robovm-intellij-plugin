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

package org.robovm.intellij.modules;

import com.intellij.ide.util.projectWizard.*;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.ModuleTypeManager;
import com.intellij.openapi.module.StdModuleTypes;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkTypeId;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.IconLoader;
import com.intellij.psi.CommonClassNames;
import com.intellij.psi.JavaPsiFacade;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.model.java.JavaModuleSourceRootTypes;

import javax.swing.*;

/**
 * Definition for the RoboVM Module (used when creating a new project)
 */
public class RoboVMModuleType extends ModuleType<JavaModuleBuilder> {

    public static final String MODULE_ID = "robovm.module";


    public RoboVMModuleType() {
        super(MODULE_ID);
    }

    public static RoboVMModuleType getInstance() {
        return (RoboVMModuleType) ModuleTypeManager.getInstance().findByID(MODULE_ID);
    }

    @Nullable
    @Override
    public ModuleWizardStep modifyProjectTypeStep(@NotNull SettingsStep settingsStep, @NotNull final ModuleBuilder moduleBuilder) {
        return ProjectWizardStepFactory.getInstance().createJavaSettingsStep(settingsStep, moduleBuilder, new Condition<SdkTypeId>() {
            @Override
            public boolean value(SdkTypeId sdkTypeId) {
                return moduleBuilder.isSuitableSdkType(sdkTypeId);
            }
        });
    }

    @NotNull
    @Override
    public RoboVMModuleBuilder createModuleBuilder() {
        return new RoboVMModuleBuilder();
    }


    @NotNull
    @Override
    public String getName() {
        return "iOS Application";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Java based iOS Application using RoboVM (http://www.robovm.org/)";
    }

    @Override
    public Icon getBigIcon() {
        return IconLoader.getIcon("/icons/robovm_large.png");
    }

    @Override
    public Icon getNodeIcon(@Deprecated boolean b) {
        return IconLoader.getIcon("/icons/robovm_small.png");
    }


    @Override
    public boolean isValidSdk(@NotNull final Module module, final Sdk projectSdk) {
        return isValidJavaSdk(module);
    }

    public static boolean isValidJavaSdk(@NotNull Module module) {
        if (ModuleRootManager.getInstance(module).getSourceRoots(JavaModuleSourceRootTypes.SOURCES).isEmpty()) return true;
        return JavaPsiFacade.getInstance(module.getProject()).findClass(CommonClassNames.JAVA_LANG_OBJECT,
                module.getModuleWithLibrariesScope()) != null;
    }


}
