package fartenware.utils.bedrock;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class BlockBreaker {
    public static void breakBlock(BlockPos pos) {
        InventoryManager.switchToItem(Items.NETHERITE_PICKAXE);
        MinecraftClient.getInstance().interactionManager.attackBlock(pos, Direction.UP);
    }
}
