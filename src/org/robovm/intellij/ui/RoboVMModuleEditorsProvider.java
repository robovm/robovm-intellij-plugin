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

package org.robovm.intellij.ui;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleConfigurationEditor;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.roots.ui.configuration.DefaultModuleConfigurationEditorFactory;
import com.intellij.openapi.roots.ui.configuration.ModuleConfigurationEditorProvider;
import com.intellij.openapi.roots.ui.configuration.ModuleConfigurationState;
import org.robovm.intellij.modules.RoboVMModuleType;

import java.util.ArrayList;
import java.util.List;

public class RoboVMModuleEditorsProvider implements ModuleConfigurationEditorProvider {
    @Override
    public ModuleConfigurationEditor[] createEditors(ModuleConfigurationState moduleConfigurationState) {

        final Module module = moduleConfigurationState.getRootModel().getModule();
        final ModuleType moduleType = ModuleType.get(module);

        if (!(moduleType instanceof RoboVMModuleType)) {
            return ModuleConfigurationEditor.EMPTY;
        }

        final DefaultModuleConfigurationEditorFactory editorFactory = DefaultModuleConfigurationEditorFactory.getInstance();

        List<ModuleConfigurationEditor> editors = new ArrayList<ModuleConfigurationEditor>();
        editors.add(editorFactory.createModuleContentRootsEditor(moduleConfigurationState));
        editors.add(editorFactory.createOutputEditor(moduleConfigurationState));
        editors.add(editorFactory.createClasspathEditor(moduleConfigurationState));

        return editors.toArray(new ModuleConfigurationEditor[editors.size()]);
    }
}
