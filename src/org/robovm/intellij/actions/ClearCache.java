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
package org.robovm.intellij.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;


/**
 * Clear RoboVM Cache
 */
public class ClearCache extends AnAction {
    private static String ROBOVM_CACHE_DIR = File.separatorChar + ".robovm" + File.separatorChar + ".cache";

    public void actionPerformed(AnActionEvent e) {
        Project project = e.getData(PlatformDataKeys.PROJECT);
        int result = Messages.showYesNoCancelDialog(getEventProject(e),
                "This will delete " + System.getProperty("user.home") + ROBOVM_CACHE_DIR,
                "Delete RoboVM Cache?",
                "Yes",
                "No",
                "Cancel",
                Messages.getQuestionIcon());

        switch(result) {
            case Messages.OK:
                try {
                    FileUtils.deleteDirectory(new File(System.getProperty("user.home") + ROBOVM_CACHE_DIR));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                Messages.showMessageDialog(project, "Cache Cleared", "Information", Messages.getInformationIcon());
                break;
            default:
               break;
        }

    }
}
