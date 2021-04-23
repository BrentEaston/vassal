/*
 *
 * Copyright (c) 2021 by The Vassal Development Team
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License (LGPL) as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, copies are available
 * at http://www.opensource.org.
 */
package VASSAL.build.module.map.deck;

import VASSAL.build.GameModule;
import VASSAL.i18n.Resources;
import VASSAL.tools.NamedKeyStroke;

import org.apache.commons.lang3.ArrayUtils;

/**
 * A component that allows the number of cards to be drawn by the next drag to be set
 * This component is activated by right-click menu only, it has no HotKey listener
 */
public class DeckDrawMultipleCommand extends AbstractDeckKeyCommand {

  public static final String SELECT_COUNT = "SelectCount";

  public DeckDrawMultipleCommand(String name, String menuText) {
    super(name, menuText, NamedKeyStroke.NULL_KEYSTROKE);
  }

  @Override
  public void performAction() {
    if (isEnabled()) {
      final int preCount = getDeck().getDragCount();
      getDeck().promptForDragCount(); // Local action, no Command
      final int postCount = getDeck().getDragCount();
      if (preCount != postCount && !getReportFormat().getFormat().isEmpty()) {
        setReportProperty(SELECT_COUNT, String.valueOf(postCount));
        GameModule.getGameModule().sendAndLog(generateReport());
      }
    }
  }

  public static String getConfigureTypeName() {
    return Resources.getString("Editor.Deck.draw_multiple_command");
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public String[] getAttributeDescriptions() {
    return new String[] {
      Resources.getString("Editor.description_label"),
      Resources.getString("Editor.menu_command"),
      Resources.getString("Editor.report_format")
    };
  }

  @Override
  public String[] getAttributeNames() {
    return new String[] { DESCRIPTION, NAME, REPORT_FORMAT };
  }

  @Override
  public Class<?>[] getAttributeTypes() {
    return new Class[] {String.class, String.class, ReportFormatConfig.class};
  }

  public String[] getReportOptions() {
    return ArrayUtils.addAll(super.getReportOptions(), SELECT_COUNT);
  }
}
