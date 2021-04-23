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

import VASSAL.build.AutoConfigurable;
import VASSAL.build.module.map.DrawPile;
import VASSAL.configure.Configurer;
import VASSAL.configure.ConfigurerFactory;
import VASSAL.configure.DeckSelectionConfigurer;
import VASSAL.counters.Deck;
import VASSAL.i18n.Resources;
import VASSAL.tools.NamedKeyStroke;

import org.apache.commons.lang3.ArrayUtils;

/**
 * Component to allow the entire contents of a Deck to be sent to another Deck.
 */
public class DeckSendCommand extends AbstractDeckKeyCommand {

  public static final String DECK_NAME = "deckName"; //NON-NLS

  private String sendToDeck;

  public DeckSendCommand(String name, String menuText, String format, NamedKeyStroke key) {
    super(name, menuText, format, key);
  }

  @Override
  public void performAction() {
    if (isEnabled()) {
      final DrawPile target = DrawPile.findDrawPile(sendToDeck);
      if (target != null) {
        logAction(getDeck().sendToDeck(target));
      }
    }
  }

  public static String getConfigureTypeName() {
    return Resources.getString("Editor.Deck.send_command");
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public void setAttribute(String key, Object value) {
    if (DECK_NAME.equals(key)) {
      sendToDeck = (String) value;
    }
    super.setAttribute(key, value);
  }

  @Override
  public String getAttributeValueString(String key) {
    if (DECK_NAME.equals(key)) {
      return sendToDeck;
    }
    return super.getAttributeValueString(key);
  }

  @Override
  public String[] getAttributeDescriptions() {
    return ArrayUtils.addAll(super.getAttributeDescriptions(),
      Resources.getString("Editor.Deck.destination_deck"));
  }

  @Override
  public String[] getAttributeNames() {
    return ArrayUtils.addAll(super.getAttributeNames(), DECK_NAME);
  }

  @Override
  public Class<?>[] getAttributeTypes() {
    return ArrayUtils.addAll(super.getAttributeTypes(), DeckConfig.class);
  }

  public static class DeckConfig implements ConfigurerFactory {

    @Override
    public Configurer getConfigurer(AutoConfigurable c, String key, String name) {
      final DeckSelectionConfigurer config =  new DeckSelectionConfigurer(name, null);
      final Deck dest = ((DeckSendCommand) c).getDeck();
      config.setValue(dest == null ? "" : dest.getDeckName());
      return config;
    }
  }
}
