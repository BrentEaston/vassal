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

import VASSAL.build.AbstractFolder;
import VASSAL.build.AbstractToolbarItem;
import VASSAL.build.Buildable;
import VASSAL.build.GameModule;
import VASSAL.build.module.Chatter;
import VASSAL.build.module.map.DeckGlobalKeyCommand;
import VASSAL.build.module.map.DrawPile;
import VASSAL.build.module.map.MassKeyCommand;
import VASSAL.build.module.properties.PropertySource;
import VASSAL.command.Command;
import VASSAL.command.NullCommand;
import VASSAL.configure.NamedHotKeyConfigurer;
import VASSAL.configure.PropertyExpression;
import VASSAL.counters.Deck;
import VASSAL.counters.DeckVisitorDispatcher;
import VASSAL.counters.GlobalCommand;
import VASSAL.counters.GlobalCommandTarget;
import VASSAL.counters.KeyCommand;
import VASSAL.counters.PieceFilter;
import VASSAL.i18n.Resources;
import VASSAL.tools.NamedKeyStroke;
import VASSAL.tools.NamedKeyStrokeListener;
import VASSAL.tools.RecursionLimiter;

import java.awt.event.ActionEvent;
import java.util.List;

/**
 * A Component that adds Global Key Commands to a Deck that only affect the Deck Contents.
 * This class replaces {@link @DeckGlobalKeyCommand}
 */
public class DeckGKCommand extends MassKeyCommand implements DeckKeyCommand {

  public static final String DESCRIPTION = "description";
  public static final String MENU_TEXT = "menuText";

  private DrawPile drawPile;
  private String description;
  private String menuText;
  private NamedKeyStrokeListener listener;

  // Convert a DeckGlobalKeyCommand
  public DeckGKCommand(DeckGlobalKeyCommand dkgc) {
    globalCommand = new DeckGlobalCommand(this);
    final String gkcName = dkgc.getAttributeValueString(NAME_PROPERTY);
    setConfigureName(gkcName);
    menuText = gkcName;
    description = gkcName;
    setAttribute(PROPERTIES_FILTER, dkgc.getAttributeValueString(PROPERTIES_FILTER));
    setAttribute(HOTKEY, NamedKeyStroke.NULL_KEYSTROKE);
    setAttribute(KEY_COMMAND, dkgc.getAttributeValueString(KEY_COMMAND));
    setAttribute(DECK_COUNT, dkgc.getAttributeValueString(DECK_COUNT));
    setAttribute(REPORT_FORMAT, dkgc.getAttributeValueString(REPORT_FORMAT));
  }

  /**
   * @return Our type of Global Key Command (overrides the one from Mass Key Command). Affects what configurer options are shown.
   * In particular no "Fast Match" parameters are shown for Deck GKCs.
   */
  @Override
  public GlobalCommandTarget.GKCtype getGKCtype() {
    return GlobalCommandTarget.GKCtype.DECK;
  }

  public Deck getDeck() {
    return drawPile.getDeck();
  }

  public void performAction() {
    if (isEnabled()) {
      GameModule.getGameModule().sendAndLog(((DeckGlobalCommand) globalCommand).apply(getDeck(), getFilter()));
    }
  }

  public static String getConfigureTypeName() {
    return Resources.getString("Editor.DeckGlobalKeyCommand.component_type"); //$NON-NLS-1$
  }


  @Override
  public void addKeyStrokeListener() {
    if (listener == null) {
      listener = new NamedKeyStrokeListener(e -> performAction());
      listener.setKeyStroke(NamedHotKeyConfigurer.decode(getAttributeValueString(HOTKEY)));
    }
    GameModule.getGameModule().addKeyStrokeListener(listener);

  }

  @Override
  public void removeKeyStrokeListener() {
    if (listener != null) {
      GameModule.getGameModule().removeKeyStrokeListener(listener);
      listener = null;
    }
  }

  @Override
  public void addTo(Buildable parent) {
    if (parent instanceof AbstractFolder) {
      parent = ((AbstractFolder) parent).getNonFolderAncestor();
    }
    drawPile = (DrawPile) parent;
    drawPile.addDeckKeyCommand(this);
  }

