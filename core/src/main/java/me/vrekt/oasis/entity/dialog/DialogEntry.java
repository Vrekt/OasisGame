package me.vrekt.oasis.entity.dialog;

import com.badlogic.gdx.utils.Disposable;

import java.util.Map;

/**
 * A single dialog entry
 */
public interface DialogEntry extends Disposable {

    /**
     * @return the key of this entry
     */
    String getKey();

    void setKey(String key);

    /**
     * @return the main content of this entry
     */
    String getContent();

    void setContent(String content);

    /**
     * @return the key this entry links to next.
     */
    String getLinksTo();

    void setLinksTo(String linksTo);

    /**
     * @return the action to execute
     */
    String getAction();

    /**
     * @return if this entry has an action
     */
    boolean hasAction();

    /**
     * @return the content to show when the entity is waiting for the player to complete something
     */
    String getWaitingContent();

    boolean hasOptions();

    void setHasOptions(boolean hasOptions);

    /**
     * @return if this dialog has suggestions
     */
    boolean hasSuggestions();

    /**
     * @return if this entry is skippable
     */
    boolean isSkippable();

    void setSkippable(boolean skippable);

    /**
     * @return the suggestions of this entry
     */
    Map<String, Float> getSuggestions();

    boolean advanceOnceExited();

    /**
     * @return if we are locked, waiting for the player to complete something
     */
    boolean isWaiting();

    void setWaiting(boolean waiting);

    /**
     * Set this entry has been visited (seen by the player)
     */
    void setVisited();

    boolean hasVisited();


}
