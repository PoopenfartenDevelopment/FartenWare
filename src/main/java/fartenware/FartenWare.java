package fartenware;

import fartenware.modules.hud.TextPresets;
import fartenware.modules.main.BedrockBreaker;
import fartenware.modules.main.EchestFarmerPlus;
import fartenware.modules.main.ItemFrameInserter;
import fartenware.modules.main.ItemFramePlacer;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.Systems;
import meteordevelopment.meteorclient.commands.Commands;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.misc.MeteorStarscript;
import meteordevelopment.starscript.value.ValueMap;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.item.Items;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

public class FartenWare extends MeteorAddon {
    static ModMetadata metadata = FabricLoader.getInstance().getModContainer("fartenware").orElseThrow(() -> new RuntimeException("FartenWare mod container not found!")).getMetadata();
    public static String VERSION = metadata.getVersion().toString();
    public static final Logger LOG = LoggerFactory.getLogger("FartenWare");
    public static final Category MAIN = new Category("FartenWare", Items.SPYGLASS.getDefaultStack());
    public static final HudGroup HUD = new HudGroup("FartenWare");

    @Override
    public void onInitialize() {
        LOG.info("Initialising FartenWare " + FartenWare.VERSION);

        MeteorClient.EVENT_BUS.registerLambdaFactory("fartenware.systems.modules", (lookupInMethod, klass) -> (MethodHandles.Lookup) lookupInMethod.invoke(null, klass, MethodHandles.lookup()));
        MeteorClient.EVENT_BUS.registerLambdaFactory("fartenware.utils", (lookupInMethod, klass) -> (MethodHandles.Lookup) lookupInMethod.invoke(null, klass, MethodHandles.lookup()));

        // Placeholders
        MeteorStarscript.ss.set("fartenware", new ValueMap().set("fartenwatermark", VERSION));

        // Commands
        //Commands.add(new Command());

        // HUD
        Hud hud = Systems.get(Hud.class);
        hud.register(TextPresets.INFO);

        // Modules
        Modules modules = Modules.get();
        modules.add(new BedrockBreaker());
        modules.add(new EchestFarmerPlus());
        modules.add(new ItemFrameInserter());
        modules.add(new ItemFramePlacer());
    }

    @Override
    public String getPackage() {
        return "fartenware";
    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(MAIN);
    }
}