  @Override
  public void removeFrom(Buildable parent) {
    if (parent instanceof AbstractFolder) {
      parent = ((AbstractFolder) parent).getNonFolderAncestor();
    }
    ((DrawPile) parent).removeDeckKeyCommand(this);
  }

  /**
   * Since we also limit application of a Deck Global Key command to a specified number of pieces in the
   * Deck, a null match expression should match all pieces, not reject them all.
   */
  @Override
  public PieceFilter getFilter() {
    if (propertiesFilter == null || propertiesFilter.getExpression() == null || propertiesFilter.getExpression().length() == 0) {
      return null;
    }
    return super.getFilter();
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public List<KeyCommand> getKeyCommands() {
    return List.of(new DKCommand(menuText));
  }

  class DKCommand extends KeyCommand {
    private static final long serialVersionUID = 1L;
    public DKCommand(String name) {
      super(name, null);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
      performAction();
    }
  }

  @Override
  public void setAttribute(String key, Object value) {
    if (AbstractToolbarItem.NAME.equals(key)) {
      setConfigureName((String) value);
    }
    else if (DESCRIPTION.equals(key)) {
      description = value == null ? "" : (String) value;
    }
    else if (MENU_TEXT.equals(key)) {
      menuText = value == null ? "" : (String) value;
    }
    else {
      super.setAttribute(key, value);
    }
  }

  @Override
  public String getAttributeValueString(String key) {
    if (AbstractToolbarItem.NAME.equals(key)) {
      return getConfigureName();
    }
    else if (DESCRIPTION.equals(key)) {
      return description;
    }
    else if (MENU_TEXT.equals(key)) {
      return menuText;
    }
    return super.getAttributeValueString(key);
  }

  @Override
  public String[] getAttributeDescriptions() {
    return new String[] {
      Resources.getString("Editor.name_label"),
      Resources.getString("Editor.description_label"),
      Resources.getString("Editor.menu_command"),
      Resources.getString("Editor.hotkey_label"),
      Resources.getString("Editor.GlobalKeyCommand.global_key_command"),
      Resources.getString("Editor.DeckGlobalKeyCommand.matching_properties"),
      Resources.getString("Editor.DeckGlobalKeyCommand.affects"),
      Resources.getString("Editor.report_format")
    };
  }

  @Override
  public String[] getAttributeNames() {
    return new String[] {
      AbstractToolbarItem.NAME,
      DESCRIPTION,
      MENU_TEXT,
      HOTKEY,
      KEY_COMMAND,
      PROPERTIES_FILTER,
      DECK_COUNT,
      REPORT_FORMAT
    };
  }

  @Override
  public Class<?>[] getAttributeTypes() {
    return new Class[] {
      String.class,
      String.class,
      String.class,
      NamedKeyStroke.class,
      NamedKeyStroke.class,
      PropertyExpression.class,           // Match properties
      DeckPolicyConfig.class,             // Apply to pieces in deck
      ReportFormatConfig.class
    };
  }


  /**
   * {@link VASSAL.search.SearchTarget}
   * @return a list of the Configurables string/expression fields if any (for search)
   */
  @Override
  public List<String> getExpressionList() {
    return List.of(propertiesFilter.getExpression());
  }

  @Override
  public List<String> getMenuTextList() {
    return List.of(menuText);
  }

  public static class DeckGlobalCommand extends GlobalCommand {

    public DeckGlobalCommand(RecursionLimiter.Loopable l) {
      super(l);
    }

    public Command apply(Deck d, PieceFilter filter) {
      final String reportText = reportFormat.getText(source);
      Command c;
      if (reportText.length() > 0) {
        c = new Chatter.DisplayText(GameModule.getGameModule().getChatter(), "*" + reportText);
        c.execute();
      }
      else {
        c = new NullCommand();
      }

      final Visitor visitor = new Visitor(c, filter, keyStroke);
      final DeckVisitorDispatcher dispatcher = new DeckVisitorDispatcher(visitor);

      dispatcher.accept(d);
      visitor.getTracker().repaint();

      c = visitor.getCommand();
      return c;
    }
  }
}
