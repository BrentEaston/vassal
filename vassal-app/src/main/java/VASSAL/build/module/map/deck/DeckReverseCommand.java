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
import VASSAL.build.module.Map;
import VASSAL.command.Command;
import VASSAL.i18n.Resources;
import VASSAL.tools.NamedKeyStroke;

/**
 * A Component that adds functionality to reverse the order of the cards in a Deck
 */
public class DeckReverseCommand extends AbstractDeckKeyCommand {

  public DeckReverseCommand(String name, String menuText, String format, NamedKeyStroke key) {
    super(name, menuText, format, key);
  }

  @Override
  public void performAction() {
    if (isEnabled()) {
      logAction(getDeck().reverse());
    }
  }

  public static String getConfigureTypeName() {
    return Resources.getString("Editor.Deck.reverse_command");
  }

  @Override
  public boolean isEnabled() {
    return true;
  }



}
