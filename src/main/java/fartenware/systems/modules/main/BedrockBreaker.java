package fartenware.systems.modules.main;

import fartenware.utils.bedrock.InventoryManager;
import fartenware.utils.bedrock.Messager;
import fartenware.utils.bedrock.TargetBlock;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

public class BedrockBreaker extends Module {
    public BedrockBreaker() {
        super(Categories.World,"bedrock-breaker","Breaks bedrock automatically (requires haste 2).");
    }

    private static ArrayList<TargetBlock> cachedTargetBlockList = new ArrayList<>();

    public static void addBlockPosToList(BlockPos pos) {
        ClientWorld world = MinecraftClient.getInstance().world;
        if (world.getBlockState(pos).isOf(Blocks.BEDROCK)) {

            String haveEnoughItems = InventoryManager.warningMessage();
            if (haveEnoughItems != null) {
                Messager.actionBar(haveEnoughItems);
                return;
            }

            if (shouldAddNewTargetBlock(pos)){
                TargetBlock targetBlock = new TargetBlock(pos, world);
                cachedTargetBlockList.add(targetBlock);
            }
        }
    }
    @EventHandler
    public void onTick(TickEvent.Post event){

    if (InventoryManager.warningMessage() != null) {
            return;
        }
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        PlayerEntity player = minecraftClient.player;

        if (!"survival".equals(minecraftClient.interactionManager.getCurrentGameMode().getName())) {
            return;
        }

        for (int i = 0; i < cachedTargetBlockList.size(); i++) {
            TargetBlock selectedBlock = cachedTargetBlockList.get(i);

            if (selectedBlock.getWorld() != MinecraftClient.getInstance().world ) {
                cachedTargetBlockList = new ArrayList<>();
                break;
            }

            if (blockInPlayerRange(selectedBlock.getBlockPos(), player, 3.4f)) {
                TargetBlock.Status status = cachedTargetBlockList.get(i).tick();
                if (status == TargetBlock.Status.RETRACTING) {
                    continue;
                } else if (status == TargetBlock.Status.FAILED || status == TargetBlock.Status.RETRACTED) {
                    cachedTargetBlockList.remove(i);
                } else {
                    break;
                }

            }
        }
    }

    private static boolean blockInPlayerRange(BlockPos blockPos, PlayerEntity player, float range) {
        return blockPos.isWithinDistance(player.getPos(), range);
    }

    private static boolean shouldAddNewTargetBlock(BlockPos pos){
        for (TargetBlock breaker : cachedTargetBlockList) {
            if (breaker.getBlockPos().getManhattanDistance(pos) == 0) {
                return false;
            }
        }
        return true;
    }
}
