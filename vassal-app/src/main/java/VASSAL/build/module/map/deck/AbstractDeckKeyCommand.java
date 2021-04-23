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

import VASSAL.build.AbstractConfigurable;
import VASSAL.build.AbstractFolder;
import VASSAL.build.AutoConfigurable;
import VASSAL.build.Buildable;
import VASSAL.build.GameModule;
import VASSAL.build.module.Chatter;
import VASSAL.build.module.Map;
import VASSAL.build.module.documentation.HelpFile;
import VASSAL.build.module.map.DrawPile;
import VASSAL.command.Command;
import VASSAL.configure.Configurer;
import VASSAL.configure.NamedHotKeyConfigurer;
import VASSAL.configure.PlayerIdFormattedStringConfigurer;
import VASSAL.counters.Deck;
import VASSAL.counters.KeyCommand;
import VASSAL.i18n.Resources;
import VASSAL.i18n.TranslatableConfigurerFactory;
import VASSAL.tools.FormattedString;
import VASSAL.tools.NamedKeyStroke;
import VASSAL.tools.NamedKeyStrokeListener;

import java.awt.event.ActionEvent;
import java.util.List;

/**
 * Base class for components that can be added to Decks to provide additional functionality.
 * The provided actions can be initiated manually via right-click Key Commands, or by registered
 * Hot Keys.
 *
 * This class provides configuration of shared attributes, logging and Hot Key Listener functionality.
 */
public abstract class AbstractDeckKeyCommand extends AbstractConfigurable implements DeckKeyCommand {

  public static final String NAME = "name";
  public static final String DESCRIPTION = "description";
  public static final String MENU_TEXT = "menuText";
  public static final String HOTKEY = "hotkey";
  public static final String REPORT_FORMAT = "reportFormat";

  public static final String REPORT_DECK_NAME = "deckName";
  public static final String REPORT_KEY_COMMAND_NAME = "name";
  public static final String REPORT_MENU = "menuOption";
  public static final String REPORT_DESCRIPTION = "description";

  private final FormattedString reportFormat = new FormattedString();
  private NamedKeyStroke hotkey = NamedKeyStroke.NULL_KEYSTROKE;
  private DrawPile drawPile;
  private NamedKeyStrokeListener listener;
  private List<KeyCommand> keyCommands;
  private String menuText;
  private String description = "";

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public FormattedString getReportFormat() {
    return reportFormat;
  }

  public NamedKeyStroke getHotkey() {
    return hotkey;
  }

  public void setHotkey(NamedKeyStroke hotkey) {
    this.hotkey = hotkey;
  }


  public AbstractDeckKeyCommand(String name, String menuText, NamedKeyStroke key) {
    this(name, menuText, "", key);
  }

  public AbstractDeckKeyCommand(String name, String menuText, String format, NamedKeyStroke key) {
    setConfigureName(name);
    this.menuText = menuText;
    reportFormat.setFormat(format);
    hotkey = key;
  }

  @Override
  public String getConfigureName() {
    return super.getConfigureName();
  }

