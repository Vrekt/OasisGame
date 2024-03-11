package me.vrekt.oasis.gui;

public enum GuiType {

    DIALOG {
        @Override
        public void hideOtherGuis(GameGui gui) {
            gui.hideGui(GuiType.HUD);
        }
    },
    QUEST {
        @Override
        public void hideOtherGuis(GameGui gui) {
            gui.hideGui(GuiType.INVENTORY, GuiType.CONTAINER);
        }
    },
    INVENTORY {
        @Override
        public void hideOtherGuis(GameGui gui) {
            gui.hideGui(GuiType.QUEST, GuiType.CONTAINER);
        }
    },
    HUD {

    },
    CLASS {

    },
    PAUSE {

    },
    SETTINGS {

    },
    CONTAINER {
        @Override
        public void hideOtherGuis(GameGui gui) {
            gui.hideGui(GuiType.QUEST, GuiType.INVENTORY);
        }
    },
    SAVE_GAME {
        @Override
        public void hideOtherGuis(GameGui gui) {
            super.hideOtherGuis(gui);
        }
    },
    DEBUG_MENU {

    },
    HINT {

    };

    /**
     * Hide certain GUIs when one is toggled visible
     *
     * @param gui the gui
     */
    public void hideOtherGuis(GameGui gui) {

    }

    public void showOtherGuis(GameGui gui) {

    }

}
