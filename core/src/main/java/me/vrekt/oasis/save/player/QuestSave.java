package me.vrekt.oasis.save.player;

import com.google.gson.annotations.Expose;
import me.vrekt.oasis.questing.Quest;
import me.vrekt.oasis.questing.QuestObjective;
import me.vrekt.oasis.questing.quests.QuestType;

import java.util.LinkedList;

/**
 * Save a quest objective state
 */
public final class QuestSave {

    @Expose
    private QuestType type;

    @Expose
    private LinkedList<QuestObjectiveSave> objectives;

    @Expose
    private int objectiveIndex;

    public QuestSave(QuestType type, Quest quest) {
        this.type = type;
        this.objectiveIndex = quest.currentObjectiveStep();
        this.objectives = new LinkedList<>();

        for (QuestObjective objective : quest.getObjectives()) {
            final QuestObjectiveSave save = new QuestObjectiveSave(objective.getDescription(), objective.isCompleted(), objective.isUnlocked());
            objectives.add(save);
        }
    }

    public QuestType type() {
        return type;
    }

    public LinkedList<QuestObjectiveSave> objectives() {
        return objectives;
    }

    public int objectiveIndex() {
        return objectiveIndex;
    }

    public static final class QuestObjectiveSave {

        // Mainly included for readability, it's not required.
        @Expose
        private String description;

        @Expose
        private boolean completed;

        @Expose
        private boolean unlocked;

        public QuestObjectiveSave(String description, boolean completed, boolean unlocked) {
            this.description = description;
            this.completed = completed;
            this.unlocked = unlocked;
        }

        public boolean completed() {
            return completed;
        }

        public boolean unlocked() {
            return unlocked;
        }
    }

}