  @Override
  public List<KeyCommand> getKeyCommands() {
    if (!isEnabled()) {
      return null;
    }
    if (keyCommands == null && ! menuText.isEmpty()) {
      keyCommands.add(new KeyCommand(menuText, hotkey.getKeyStroke()) {
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
          performAction();
        }
      });
    }
    return keyCommands;
  }

  public Deck getDeck() {
    return drawPile.getDeck();
  }

  @Override
  public void addKeyStrokeListener() {
    if (listener == null) {
      listener = new NamedKeyStrokeListener(e -> performAction());
      listener.setKeyStroke(hotkey);
      GameModule.getGameModule().addKeyStrokeListener(listener);
    }
  }

  @Override
  public void removeKeyStrokeListener() {
    if (listener != null) {
      GameModule.getGameModule().removeKeyStrokeListener(listener);
      listener = null;
    }
  }

  @Override
  public String[] getAttributeNames() {
    return new String[] { NAME, DESCRIPTION, MENU_TEXT, HOTKEY, REPORT_FORMAT };
  }

  @Override
  public void setAttribute(String key, Object value) {
    if (NAME.equals(key)) {
      setConfigureName((String) value);
    }
    else if (DESCRIPTION.equals(key)) {
      description = value == null ? "" : value.toString();
    }
    else if (MENU_TEXT.equals(key)) {
      menuText = value == null ? "" : value.toString();
    }
    else if (REPORT_FORMAT.equals(key)) {
      reportFormat.setFormat(value == null ? "" : value.toString());
    }
    else if (HOTKEY.equals(key)) {
      if (value instanceof String) {
        value = NamedHotKeyConfigurer.decode((String) value);
      }
      hotkey = (NamedKeyStroke) value;
    }
  }

  @Override
  public String getAttributeValueString(String key) {
    if (NAME.equals(key)) {
      return getConfigureName();
    }
    else if (DESCRIPTION.equals(key)) {
      return description;
    }
    else if (MENU_TEXT.equals(key)) {
      return menuText;
    }
    else if (REPORT_FORMAT.equals(key)) {
      return reportFormat.getFormat();
    }
    else if (HOTKEY.equals(key)) {
      return NamedHotKeyConfigurer.encode(hotkey);
    }
    return null;
  }

  @Override
  public String[] getAttributeDescriptions() {
    return new String[] {
      Resources.getString("Editor.name_label"),
      Resources.getString("Editor.description_label"),
      Resources.getString("Editor.menu_command"),
      Resources.getString("Editor.hotkey_label"),
      Resources.getString("Editor.report_format")
    };
  }

  @Override
  public Class<?>[] getAttributeTypes() {
    return new Class[] {String.class, String.class, String.class, NamedKeyStroke.class, ReportFormatConfig.class};
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
    drawPile.removeDeckKeyCommand(this);
  }

  @Override
  public HelpFile getHelpFile() {
    return null;
  }

  @Override
  public Class[] getAllowableConfigureComponents() {
    return new Class[0];
  }

  public static class ReportFormatConfig implements TranslatableConfigurerFactory {
    @Override
    public Configurer getConfigurer(AutoConfigurable c, String key, String name) {
      return new PlayerIdFormattedStringConfigurer(key, name, ((AbstractDeckKeyCommand) c).getReportOptions());
    }
  }

  public String[] getReportOptions() {
    return new String[] { REPORT_DECK_NAME };
  }

  public void setReportProperty(String key, String value) {
    reportFormat.setProperty(key, value);
  }

  public Command generateReport() {
    reportFormat.setProperty(REPORT_DECK_NAME, getDeck().getDeckName());
    reportFormat.setProperty(DrawPile.DECK_NAME, getDeck().getDeckName()); // Compatibility
    reportFormat.setProperty(REPORT_MENU, menuText);
    reportFormat.setProperty(DrawPile.COMMAND_NAME, menuText); // Compatibility
    reportFormat.setProperty(REPORT_DESCRIPTION, description);
    reportFormat.setProperty(REPORT_KEY_COMMAND_NAME, getConfigureName());
    reportFormat.setDefaultProperties(getDeck());
    final GameModule mod = GameModule.getGameModule();
    final String report = getReportFormat().getLocalizedText(); //$NON-NLS-1$
    if (! report.isEmpty()) {
      Command c = new Chatter.DisplayText(mod.getChatter(), "* " + report); //$NON-NLS-1$
      c.execute();
      return c;
    }

    return null;
  }

  /**
   * Each subclass must implement performAction to
   * a) Check if it is valid to perform that action
   * b) perform and log the action.
   */
  public abstract void performAction();

  /**
   * Log an action that encompasses the execution of the Key Command
   * Display and add a report if Reporting is not paused.
   * Log the action & report to the log file and other clients
   * Repaint the map in case the Decks visual has changed
   *
   * @param action Deck Key Command
   */
  public void logAction(final Command action) {
    Command c = action;
    if (Map.isChangeReportingEnabled()) {
      c = c.append(generateReport());
    }
    GameModule.getGameModule().sendAndLog(c);
    getDeck().repaintMap();
  }

  @Override
  public List<String> getExpressionList() {
    return super.getExpressionList();
  }

  @Override
  public List<String> getFormattedStringList() {
    return List.of(reportFormat.getFormat());
  }

  @Override
  public List<String> getMenuTextList() {
    return List.of(menuText);
  }

  @Override
  public List<NamedKeyStroke> getNamedKeyStrokeList() {
    return List.of(NamedHotKeyConfigurer.decode(getAttributeValueString(HOTKEY)));
  }

  @Override
  public List<String> getPropertyList() {
    return super.getPropertyList();
  }
}
