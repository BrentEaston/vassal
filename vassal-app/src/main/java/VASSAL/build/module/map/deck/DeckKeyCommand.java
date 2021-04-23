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

import VASSAL.build.module.properties.PropertySource;
import VASSAL.counters.KeyCommand;

import java.util.List;

/**
 * An interface defining the core methods required by Deck Key Commands that add additional functionality
 * to Decks.
 */
public interface DeckKeyCommand  {

  /**
   * Add any HotKey KeyStroke Listeners used by this KeyCommand
   */
  void addKeyStrokeListener();

  /**
   * Remove any HotKey KeyStroke Listeners used by this KeyCommand
   */
  void removeKeyStrokeListener();

  /**
   * Is this DeckKeyCommand allowed to be executed currently
   * @return true if this Command is allowed.
   */
  boolean isEnabled();

  /**
   * Return a List of KeyCommands containing the Menu texts for this DeckKeyCommand
   * @return
   */
  List<KeyCommand> getKeyCommands();

  /**
   * Record a PropertySource. Most DeckKeyCommands do not need a PropertySource
   * Over-ride this method and save the source if needed.
   * @param source PropertySource
   */
  default void setPropertySource(PropertySource source) {
    return;
  }
}
