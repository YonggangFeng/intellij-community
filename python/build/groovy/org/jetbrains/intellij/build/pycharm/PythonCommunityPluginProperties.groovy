/*
 * Copyright 2000-2017 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jetbrains.intellij.build.pycharm

import org.jetbrains.intellij.build.CommunityRepositoryModules
import org.jetbrains.intellij.build.python.PythonCommunityPluginModules

/**
 * @author vlan
 */
class PythonCommunityPluginProperties extends PythonPluginPropertiesBase {
  PythonCommunityPluginProperties() {
    super()
    productCode = "PC"
    platformPrefix = "PyCharmCore"
    applicationInfoModule = "intellij.pycharm.community.resources"
    productLayout.pluginModulesToPublish = [PythonCommunityPluginModules.PYTHON_COMMUNITY_PLUGIN_MODULE]

    productLayout.allNonTrivialPlugins = CommunityRepositoryModules.COMMUNITY_REPOSITORY_PLUGINS + [
      PythonCommunityPluginModules.pythonCommunityPluginLayout()
    ]
  }
}
