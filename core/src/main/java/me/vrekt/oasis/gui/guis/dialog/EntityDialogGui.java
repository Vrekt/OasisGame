package me.vrekt.oasis.gui.guis.dialog;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.github.tommyettinger.textra.TypingLabel;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextField;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.entity.dialog.EntityDialogBuilder;
import me.vrekt.oasis.entity.interactable.EntitySpeakable;
import me.vrekt.oasis.gui.GuiType;
import me.vrekt.oasis.gui.Gui;
import me.vrekt.oasis.gui.GuiManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;

import java.util.LinkedList;


public final class EntityDialogGui extends Gui {

    private final LinkedList<DialogOptionContainer> dialogOptionContainers = new LinkedList<>();
    private final VisLabel entityNameLabel;
    private final TypingLabel dialogTextLabel;
    private final VisTextField userInputField;

    // wrapper for each dialog option
    private final VisTable dialogOptionsWrapper;

    private final VisImage entityPreview;
    private EntitySpeakable entity;
    private int optionTracker;

    private boolean hasAnyFocus;
    private int suggestionsBeingShown;
    private final LinkedList<String> suggestionsShowing = new LinkedList<>();

    private final JaroWinklerSimilarity similarity = new JaroWinklerSimilarity();
    public EntityDialogGui(GuiManager guiManager) {
        super(GuiType.DIALOG, guiManager);

        hideWhenVisible.add(GuiType.HUD);

        rootTable.setFillParent(true);
        rootTable.setVisible(false);
        rootTable.bottom().padBottom(6f);

        final TextureRegionDrawable start = new TextureRegionDrawable(guiManager.getAsset().get("dialog_picture"));
        final TextureRegionDrawable middle = new TextureRegionDrawable(guiManager.getAsset().get("dialog_option"));
        final TextureRegionDrawable end = new TextureRegionDrawable(guiManager.getAsset().get("dialog_end_picture"));

        // add the input field for users inputting their own responses
        final VisTable inputTable = new VisTable();
        inputTable.left();

        userInputField = new VisTextField("...", guiManager.getStyle().getFieldStyle());
        inputTable.add(userInputField).padBottom(-2f);
        createUserInputHandler(userInputField);

        // Used for the 'f' key input image
        final Table keyContainer = new Table();
        keyContainer.right();
        keyContainer.add(new VisImage(guiManager.getAsset().get("fkey"))).size(32, 32);

        // Suggestions for how the player should respond
        // Or, the default dialog options
        final VisTable dialogOptionsContainer = new VisTable();
        dialogOptionsContainer.left();

        // individual containers for each dialog option or suggestion
        dialogOptionsWrapper = new VisTable();

        // create containers for each dialog option that can be clicked.
        // these will be dynamically added/remove later.
        for (int i = 0; i < 3; i++) {
            final VisLabel label = new VisLabel(StringUtils.EMPTY, guiManager.getStyle().getSmallWhite());
            final VisTable table = createTextContainerComponent(label, start, middle, end);
            final DialogOptionContainer container = new DialogOptionContainer(table, label);
            table.setVisible(false);

            dialogOptionContainers.add(container);
        }

        // finally add the wrapped contents into the container
        dialogOptionsContainer.add(dialogOptionsWrapper);

        final VisTable dialogContentsContainer = new VisTable();
        entityNameLabel = new VisLabel(StringUtils.EMPTY, guiManager.getStyle().getMediumWhite());
        dialogTextLabel = new TypingLabel(StringUtils.EMPTY, guiManager.getStyle().getMediumWhite());
        // fixed width for this text so it wraps nicely inside the container
        dialogTextLabel.setWidth(448);
        dialogTextLabel.setWrap(true);

        // entity preview picture
        entityPreview = new VisImage();
        final VisTable entityPreviewContainer = new VisTable();
        final VisTable dialogContents = new VisTable();
        dialogContents.left();

        // set background of the container
        dialogContentsContainer.setBackground(guiManager.getStyle().getTheme());
        // add names and previews
        entityPreviewContainer.add(entityNameLabel);
        entityPreviewContainer.row();
        entityPreviewContainer.add(entityPreview).left();
        dialogContents.add(entityPreviewContainer).padLeft(16);
        // finally add dialog text
        dialogContents.add(dialogTextLabel).padLeft(8).width(448);
        dialogContents.row();

        // add contents to main container
        dialogContentsContainer.add(dialogContents);

        // now add all elements into the root
        rootTable.add(inputTable).left().bottom();
        rootTable.row();
        rootTable.add(dialogOptionsWrapper).left().bottom();
        rootTable.row();
        rootTable.add(dialogContentsContainer);

        guiManager.addGui(rootTable);
    }

