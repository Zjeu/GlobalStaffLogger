package me.nedayazady.globalstafflogger.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorUtils {

    // Regex patterns for converting legacy codes to MiniMessage tags
    private static final Pattern HEX_PATTERN_1 = Pattern.compile("(?i)[&§]#([0-9a-fA-F]{6})");
    private static final Pattern HEX_PATTERN_2 = Pattern.compile("(?i)[&§]x([&§][0-9a-fA-F]){6}");
    private static final Pattern NAMED_LEGACY_PINK = Pattern.compile("(?i)[&§]pink");
    private static final Pattern LEGACY_CODE_PATTERN = Pattern.compile("(?i)[&§]([0-9a-fk-orA-FK-OR])");

    // Fallback legacy serializer
    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.builder()
            .character('&')
            .hexColors()
            .useUnusualXRepeatedCharacterHexFormat()
            .build();

    // Custom Tag Resolvers for color aliases such as <pink>
    private static final TagResolver CUSTOM_TAGS = TagResolver.builder()
            .resolver(TagResolver.resolver("pink", Tag.styling(TextColor.fromHexString("#FFC0CB"))))
            .resolver(TagResolver.resolver("light_pink", Tag.styling(TextColor.fromHexString("#FFB6C1"))))
            .resolver(TagResolver.resolver("lightpink", Tag.styling(TextColor.fromHexString("#FFB6C1"))))
            .resolver(TagResolver.resolver("hot_pink", Tag.styling(TextColor.fromHexString("#FF69B4"))))
            .resolver(TagResolver.resolver("hotpink", Tag.styling(TextColor.fromHexString("#FF69B4"))))
            .resolver(TagResolver.resolver("orange", Tag.styling(TextColor.fromHexString("#FFA500"))))
            .resolver(TagResolver.resolver("purple", Tag.styling(NamedTextColor.DARK_PURPLE)))
            .resolver(TagResolver.resolver("magenta", Tag.styling(NamedTextColor.LIGHT_PURPLE)))
            .resolver(TagResolver.resolver("cyan", Tag.styling(NamedTextColor.DARK_AQUA)))
            .resolver(TagResolver.resolver("lime", Tag.styling(NamedTextColor.GREEN)))
            .build();

    // MiniMessage instance with standard + custom color tags
    private static final MiniMessage MINIMESSAGE = MiniMessage.builder()
            .tags(TagResolver.builder()
                    .resolver(TagResolver.standard())
                    .resolver(CUSTOM_TAGS)
                    .build())
            .build();

    /**
     * Converts legacy formatting codes (&c, &#RRGGBB, &x&r... or §c) to MiniMessage tags.
     *
     * @param message The input string.
     * @return Converted string in MiniMessage format.
     */
    public static String convertLegacyToMiniMessage(String message) {
        if (message == null || message.isEmpty()) {
            return message;
        }

        // 1. Convert &#RRGGBB or §#RRGGBB to <#RRGGBB>
        Matcher hexMatcher1 = HEX_PATTERN_1.matcher(message);
        StringBuilder sb = new StringBuilder();
        while (hexMatcher1.find()) {
            hexMatcher1.appendReplacement(sb, "<#" + hexMatcher1.group(1) + ">");
        }
        hexMatcher1.appendTail(sb);
        message = sb.toString();

        // 2. Convert &x&r&r&g&g&b&b or §x§r§r§g§g§b§b to <#rrggbb>
        Matcher hexMatcher2 = HEX_PATTERN_2.matcher(message);
        sb = new StringBuilder();
        while (hexMatcher2.find()) {
            String full = hexMatcher2.group();
            StringBuilder hex = new StringBuilder("<#");
            for (int i = 2; i < full.length(); i += 2) {
                hex.append(full.charAt(i + 1));
            }
            hex.append(">");
            hexMatcher2.appendReplacement(sb, Matcher.quoteReplacement(hex.toString()));
        }
        hexMatcher2.appendTail(sb);
        message = sb.toString();

        // 3. Convert &pink / §pink to <pink>
        message = NAMED_LEGACY_PINK.matcher(message).replaceAll("<pink>");

        // 4. Convert standard legacy codes &0-&f, &k-&r
        Matcher legacyMatcher = LEGACY_CODE_PATTERN.matcher(message);
        sb = new StringBuilder();
        while (legacyMatcher.find()) {
            char code = Character.toLowerCase(legacyMatcher.group(1).charAt(0));
            String replacement = switch (code) {
                case '0' -> "<black>";
                case '1' -> "<dark_blue>";
                case '2' -> "<dark_green>";
                case '3' -> "<dark_aqua>";
                case '4' -> "<dark_red>";
                case '5' -> "<dark_purple>";
                case '6' -> "<gold>";
                case '7' -> "<gray>";
                case '8' -> "<dark_gray>";
                case '9' -> "<blue>";
                case 'a' -> "<green>";
                case 'b' -> "<aqua>";
                case 'c' -> "<red>";
                case 'd' -> "<light_purple>";
                case 'e' -> "<yellow>";
                case 'f' -> "<white>";
                case 'k' -> "<obfuscated>";
                case 'l' -> "<bold>";
                case 'm' -> "<strikethrough>";
                case 'n' -> "<underlined>";
                case 'o' -> "<italic>";
                case 'r' -> "<reset>";
                default -> legacyMatcher.group();
            };
            legacyMatcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        legacyMatcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * Parses a string that may contain MiniMessage tags (<pink>, <red>, <bold>)
     * or legacy color codes (&c, &#ff0000, &d).
     *
     * @param message The raw string to parse.
     * @return The parsed Adventure Component.
     */
    public static Component parse(String message) {
        if (message == null || message.isEmpty()) {
            return Component.empty();
        }

        try {
            String converted = convertLegacyToMiniMessage(message);
            return MINIMESSAGE.deserialize(converted);
        } catch (Exception e) {
            // Fallback to legacy serializer if MiniMessage fails on malformed input
            try {
                return LEGACY_SERIALIZER.deserialize(message);
            } catch (Exception ex) {
                return Component.text(message);
            }
        }
    }
}
