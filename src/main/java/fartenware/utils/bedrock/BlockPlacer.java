package fartenware.utils.bedrock;

import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class BlockPlacer {
    public static void simpleBlockPlacement(BlockPos pos, ItemConvertible item) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();

        InventoryManager.switchToItem(item);
        BlockHitResult hitResult = new BlockHitResult(new Vec3d(pos.getX(), pos.getY(), pos.getZ()), Direction.UP, pos, false);
        placeBlockWithoutInteractingBlock(minecraftClient, hitResult);
    }

    public static void pistonPlacement(BlockPos pos, Direction direction) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        double x = pos.getX();
        PlayerEntity player = minecraftClient.player;
        float pitch;
        switch (direction) {
            case UP -> pitch = 90f;
            case DOWN -> {
                pitch = -90f;
            }
            default -> {
                pitch = 90f;
            }
        }
        minecraftClient.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(player.getYaw(1.0f), pitch, player.isOnGround()));

        Vec3d vec3d = new Vec3d(x, pos.getY(), pos.getZ());

        InventoryManager.switchToItem(Blocks.PISTON);
        BlockHitResult hitResult = new BlockHitResult(vec3d, Direction.UP, pos, false);
        placeBlockWithoutInteractingBlock(minecraftClient, hitResult);
    }

    private static void placeBlockWithoutInteractingBlock(MinecraftClient minecraftClient, BlockHitResult hitResult) {
        ClientPlayerEntity player = minecraftClient.player;
        ItemStack itemStack = player.getStackInHand(Hand.MAIN_HAND);

        mc.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, hitResult, 0));

        if (!itemStack.isEmpty() && !player.getItemCooldownManager().isCoolingDown(itemStack.getItem())) {
            ItemUsageContext itemUsageContext = new ItemUsageContext(player, Hand.MAIN_HAND, hitResult);
            itemStack.useOnBlock(itemUsageContext);
        }
    }
}
