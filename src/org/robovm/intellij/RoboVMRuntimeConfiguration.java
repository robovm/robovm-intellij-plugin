package org.robovm.intellij;/*
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

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.ModuleBasedConfiguration;
import com.intellij.execution.configurations.RunConfigurationWithSuppressedDefaultDebugAction;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.RunConfigurationWithSuppressedDefaultRunAction;
import com.intellij.openapi.components.PathMacroManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizerUtil;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiManager;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.robovm.intellij.runconf.RoboVMCommandLineState;
import org.robovm.intellij.runconf.RoboVMConfigurationType;
import org.robovm.intellij.runconf.RoboVMSettings;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;

public class RoboVMRuntimeConfiguration extends ModuleBasedConfiguration<RoboVMSettings> implements RunConfigurationWithSuppressedDefaultDebugAction, RunConfigurationWithSuppressedDefaultRunAction {
    private JPanel panel;
    private JComboBox moduleComboBox, targetComboBox, sdkBox;
    private Module[] modules;
    private Project project;
    private String target, moduleName, sdk;
    private boolean shouldEraseSimulator = false;
    private boolean shouldUninstallApp = false;
    private Device[] devices;
    private static String NO_SDK_FOUND = "No SDKs found";



    SettingsEditor<RoboVMRuntimeConfiguration> roboVMRuntimeConfigurationSettingsEditor=  new SettingsEditor<RoboVMRuntimeConfiguration>() {
        @Override
        protected void resetEditorFrom(RoboVMRuntimeConfiguration roboVMRuntimeConfiguration) {
            devices = getDeviceTypes();

            targetComboBox.setModel(new DefaultComboBoxModel(getValidTargets(devices)));

            String[] moduleNames = new String[modules.length];
            for (int i=0;i<modules.length; i++) {
                moduleNames[i] = modules[i].getName();
            }

            if (moduleNames.length > 0) {
                moduleComboBox.setModel(new DefaultComboBoxModel(moduleNames));
            } else {
                moduleComboBox.setModel(new DefaultComboBoxModel(new String[]{project.getName()}));
            }

            sdkBox.setModel(new DefaultComboBoxModel(getValidSDKs(devices)));
            sdkBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String selectedItem = (String) sdkBox.getSelectedItem();
                    if (!selectedItem.equals(NO_SDK_FOUND)) {
                        filterDeviceComboBox(targetComboBox, selectedItem, devices);
                    }
                }
            });

            if (roboVMRuntimeConfiguration.getSdk() != null) {
                sdkBox.getModel().setSelectedItem(roboVMRuntimeConfiguration.getSdk());
            }
            if (roboVMRuntimeConfiguration.getTarget() != null) {
                targetComboBox.getModel().setSelectedItem(roboVMRuntimeConfiguration.getTarget());
            }

            moduleName = (String) moduleComboBox.getSelectedItem();
            target = (String) targetComboBox.getSelectedItem();
            sdk = (String) sdkBox.getSelectedItem();
        }



        @Override
        protected void applyEditorTo(RoboVMRuntimeConfiguration roboVMRuntimeConfiguration) throws ConfigurationException {
            roboVMRuntimeConfiguration.setModule((String) moduleComboBox.getSelectedItem());
            roboVMRuntimeConfiguration.setTarget((String) targetComboBox.getSelectedItem());
            roboVMRuntimeConfiguration.setSDK((String) sdkBox.getSelectedItem());
            sdk = (String) sdkBox.getSelectedItem();
        }

        @NotNull
        @Override
        protected JComponent createEditor() {
            return panel;
        }

    };

    public RoboVMRuntimeConfiguration(String name, Project project, RoboVMConfigurationType configurationType) {
        super(name, new RoboVMSettings(project), configurationType.getConfigurationFactories()[0]);
        this.project = project;
        modules = ModuleManager.getInstance(project).getModules();
    }




    @NotNull
    @Override
    public SettingsEditor<? extends RoboVMRuntimeConfiguration> getConfigurationEditor() {
        return roboVMRuntimeConfigurationSettingsEditor;
    }

    private void filterDeviceComboBox(JComboBox targetComboBox, String sdk, Device[] devices) {
        ArrayList<String> validDevices = new ArrayList<String>();
        for (Device device : devices) {
            if (device.getSdk().equals(sdk)) {
                validDevices.add(device.getDevice());
            }
        }
        targetComboBox.setModel(new DefaultComboBoxModel(validDevices.toArray(new String[validDevices.size()])));
    }

    private String[] getValidSDKs(Device[] devices) {
        ArrayList<String> sdks = new ArrayList<String>();
        if (devices == null || devices.length == 0) {
            return new String[] {
                    NO_SDK_FOUND
            };
        } else {
            for (Device device : devices) {
                if (!sdks.contains(device.getSdk())) {
                    sdks.add(device.getSdk());
                }
            }
            return sdks.toArray(new String[sdks.size()]);
        }
    }

    private String[] getValidTargets(Device[] devices) {
        if (devices == null || devices.length == 0) {
            return new String[] {
                "IPhoneSimulator",
                "IPadSimulator",
                "IOSDevice"
            };
        }
        else {
            ArrayList<String> deviceTargets = new ArrayList<String>();
            for (Device device : devices) {
                if (!deviceTargets.contains(device.getDevice())) {
                    deviceTargets.add(device.getDevice());
                }
            }
            return deviceTargets.toArray(new String[deviceTargets.size()]);
        }
    }

    private Device[] getDeviceTypes() {
        ArrayList<Device> devices = new ArrayList<Device>();
        try {
            Process p = Runtime.getRuntime().exec("xcrun simctl list");
            BufferedReader stdout = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            String sdk = null;
            while ((line = stdout.readLine()) != null) {
                if (line.startsWith("--")) {
                    sdk = getSdkFromLine(line);
                } else {
                    if ((line.contains("iPad") || line.contains("iPhone")) && !line.contains("unavailable")) {
                        String deviceId = line.substring(0, line.indexOf("("));
                        if (sdk != null && deviceId.length() > 0)
                            devices.add(new Device(sdk.trim(),deviceId.trim().replaceAll(" ","-"),
                                    line.substring(line.indexOf("(") + 1, line.indexOf(")"))));
                    }
                }
            }

            devices.add(new Device("-","iOS Device"));
            devices.add(new Device("-","Create IPA"));
        } catch (IOException e) {
           // e.printStackTrace();
            System.err.println("Could not run xcrun");
        }
        return devices.toArray(new Device[0]);
    }

    private String getSdkFromLine(String line) {
        String[] token = line.split(" ");
        return token[2];
    }

    private void setSDK(String selectedSDK) {
        this.sdk = selectedSDK;
    }

    private void setTarget(String targetName) {
        this.target = targetName;
    }

    private void setModule(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getSdk() {
        return sdk;
    }

    public String getTarget() {
        return target;
    }

    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {
        super.checkConfiguration();

        Module buildModule = ModuleManager.getInstance(getProject()).findModuleByName(moduleName);
        final ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(buildModule);
        final VirtualFile contentRoots[] = moduleRootManager.getContentRoots();

        final PsiDirectory projectBaseDirectory = PsiManager.getInstance(getProject()).findDirectory(contentRoots[0]);

        testFileExists(projectBaseDirectory, "gradlew", "Have you linked your gradle project?");
        testFileExists(projectBaseDirectory, "build.gradle", "");
        testFileExists(projectBaseDirectory, "robovm.xml", "");
        testFileExists(projectBaseDirectory, "robovm.properties", "");
        testFileExists(projectBaseDirectory, "Info.plist.xml", "");

    }

    private void testFileExists(PsiDirectory srcDirectory, String fileName, String errorString) throws RuntimeConfigurationException {
        PsiFile foundFile = srcDirectory.findFile(fileName);
        if (foundFile == null) {
            throw new RuntimeConfigurationException( "Required file " + fileName + " not found in " + srcDirectory.getName() + " : " + errorString);
        }
    }

    @Nullable
    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment executionEnvironment) throws ExecutionException {
        return new RoboVMCommandLineState(this, executionEnvironment);
    }

    private String getUUIForDevice(Device device) {
        Device[] devices = getDeviceTypes();
        for (Device dev : devices) {
           if (dev.getSdk().equals(device.getSdk()) && dev.getDevice().equals(device.getDevice())) {
               return dev.getUUID();
           }
        }
        return "";
    }

    @Override
    public void writeExternal(Element element) throws WriteExternalException {

        super.writeExternal(element);
        writeModule(element);

        PathMacroManager.getInstance(getProject()).collapsePathsRecursively(element);

        JDOMExternalizerUtil.writeField(element, "sdk", sdk);
        JDOMExternalizerUtil.writeField(element, "device", target);
        JDOMExternalizerUtil.writeField(element, "moduleName", moduleName);
    }

    @Override
    public Collection<Module> getValidModules() {
        return null;
    }

    @Override
    public void readExternal(Element element) throws InvalidDataException {
        PathMacroManager.getInstance(getProject()).expandPaths(element);
        super.readExternal(element);

        readModule(element);
        sdk = JDOMExternalizerUtil.readField(element,"sdk");
        target = JDOMExternalizerUtil.readField(element,"device");
        moduleName = JDOMExternalizerUtil.readField(element,"moduleName");
    }

    public String[] getGradleTask() {

        String task = null;

        if (target.contains("Phone")) {
            task = "launchIPhoneSimulator";
        } else if (target.contains("Pad")) {
            task = "launchIPadSimulator";
        } else if (target.contains("Device") ) {
            return new String[] { "launchIOSDevice" };
        } else if (target.contains("IPA")) {
            return new String[] { "createIPA" };
        }

        String sdkProperty = "-Probovm.sdk.version=" + sdk;
        String targetProperty = "-Probovm.device.name=" + target;

        return new String[] {
                task,
                sdkProperty,
                targetProperty
        };

    }


}
