package by.mrtorex.businessshark.client.gui.enums;

import lombok.Getter;

/**
 * Перечисление путей к CSS-файлам для различных тем оформления приложения.
 */
@Getter
public enum ThemesPath {
    /**
     * Путь к светлой теме оформления.
     */
    LIGHT("/styles/colors/colors-light.css"),

    /**
     * Путь к тёмной теме оформления.
     */
    DARK("/styles/colors/colors-dark.css"),

    /**
     * Путь к теме оформления в стиле Synthwave.
     */
    SYNTHWAVE("/styles/colors/colors-synthwave.css"),

    /**
     * Путь к теме оформления в стиле Persona 5.
     */
    PERSONA5("/styles/colors/colors-persona5.css"),

    /**
     * Путь к теме оформления в стиле Catpuccin.
     */
    CATPUCCIN("/styles/colors/colors-catpuccin.css");

    private final String pathToCss;

    /**
     * Конструктор перечисления.
     *
     * @param pathToCss путь к CSS-файлу темы
     * @throws IllegalArgumentException если путь к CSS-файлу пустой или null
     */
    ThemesPath(String pathToCss) {
        if (pathToCss == null || pathToCss.trim().isEmpty()) {
            throw new IllegalArgumentException("Путь к CSS-файлу не может быть пустым или null");
        }
        this.pathToCss = pathToCss;
    }
}