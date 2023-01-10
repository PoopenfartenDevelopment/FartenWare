package fartenware.utils.bedrock;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class Messager {
    public static void actionBar(String message){
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        minecraftClient.inGameHud.setOverlayMessage(Text.translatable(message),false);
    }
}
