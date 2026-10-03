package me.nedayazady.globalstafflogger.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ColorUtilsTest {

    @Test
    void testPinkMiniMessage() {
        Component comp = ColorUtils.parse("<pink>Hello</pink>");
        assertEquals("Hello", PlainTextComponentSerializer.plainText().serialize(comp));
        assertEquals(TextColor.fromHexString("#FFC0CB"), comp.color());
    }

    @Test
    void testPinkUnclosed() {
        Component comp = ColorUtils.parse("<pink>Hello");
        assertEquals("Hello", PlainTextComponentSerializer.plainText().serialize(comp));
        assertEquals(TextColor.fromHexString("#FFC0CB"), comp.color());
    }

    @Test
    void testLegacyAmpersandColor() {
        Component comp = ColorUtils.parse("&cRedText");
        assertEquals("RedText", PlainTextComponentSerializer.plainText().serialize(comp));
        assertEquals(NamedTextColor.RED, comp.color());
    }

    @Test
    void testLegacyPinkAmpersand() {
        Component comp = ColorUtils.parse("&dPinkText");
        assertEquals("PinkText", PlainTextComponentSerializer.plainText().serialize(comp));
        assertEquals(NamedTextColor.LIGHT_PURPLE, comp.color());
    }

    @Test
    void testLegacyNamedPink() {
        Component comp = ColorUtils.parse("&pinkHello");
        assertEquals("Hello", PlainTextComponentSerializer.plainText().serialize(comp));
        assertEquals(TextColor.fromHexString("#FFC0CB"), comp.color());
    }

    @Test
    void testHexFormat() {
        Component comp = ColorUtils.parse("&#FFC0CBHello");
        assertEquals("Hello", PlainTextComponentSerializer.plainText().serialize(comp));
        assertEquals(TextColor.fromHexString("#FFC0CB"), comp.color());
    }

    @Test
    void testMixedFormats() {
        Component comp = ColorUtils.parse("&8[&6Spy&8] <pink>User</pink> &7(Lobby)");
        assertEquals("[Spy] User (Lobby)", PlainTextComponentSerializer.plainText().serialize(comp));
    }

    @Test
    void testEmptyAndNull() {
        assertEquals(Component.empty(), ColorUtils.parse(null));
        assertEquals(Component.empty(), ColorUtils.parse(""));
    }

    @Test
    void testMiniMessageAdvancedTags() {
        Component comp = ColorUtils.parse("<gradient:#ff0000:#00ff00>Gradient</gradient>");
        assertEquals("Gradient", PlainTextComponentSerializer.plainText().serialize(comp));
    }

    @Test
    void testLiteralChevrons() {
        Component comp = ColorUtils.parse("Player: Hello <3");
        assertEquals("Player: Hello <3", PlainTextComponentSerializer.plainText().serialize(comp));
    }
}
