package fartenware.modules.hud;

import fartenware.FartenWare;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.elements.TextHud;

public class TextPresets {
    public static final HudElementInfo<TextHud> INFO = new HudElementInfo<>(FartenWare.HUD, "farten-presets", "Displays text with Starscript.", TextPresets::create);

    static {
        addPreset("Farten-Watermark", "FartenWare #1{fartenware.fartenwatermark}", Integer.MAX_VALUE);
    }

    private static TextHud create() {
        return new TextHud(INFO);
    }

    private static HudElementInfo<TextHud>.Preset addPreset(String title, String text, int updateDelay) {
        return INFO.addPreset(title, textHud -> {
            if (text != null) textHud.text.set(text);
            if (updateDelay != -1) textHud.updateDelay.set(updateDelay);
        });
    }
}
