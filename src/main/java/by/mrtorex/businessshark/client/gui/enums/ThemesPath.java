package by.mrtorex.businessshark.client.gui.enums;

import lombok.Getter;

@Getter
public enum ThemesPath {
    LIGHT("/styles/colors/colors-light.css"),
    DARK("/styles/colors/colors-dark.css"),
    SYNTHWAVE("/styles/colors/colors-synthwave.css"),
    PERSONA5("/styles/colors/colors-persona5.css"),
    CATPUCCIN("/styles/colors/colors-catpuccin.css");

    private final String pathToCss;

    ThemesPath(String pathToCss) {
        this.pathToCss = pathToCss;
    }
}