    /**
     * Add the container to the wrapper table
     *
     * @param container the container
     */
    private void addDialogOptionContainer(DialogOptionContainer container) {
        dialogOptionsWrapper.add(container.parent).padTop(2f).row();
    }

    /**
     * Show dialog for an entity
     * Also invoked when a dialog is continued onto the next stage
     *
     * @param entity the entity
     */
    public void showEntityDialog(EntitySpeakable entity) {
        this.entity = entity;

        entityNameLabel.setText(entity.getName());
        entityPreview.setDrawable(new TextureRegionDrawable(entity.getDialogFace()));
        dialogTextLabel.setText(entity.getDialog().getText());
        dialogTextLabel.restart();

        if (entity.getDialog().hasSuggestions()) {
            // enable suggestions input box
            userInputField.setVisible(true);
        } else {
            userInputField.setVisible(false);
            entity.getDialog().getOptions().forEach(this::addOption);
        }
    }

    /**
     * Add an option to be clicked
     *
     * @param key    the key
     * @param option the option
     */
    private void addOption(String key, String option) {
        if (optionTracker >= 3) return; // TODO: Later more options.
        final DialogOptionContainer container = dialogOptionContainers.get(optionTracker);
        container.label.setText(option);
        container.listener.setKeyAndEntity(key, entity);
        optionTracker++;
        // add this container to the screen
        addDialogOptionContainer(container);
    }

    /**
     * Add a suggestion to be clicked
     *
     * @param index      the current index
     * @param suggestion the suggestion string
     */
    private void addSuggestion(int index, String suggestion, String key) {
        final DialogOptionContainer container = dialogOptionContainers.get(index);
        container.label.setText(suggestion);
        container.listener.setKeyAndEntity(key, entity);
        container.suggestion = suggestion;
        container.show();

        addDialogOptionContainer(container);
    }

    private void clearTextContainers() {
        dialogOptionsWrapper.clearChildren();
    }

    /**
     * Find the suggestion container and clear it.
     * Kinda cumbersome, but it works.
     *
     * @param suggestion the suggestion
     */
    private void clearTextContainerSuggestion(String suggestion) {
        final DialogOptionContainer cc = dialogOptionContainers
                .stream()
                .filter(container -> StringUtils.equals(suggestion, container.suggestion))
                .findAny()
                .orElse(null);
        if (cc != null) {
            cc.reset();
            dialogOptionsWrapper.removeActor(cc.parent);
        }
    }

    /**
     * Create text container UI elements
     *
     * @param label  the label
     * @param start  fancy picture
     * @param middle fancy middle picture
     * @param end    fancy end picture
     * @return the newly created container table
     */
    private VisTable createTextContainerComponent(VisLabel label, TextureRegionDrawable start, TextureRegionDrawable middle, TextureRegionDrawable end) {
        final VisTable container = new VisTable();
        final VisTable text = new VisTable();

        text.setBackground(middle);
        text.add(label);

        container.add(new VisImage(start));
        container.add(text);
        container.add(new VisImage(end));
        return container;
    }

    /**
     * Create the input handlers for the text field
     *
     * @param field the field
     */
    private void createUserInputHandler(VisTextField field) {
        handleClickAction(field);
        handleFocusControl(field);
        field.setTextFieldListener(this::handleInputSimilarities);
    }

