package fartenware.modules.main;

import fartenware.FartenWare;
import fartenware.utils.bedrock.InventoryManager;
import fartenware.utils.bedrock.Messager;
import fartenware.utils.bedrock.TargetBlock;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ModuleListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.misc.AntiPacketKick;
import meteordevelopment.meteorclient.systems.modules.player.*;
import meteordevelopment.meteorclient.systems.modules.world.NoGhostBlocks;
import meteordevelopment.meteorclient.systems.modules.world.PacketMine;
import meteordevelopment.meteorclient.systems.modules.world.Timer;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class BedrockBreaker extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> toggleModules = sgGeneral.add(new BoolSetting.Builder()
        .name("toggle-modules")
        .description("Turn off specific modules when BedrockBreaker is activated.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> toggleBack = sgGeneral.add(new BoolSetting.Builder()
        .name("toggle-back-on")
        .description("Turn the specific modules back on when BedrockBreaker is deactivated.")
        .defaultValue(false)
        .visible(toggleModules::get)
        .build()
    );

    private final Setting<List<Module>> modules = sgGeneral.add(new ModuleListSetting.Builder()
        .name("modules")
        .description("Which modules to disable on activation.")
        .defaultValue(new ArrayList<>() {{
            add(Modules.get().get(AntiHunger.class));
            add(Modules.get().get(AntiPacketKick.class));
            add(Modules.get().get(AutoClicker.class));
            add(Modules.get().get(AutoTool.class));
            add(Modules.get().get(BreakDelay.class));
            add(Modules.get().get(NoGhostBlocks.class));
            add(Modules.get().get(NoMiningTrace.class));
            add(Modules.get().get(PacketMine.class));
            add(Modules.get().get(Timer.class));
        }})
        .visible(toggleModules::get)
        .build()
    );

    public ArrayList<Module> toActivate;
    private static ArrayList<TargetBlock> cachedTargetBlockList = new ArrayList<>();

    public BedrockBreaker() {
        super(FartenWare.MAIN, "bedrock-breaker", "Breaks bedrock automatically (requires haste 2).");
    }

    @Override
    public void onActivate() {
        toActivate = new ArrayList<>();

        if (toggleModules.get() && !modules.get().isEmpty() && mc.world != null && mc.player != null) {
            for (Module module : modules.get()) {
                if (module.isActive()) {
                    module.toggle();
                    toActivate.add(module);
                }
            }
        }
    }

    @Override
    public void onDeactivate() {
        if (toggleBack.get() && !toActivate.isEmpty() && mc.world != null && mc.player != null) {
            for (Module module : toActivate) {
                if (!module.isActive()) {
                    module.toggle();
                }
            }
        }
    }

    public static void addBlockPosToList(BlockPos pos) {
        ClientWorld world = MinecraftClient.getInstance().world;
        if (world.getBlockState(pos).isOf(Blocks.BEDROCK)) {

            String haveEnoughItems = InventoryManager.warningMessage();
            if (haveEnoughItems != null) {
                Messager.actionBar(haveEnoughItems);
                return;
            }

            if (shouldAddNewTargetBlock(pos)) {
                TargetBlock targetBlock = new TargetBlock(pos, world);
                cachedTargetBlockList.add(targetBlock);
            }
        }
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
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

            if (selectedBlock.getWorld() != MinecraftClient.getInstance().world) {
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

    private static boolean shouldAddNewTargetBlock(BlockPos pos) {
        for (TargetBlock breaker : cachedTargetBlockList) {
            if (breaker.getBlockPos().getManhattanDistance(pos) == 0) {
                return false;
            }
        }
        return true;
    }
}
