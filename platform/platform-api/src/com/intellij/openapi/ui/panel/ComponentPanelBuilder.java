// Copyright 2000-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.openapi.ui.panel;

import com.intellij.openapi.ui.ComponentWithBrowseButton;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.ContextHelpLabel;
import com.intellij.ui.Gray;
import com.intellij.ui.TextComponent;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.panels.NonOpaquePanel;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class ComponentPanelBuilder implements GridBagPanelBuilder {

  private final JComponent myComponent;

  private String myLabelText;
  private boolean myLabelOnTop;
  private String myCommentText;
  private boolean myCommentBelow = true;
  private String myHTDescription;
  private String myHTLinkText;
  private Runnable myHTAction;
  private JComponent myTopRightComponent;
  private boolean valid = true;

  public ComponentPanelBuilder(JComponent component) {
    myComponent = component;
  }

  /**
   * @param labelText text for the label.
   * @return <code>this</code>
   */
  public ComponentPanelBuilder withLabel(@NotNull String labelText) {
    myLabelText = labelText;
    return this;
  }

  /**
   * Move label on top of the owner component. Default position is on the left of the owner component.
   *
   * @return <code>this</code>
   */
  public ComponentPanelBuilder moveLabelOnTop() {
    myLabelOnTop = true;
    valid = StringUtil.isNotEmpty(myCommentText) && StringUtil.isEmpty(myHTDescription);
    return this;
  }

  /**
   * @param comment help context styled text written below the owner component.
   * @return <code>this</code>
   */
  public ComponentPanelBuilder withComment(@NotNull String comment) {
    myCommentText = comment;
    valid = StringUtil.isNotEmpty(comment) && StringUtil.isEmpty(myHTDescription) &&
              (myLabelOnTop || myTopRightComponent == null);
    return this;
  }

  public ComponentPanelBuilder withTopRightComponent(@NotNull JComponent topRightComponent) {
    myTopRightComponent = topRightComponent;
    valid = StringUtil.isNotEmpty(myCommentText) && StringUtil.isEmpty(myHTDescription) && myLabelOnTop;
    return this;
  }

  /**
   * Move comment to the right of the owner component. Default position is below the owner component.
   *
   * @return <code>this</code>
   */
  public ComponentPanelBuilder moveCommentRight() {
    myCommentBelow = false;
    return this;
  }

  /**
   * Enables the help tooltip icon on the right of the owner component and sets the description text for the tooltip.
   *
   * @param description help tooltip description.
   * @return <code>this</code>
   */
  public ComponentPanelBuilder withTooltip(@NotNull String description) {
    myHTDescription = description;
    valid = StringUtil.isEmpty(myCommentText) && StringUtil.isNotEmpty(description) &&
            (myLabelOnTop || myTopRightComponent == null);
    return this;
  }

  /**
   * Sets optional help tooltip link and link action.
   *
   * @param linkText help tooltip link text.
   *
   * @param action help tooltip link action.
   *
   * @return <code>this</code>
   */
  public ComponentPanelBuilder withTooltipLink(@NotNull String linkText, @NotNull Runnable action) {
    myHTLinkText = linkText;
    myHTAction = action;
    return this;
  }

  @Override
  @NotNull
  public JPanel createPanel() {
    JPanel panel = new NonOpaquePanel(new GridBagLayout());
    GridBagConstraints gc = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL,
                                                   null, 0, 0);
    addToPanel(panel, gc);
    return panel;
  }

  @Override
  public boolean constrainsValid() {
    return valid;
  }

  @Override
  public int gridWidth() {
    return 2;
  }

  @Override
  public void addToPanel(JPanel panel, GridBagConstraints gc) {
    if (constrainsValid()) {
      new ComponentPanelImpl().addToPanel(panel, gc);
    }
  }

  private Border getCommentBorder() {
    if (StringUtil.isNotEmpty(myCommentText)) {
      boolean isMacDefault = UIUtil.isUnderDefaultMacTheme();
      boolean isWin10 = UIUtil.isUnderWin10LookAndFeel();

      if (myCommentBelow) {
        int top = 8, left = 2, bottom = 0;

        if (myComponent instanceof JRadioButton || myComponent instanceof JCheckBox) {
          top = 0;
          left = isMacDefault ? 27 : isWin10 ? 17 : 23;
          bottom = isWin10 ? 10 : isMacDefault ? 8 : 9;
        }
        else if (myComponent instanceof JTextField || myComponent instanceof TextComponent ||
                 myComponent instanceof JComboBox || myComponent instanceof ComponentWithBrowseButton) {
          top = isWin10 ? 3 : 4;
          left = isWin10 ? 2 : isMacDefault ? 5 : 4;
          bottom = isWin10 ? 10 : isMacDefault ? 8 : 9;
        }
        else if (myComponent instanceof JButton) {
          top = isWin10 ? 2 : 4;
          left = isWin10 ? 2 : isMacDefault ? 5 : 4;
          bottom = 0;
        }

        return JBUI.Borders.empty(top, left, bottom, 0);
      } else {
        int left = 14;

        if (myComponent instanceof JRadioButton || myComponent instanceof JCheckBox) {
          left = isMacDefault ? 8 : 13;
        }
        else if (myComponent instanceof JTextField || myComponent instanceof TextComponent ||
                 myComponent instanceof JComboBox || myComponent instanceof ComponentWithBrowseButton) {
          left = isMacDefault ? 13 : 14;
        }
        return JBUI.Borders.emptyLeft(left);
      }
    } else {
      return JBUI.Borders.empty();
    }
  }

  private class ComponentPanelImpl extends ComponentPanel {
    private final JLabel label;
    private final JBLabel comment;

    private ComponentPanelImpl() {
      if ((StringUtil.isNotEmpty(myLabelText))) {
        label = new JLabel();
        LabeledComponent.TextWithMnemonic.fromTextWithMnemonic(myLabelText).setToLabel(label);
        label.setLabelFor(myComponent);
      } else {
        label = new JLabel("");
      }

      comment = new JBLabel("").setCopyable(true).setAllowAutoWrapping(true);
      comment.setVerticalTextPosition(SwingConstants.TOP);
      comment.setFocusable(false);
      comment.setForeground(Gray.x78);
      comment.setBorder(getCommentBorder());
      setCommentTextImpl(myCommentText);

      if (SystemInfo.isMac) {
        Font font = comment.getFont();
        float size = font.getSize2D();
        Font smallFont = font.deriveFont(size - 2.0f);
        comment.setFont(smallFont);
      }
    }

    @Override
    public String getCommentText() {
      return myCommentText;
    }

    @Override
    public void setCommentText(String commentText) {
      if (!StringUtil.equals(myCommentText, commentText)) {
        myCommentText = commentText;
        setCommentTextImpl(commentText);
      }
    }

    private void setCommentTextImpl(String commentText) {
      if (commentText != null) {
        if (commentText.length() > 70 && myCommentBelow) {
          int width = comment.getFontMetrics(comment.getFont()).stringWidth(commentText.substring(0, 70));
          comment.setText(String.format("<html><div width=%d>%s</div></html>", width, commentText));
        } else {
          comment.setText(String.format("<html><div>%s</div></html>", commentText));
        }
      }
    }

    private void addToPanel(JPanel panel, GridBagConstraints gc) {
      gc.gridx = 0;
      gc.gridwidth = 1;
      gc.weightx = 0.0;
      gc.anchor = GridBagConstraints.LINE_START;

      if (StringUtil.isNotEmpty(myLabelText)) {
        if (myLabelOnTop) {
          gc.insets = JBUI.insetsBottom(4);
          gc.gridx++;

          JPanel topPanel = new JPanel();
          topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
          topPanel.add(label);

          if (myTopRightComponent != null) {
            topPanel.add(new Box.Filler(JBUI.size(UIUtil.DEFAULT_HGAP, 0),
                                        JBUI.size(UIUtil.DEFAULT_HGAP, 0),
                                        JBUI.size(Integer.MAX_VALUE)));
            topPanel.add(myTopRightComponent);
          }

          panel.add(topPanel, gc);
          gc.gridy++;
        } else {
          gc.insets = JBUI.insetsRight(8);
          panel.add(label, gc);
        }
      }

      gc.gridx += myLabelOnTop ? 0 : 1;
      gc.weightx = 1.0;
      gc.insets = JBUI.emptyInsets();

      JPanel componentPanel = new NonOpaquePanel();
      componentPanel.setLayout(new BoxLayout(componentPanel, BoxLayout.X_AXIS));
      componentPanel.add(myComponent);

      myComponent.putClientProperty(DECORATED_PANEL_PROPERTY, this);

      if (StringUtil.isNotEmpty(myHTDescription)) {
        ContextHelpLabel lbl = StringUtil.isNotEmpty(myHTLinkText) && myHTAction != null ?
                               ContextHelpLabel.createWithLink(null, myHTDescription, myHTLinkText, myHTAction) :
                               ContextHelpLabel.create(myHTDescription);
        componentPanel.add(Box.createRigidArea(JBUI.size(7, 0)));
        componentPanel.add(lbl);
      }
      else if (!myCommentBelow) {
        comment.setBorder(getCommentBorder());
        componentPanel.add(comment);
      }

      panel.add(componentPanel, gc);

      if (myCommentBelow) {
        gc.gridx = 1;
        gc.gridy++;
        gc.weightx = 0.0;
        gc.anchor = GridBagConstraints.NORTHWEST;
        gc.insets = JBUI.emptyInsets();

        comment.setBorder(getCommentBorder());
        panel.add(comment, gc);
      }

      gc.gridy++;
    }
  }
}
