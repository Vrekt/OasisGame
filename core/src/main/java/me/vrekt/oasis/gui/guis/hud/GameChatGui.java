package me.vrekt.oasis.gui.guis.hud;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.utils.Pool;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextField;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.gui.Gui;
import me.vrekt.oasis.gui.GuiManager;
import me.vrekt.oasis.gui.GuiType;
import me.vrekt.oasis.utility.logging.GameLogging;
import org.apache.commons.lang3.StringUtils;

import java.util.Iterator;
import java.util.LinkedList;

public final class GameChatGui extends Gui {

    private static final int MAX_CHAT_LENGTH = 125;
    private static final float CHAT_MESSAGE_TIME = 6.5f;

    private final Pool<ChatMessageContainer> containerPool = new Pool<>(10, 10) {
        @Override
        protected ChatMessageContainer newObject() {
            return new ChatMessageContainer();
        }
    };

    private final PlayerSP player;

    private final LinkedList<ChatMessageContainer> messages = new LinkedList<>();

    private final VisTable log;
    private final VisTextField chatInput;
    private boolean hasAnyFocus;

    public GameChatGui(GuiManager guiManager) {
        super(GuiType.CHAT, guiManager);

        this.player = guiManager.getGame().getPlayer();

        rootTable.setVisible(false);
        rootTable.bottom().left();

        // half a second
        this.updateInterval = 0.1f;

        final VisTable parent = new VisTable();
        parent.setFillParent(false);

        parent.setBackground(guiManager.getStyle().getTheme());

        parent.bottom().left();
        parent.getColor().a = 0.88f;

        log = new VisTable();
        chatInput = new VisTextField(StringUtils.EMPTY, guiManager.getStyle().getTransparentFieldStyle());
        chatInput.setMaxLength(MAX_CHAT_LENGTH);

        parent.add(log).left().padLeft(3f);
        parent.row();
        parent.add(chatInput).padLeft(3f).left().fillX();

        // have the chat kinda pop-out the window with no borders
        // going to be broken with resizing
        rootTable.add(parent).padLeft(-4).padBottom(-4);
        rootTable.setZIndex(99);

        handleFocusController();
        handleInputController();

        guiManager.addGui(rootTable);
    }

    @Override
    public void timedUpdate(float tick) {
        for (Iterator<ChatMessageContainer> it = messages.iterator(); it.hasNext(); ) {
            final ChatMessageContainer message = it.next();

            if (!message.visible) {
                final Cell<?> cell = log.row();
                message.setParentCell(cell);
                message.add(log);
            } else if (message.expired()) {
                message.startFade();
            } else if (message.fading) {
                final boolean done = message.fade();
                if (done) {
                    log.getCells().removeValue(message.parentCell, true);
                    log.removeActor(message.label);
                    log.invalidate();

                    containerPool.free(message);
                    it.remove();
                }
            }
        }
    }

    /**
     * Handle input focus and escape key presses
     */
    private void handleFocusController() {
        chatInput.addListener(new FocusListener() {
            @Override
            public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
                if (focused) {
                    hasAnyFocus = true;
                    player.disableMovement();
                } else if (hasAnyFocus) {
                    player.enableMovement();
                }
            }
        });
    }

    private void handleInputController() {
        chatInput.addListener(new ClickListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    guiManager.getStage().unfocus(chatInput);

                    hasAnyFocus = false;
                    player.enableMovement();
                    hide();
                    return true;
                } else if (keycode == Input.Keys.ENTER) {
                    player.getConnection().sendChatMessage(chatInput.getText());
                    addLocalChatMessage();
                    chatInput.clearText();
                }
                return false;
            }
        });
    }

    /**
     * Add local message to the chat log
     */
    private void addLocalChatMessage() {
        messages.add(obtain(player.name(), chatInput.getText()));
    }

    /**
     * Add network message to chat
     *
     * @param name    name
     * @param message message
     */
    public void addNetworkMessage(String name, String message) {
        if (messages.size() >= 10) {
            GameLogging.warn(this, "Exceeded max message count! from=%s msg=%s", name, message);
            return;
        }
        messages.add(obtain(name, message));

        if (!isShowing) {
            // show this GUI, message log only.
            show();
            chatInput.setVisible(false);
        }
    }

    /**
     * Obtain a new message container
     *
     * @param name    the player name
     * @param message the message
     * @return the container
     */
    private ChatMessageContainer obtain(String name, String message) {
        final ChatMessageContainer container = containerPool.obtain();
        container.label = new VisLabel(name + ": " + message, guiManager.getStyle().getSmallWhite());
        return container;
    }

    @Override
    public void show() {
        super.show();

        guiManager.getHudComponent().hideComponentsForChat();
        guiManager.getStage().setKeyboardFocus(chatInput);
        rootTable.setVisible(true);

        if (!chatInput.isVisible()) chatInput.setVisible(true);
    }

    @Override
    public void hide() {
        super.hide();

        guiManager.getHudComponent().showComponentsAfterChat();
        rootTable.setVisible(false);

        // give control back to player if this GUI was unexpectedly closed.
        if (hasAnyFocus) player.enableMovement();
    }

    private final class ChatMessageContainer implements Pool.Poolable {
        VisLabel label;
        Cell<?> parentCell;

        boolean visible, fading;
        float added;

        public ChatMessageContainer() {
            label = new VisLabel(StringUtils.EMPTY, guiManager.getStyle().getSmallWhite());
        }

        void add(VisTable parent) {
            visible = true;
            added = GameManager.getTick();
            parent.add(label).left();
        }

        void setParentCell(Cell<?> cell) {
            this.parentCell = cell;
        }

        boolean expired() {
            return !fading && GameManager.hasTimeElapsed(added, CHAT_MESSAGE_TIME);
        }

        void startFade() {
            fading = true;
        }

        boolean fade() {
            label.getColor().a -= 0.1f;
            return label.getColor().a <= 0.0f;
        }

        @Override
        public void reset() {
            label = null;
            parentCell = null;
            visible = false;
            fading = false;
        }
    }

}
