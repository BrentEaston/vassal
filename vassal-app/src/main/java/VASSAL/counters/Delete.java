/*
 *
 * Copyright (c) 2003 by Rodney Kinney
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

package VASSAL.counters;

import VASSAL.i18n.Resources;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.Arrays;
import java.util.List;

import java.util.Objects;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import VASSAL.build.GameModule;
import VASSAL.build.module.documentation.HelpFile;
import VASSAL.command.Command;
import VASSAL.command.RemovePiece;
import VASSAL.configure.NamedHotKeyConfigurer;
import VASSAL.configure.StringConfigurer;
import VASSAL.i18n.PieceI18nData;
import VASSAL.i18n.TranslatablePiece;
import VASSAL.tools.NamedKeyStroke;
import VASSAL.tools.SequenceEncoder;

/**
 * This trait adds a command that creates a duplicate of the selected Gamepiece
 */
public class Delete extends Decorator implements TranslatablePiece {
  public static final String ID = "delete;"; // NON-NLS
  protected KeyCommand[] keyCommands;
  protected KeyCommand deleteCommand;
  protected String commandName;
  protected NamedKeyStroke key;

  public Delete() {
    this(ID + Resources.getString("Editor.Delete.delete") + ";D", null); // NON-NLS
  }

  public Delete(String type, GamePiece inner) {
    mySetType(type);
    setInner(inner);
  }

  @Override
  public void mySetType(String type) {
    type = type.substring(ID.length());
    SequenceEncoder.Decoder st = new SequenceEncoder.Decoder(type, ';');
    commandName = st.nextToken();
    key = st.nextNamedKeyStroke('D');
    keyCommands = null;
  }

  @Override
  public String myGetType() {
    SequenceEncoder se = new SequenceEncoder(';');
    se.append(commandName).append(key);
    return ID + se.getValue();
  }

  @Override
  protected KeyCommand[] myGetKeyCommands() {
    if (keyCommands == null) {
      deleteCommand = new KeyCommand(commandName, key, Decorator.getOutermost(this), this);
      if (commandName.length() > 0 && key != null && ! key.isNull()) {
        keyCommands = new KeyCommand[]{deleteCommand};
      }
      else {
        keyCommands = new KeyCommand[0];
      }
    }
    deleteCommand.setEnabled(getMap() != null);
    return keyCommands;
  }

  @Override
  public String myGetState() {
    return "";
  }

  @Override
  public Command myKeyEvent(KeyStroke stroke) {
    Command c = null;
    myGetKeyCommands();
    if (deleteCommand.matches(stroke)) {
      GamePiece outer = Decorator.getOutermost(this);
      if (getParent() != null) {
        GamePiece next = getParent().getPieceBeneath(outer);
        if (next == null)
          next = getParent().getPieceAbove(outer);
        if (next != null) {
          final GamePiece selected = next;
          Runnable runnable = () -> {
            // Don't select if the next piece has itself been deleted
            if (GameModule.getGameModule().getGameState().getPieceForId(selected.getId()) != null) {
              KeyBuffer.getBuffer().add(selected);
            }
          };
          SwingUtilities.invokeLater(runnable);
        }
      }
      c = new RemovePiece(outer);
      c.execute();
    }
    return c;
  }

  /**
   * @return a list of any Named KeyStrokes referenced in the Decorator, if any (for search)
   */
  @Override
  public List<NamedKeyStroke> getNamedKeyStrokeList() {
    return Arrays.asList(key);
  }

  /**
   * @return a list of any Menu Text strings referenced in the Decorator, if any (for search)
   */
  @Override
  public List<String> getMenuTextList() {
    return List.of(commandName);
  }


  @Override
  public void mySetState(String newState) {
  }

  @Override
  public Rectangle boundingBox() {
    return piece.boundingBox();
  }

  @Override
  public void draw(Graphics g, int x, int y, Component obs, double zoom) {
    piece.draw(g, x, y, obs, zoom);
  }

  @Override
  public String getName() {
    return piece.getName();
  }

  @Override
  public Shape getShape() {
    return piece.getShape();
  }

  @Override
  public PieceEditor getEditor() {
    return new Ed(this);
  }

  @Override
  public String getDescription() {
    return Resources.getString("Editor.Delete.trait_description");
  }

  @Override
  public HelpFile getHelpFile() {
    return HelpFile.getReferenceManualPage("GamePiece.html", "Delete"); // NON-NLS
  }

  @Override
  public PieceI18nData getI18nData() {
    return getI18nData(commandName, Resources.getString("Editor.Delete.delete_command_description"));
  }

  @Override
  public boolean testEquals(Object o) {
    if (! (o instanceof Delete)) return false;
    Delete c = (Delete) o;
    if (! Objects.equals(commandName, c.commandName)) return false;
    return Objects.equals(key, c.key);
  }

  public static class Ed implements PieceEditor {
    private final StringConfigurer nameInput;
    private final NamedHotKeyConfigurer keyInput;
    private final JPanel controls;

    public Ed(Delete p) {
      controls = new JPanel(new TraitLayout());

      nameInput = new StringConfigurer(p.commandName);
      controls.add(new JLabel(Resources.getString("Editor.command_name")));
      controls.add(nameInput.getControls());

      keyInput = new NamedHotKeyConfigurer(null, null, p.key);
      controls.add(new JLabel(Resources.getString("Editor.keyboard_command")));
      controls.add(keyInput.getControls());

    }

    @Override
    public Component getControls() {
      return controls;
    }

    @Override
    public String getType() {
      SequenceEncoder se = new SequenceEncoder(';');
      se.append(nameInput.getValueString()).append(keyInput.getValueString());
      return ID + se.getValue();
    }

    @Override
    public String getState() {
      return "";
    }
  }
}