    /**
     * Reset field input to empty when clicked.
     * TODO: May or may not be desirable, especially if focus was lost
     *
     * @param field the field
     */
    private void handleClickAction(VisTextField field) {
        field.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                field.setText(StringUtils.EMPTY);
                // reset suggestions shown since input was cleared
                suggestionsBeingShown = 0;
            }
        });
    }

    /**
     * handle focus control
     *
     * @param field the field
     */
    private void handleFocusControl(VisTextField field) {
        field.addListener(new FocusListener() {
            @Override
            public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
                if (focused) {
                    hasAnyFocus = true;
                    GameManager.getPlayer().setDisableMovement(true);
                } else if (hasAnyFocus) {
                    GameManager.getPlayer().setDisableMovement(false);
                }
            }
        });
    }


    /**
     * Handle similarities between user input and suggestions
     * Only invoked if this element is visible indicating suggestions support
     *
     * @param field     the text field
     * @param character character typed
     */
    private void handleInputSimilarities(VisTextField field, char character) {
        final String text = field.getText();

        // reset our suggestions if the space was cleared (backspace)
        if (text.isEmpty()) {
            // remove all actors from container wrapper table
            clearTextContainers();
            suggestionsShowing.clear();
            suggestionsBeingShown = 0;
            dialogOptionContainers.forEach(DialogOptionContainer::reset);
            return;
        }

        // may or may not need the second statement
        if (entity != null && entity.getDialog().hasSuggestions()) {
            // if suggestions is already maxed, just return.
            if (suggestionsBeingShown >= 3) return;
            for (EntityDialogBuilder.Suggestion entry : entity.getDialog().getSuggestions()) {
                final String suggestion = entry.suggestion;
                final double tolerance = entry.tolerance;
                final double sim = similarity.apply(text, suggestion);
                if (sim >= tolerance && !suggestionsShowing.contains(suggestion)) {
                    // string is similar show it as a suggestion.
                    suggestionsShowing.add(suggestion);
                    addSuggestion(suggestionsBeingShown, suggestion, entry.keyLink);
                    suggestionsBeingShown++;
                } else if (sim <= tolerance && suggestionsShowing.contains(suggestion)) {
                    clearTextContainerSuggestion(suggestion);
                    suggestionsShowing.remove(suggestion);
                    suggestionsBeingShown--;
                }
            }
        }
    }

    @Override
    public void show() {
        super.show();
        rootTable.setVisible(true);
    }

    @Override
    public void hide() {
        super.hide();
        rootTable.setVisible(false);

        // give control back to player if this GUI was unexpectedly closed.
        if (hasAnyFocus) GameManager.getPlayer().setDisableMovement(false);
    }


    /**
     * Container for dialog options that can assign handlers for clicking and more
     */
    private final class DialogOptionContainer {
        private final VisTable parent;
        private final VisLabel label;
        private final DialogOptionClickListener listener;
        private String suggestion;

        public DialogOptionContainer(VisTable parent, VisLabel label) {
            this.parent = parent;
            this.label = label;
            listener = new DialogOptionClickListener();
            parent.addListener(listener);
        }

        void hide() {
            parent.setVisible(false);
        }

        void show() {
            parent.setVisible(true);
        }

        void reset() {
            label.setText(StringUtils.EMPTY);
            hide();
        }

    }

    /**
     * Handles clicking of an individual dialog option
     */
    private final class DialogOptionClickListener extends ClickListener {
        private EntitySpeakable entity;
        private String key;

        public void setKeyAndEntity(String key, EntitySpeakable entity) {
            this.key = key;
            this.entity = entity;
        }

        @Override
        public void clicked(InputEvent event, float x, float y) {
            if (entity.advanceDialogStage(key)) {
                // no more dialog so stop speaking
                entity.setSpeakingTo(false);
                EntityDialogGui.this.hide();
            } else {
                EntityDialogGui.this.showEntityDialog(entity);
            }
        }
    }

}
