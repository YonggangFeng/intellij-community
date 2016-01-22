/*
 * Copyright 2000-2016 JetBrains s.r.o.
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
package com.intellij.codeInsight.editorActions.moveLeftRight;

import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class XmlMoveLeftRightHandler extends MoveStatementLeftRightHandler {
  @Nullable
  @Override
  public PsiElement[] getElementListInContext(@NotNull PsiElement element) {
    while (element != null) {
      if (element instanceof XmlTag) {
        XmlAttribute[] attributes = ((XmlTag)element).getAttributes();
        return attributes.length > 1 ? attributes : null;
      }
      element = element.getParent();
    }
    return null;
  }
}
