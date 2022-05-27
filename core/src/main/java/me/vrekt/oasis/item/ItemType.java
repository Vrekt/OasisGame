package me.vrekt.oasis.item;

public enum ItemType {
    ;

    public enum Tools {
        LUCID_HARVESTING_TOOL("lucid_harvesting_tool_icon");

        private final String texture, icon;

        Tools(String texture) {
            this.texture = texture;
            this.icon = texture;
        }

        Tools(String texture, String icon) {
            this.texture = texture;
            this.icon = icon;
        }

        public String getTexture() {
            return texture;
        }

        public String getIcon() {
            return icon;
        }
    }

}
