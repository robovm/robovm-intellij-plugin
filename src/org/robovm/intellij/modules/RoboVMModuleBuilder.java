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

import com.intellij.ide.util.projectWizard.JavaModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleBuilderListener;
import com.intellij.lang.Language;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Create a HelloWorld.java application with associated build.gradle file
 */
public class RoboVMModuleBuilder extends JavaModuleBuilder implements ModuleBuilderListener {

    public RoboVMModuleBuilder() {
        addListener(this);
    }



    @Override
    public void moduleCreated(@NotNull final Module module) {

        final ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(module);
        final VirtualFile sourceRoots[] = moduleRootManager.getSourceRoots();
        final VirtualFile contentRoots[] = moduleRootManager.getContentRoots();

        if (sourceRoots.length != 1 ) {
            return;
        }

        final PsiDirectory srcDirectory = PsiManager.getInstance(module.getProject()).findDirectory(sourceRoots[0]);
        final PsiDirectory projectBaseDirectory = PsiManager.getInstance(module.getProject()).findDirectory(contentRoots[0]);

        if (srcDirectory == null || srcDirectory.getParentDirectory() == null) {
            return;
        }


        WriteCommandAction<Void> action = new WriteCommandAction<Void>(module.getProject()) {
            @Override
            protected void run(@NotNull Result<Void> voidResult) throws Throwable {
                PsiDirectory mainDirectory = srcDirectory.createSubdirectory("main");
                mainDirectory.createSubdirectory("assets");
                PsiDirectory javaDir = mainDirectory.createSubdirectory("java");

                try {
                    /* create robovm.xml*/
                    PsiFile robovmXMLFile = PsiFileFactory.getInstance(javaDir.getProject())
                            .createFileFromText("robovm.xml",
                                    Language.findLanguageByID("TEXT"),
                                    templateToText("/templates/robovm.xml"));

                    /* create robovm.properties */
                    PsiFile robovmPropFile = PsiFileFactory.getInstance(javaDir.getProject())
                            .createFileFromText("robovm.properties",
                                    Language.findLanguageByID("TEXT"),
                                    templateToText("/templates/robovm.properties"));

                    /* create build.gradle */
                    PsiFile gradleBuildFile = PsiFileFactory.getInstance(srcDirectory.getProject())
                            .createFileFromText("build.gradle",
                                    Language.findLanguageByID("TEXT"),
                                    templateToText("/templates/build.gradle"));

                     /* create Info.plist.xml */
                    PsiFile infoList = PsiFileFactory.getInstance(srcDirectory.getProject())
                            .createFileFromText("Info.plist.xml",
                                    Language.findLanguageByID("TEXT"),
                                    templateToText("/templates/Info.plist.xml"));


                    projectBaseDirectory.add(gradleBuildFile);
                    projectBaseDirectory.add(robovmXMLFile);
                    projectBaseDirectory.add(robovmPropFile);
                    projectBaseDirectory.add(infoList);



                } catch (IOException e) {
                    e.printStackTrace();
                }
                srcDirectory.createSubdirectory("test");
            }
        };

        action.execute();
    }


    /**
     * Convert template file to a String
     * @param templateName
     * @return Template formatted as a string
     * @throws IOException
     */
    private String templateToText(String templateName) throws IOException {

        File file = new File(getClass().getClassLoader().getResource(templateName).getFile());
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        StringBuilder returnString = new StringBuilder();

        while ((line = reader.readLine()) != null) {
            returnString.append(line + "\n");
        }

        return returnString.toString();

    }

    @Override
    public ModuleType getModuleType() {
        return RoboVMModuleType.getInstance();
    }

    @Override
    public boolean isTemplateBased() {
        return true;
    }

}
