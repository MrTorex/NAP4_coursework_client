package by.mrtorex.businessshark.client.gui.enums;

import lombok.Getter;

@Getter
public enum ThemesPath {
    LIGHT("/colors-light.css"),
    DARK("/colors-dark.css"),
    SYNTHWAVE("/colors-synthwave.css"),
    PERSONA5("/colors-persona5.css");

    private final String pathToCss;

    ThemesPath(String pathToCss) {
        this.pathToCss = pathToCss;
    }
}
