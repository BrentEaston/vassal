/*
 *
 * Copyright (c) 2021 by vassalengine.org, Brian Reynolds
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

package VASSAL.build.module.folder;

import VASSAL.build.AbstractFolder;
import VASSAL.build.module.map.DeckGlobalKeyCommand;
import VASSAL.build.module.map.deck.DeckDrawMultipleCommand;
import VASSAL.build.module.map.deck.DeckGKCommand;
import VASSAL.build.module.map.deck.DeckReverseCommand;
import VASSAL.build.module.map.deck.DeckSendCommand;
import VASSAL.build.module.map.deck.DeckSortCommand;
import VASSAL.build.widget.CardSlot;

public class DeckSubFolder extends AbstractFolder {
  @Override
  public Class<?>[] getAllowableConfigureComponents() {
    return new Class<?>[] {
      this.getClass(),
      CardSlot.class,
      DeckGKCommand.class,
      DeckDrawMultipleCommand.class,
      DeckReverseCommand.class,
      DeckSendCommand.class,
      DeckSortCommand.class };
  }
}
