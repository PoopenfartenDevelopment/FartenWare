package fartenware.systems.modules.main;

import fartenware.systems.FartenWare;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class ItemFrameInserter extends Module{
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> distance = sgGeneral.add(new IntSetting.Builder()
        .name("distance")
        .description("The max distance to search for pistons.")
        .min(1)
        .sliderMin(1)
        .defaultValue(5)
        .sliderMax(6)
        .max(6)
        .build()
    );

    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
        .name("delay")
        .description("delay between places")
        .defaultValue(2)
        .range(0,20)
        .build()
    );
    private final Setting<Boolean> rotate = sgGeneral.add(new BoolSetting.Builder()
        .name("rotate")
        .description("Whether or not to rotate when placing.")
        .defaultValue(true)
        .build()
    );

    public ItemFrameInserter() {
        super(FartenWare.MAIN,"item-frame-inserter", "Places currently held item into item frame (recommend to use with auto replenish with unstackables enabled).");
    }

    int timer = 0;

    @Override
    public void onActivate(){
        timer = 0;
    }

    @Override
    public void onDeactivate(){
        timer = 0;
    }

    @EventHandler
    public void onTick(TickEvent.Pre event){

        if (timer < delay.get()){
            timer++;
            return;
        }

        for (Entity entity : mc.world.getEntities()) {
            if (!(entity instanceof ItemFrameEntity)) continue;
            BlockPos entityPos = new BlockPos(Math.floor(entity.getPos().x), Math.floor(entity.getPos().y), Math.floor(entity.getPos().z));
            if (distanceBetween(entityPos,mc.player.getBlockPos()) > distance.get()) continue;
            if (!((ItemFrameEntity) entity).getHeldItemStack().isEmpty()) continue;

            if (rotate.get()){
                Rotations.rotate(Rotations.getYaw(entity),Rotations.getPitch(entity),() -> mc.interactionManager.interactEntity(mc.player,entity,Hand.MAIN_HAND));
            }else{
                mc.interactionManager.interactEntity(mc.player,entity,Hand.MAIN_HAND);
            }
            timer = 0;
            return;
        }

    }

    public static double distanceBetween(BlockPos pos1, BlockPos pos2) {
        double d = pos1.getX() - pos2.getX();
        double e = pos1.getY() - pos2.getY();
        double f = pos1.getZ() - pos2.getZ();
        return MathHelper.sqrt((float) (d * d + e * e + f * f));
    }
}
