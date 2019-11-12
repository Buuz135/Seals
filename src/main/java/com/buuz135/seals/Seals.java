package com.buuz135.seals;

import com.buuz135.seals.client.SealButton;
import com.buuz135.seals.client.SealPlayerRenderer;
import com.buuz135.seals.config.JSONConfigLoader;
import com.buuz135.seals.config.SealManager;
import com.buuz135.seals.network.ClientSyncSealsMessage;
import com.buuz135.seals.network.SealRequestMessage;
import com.buuz135.seals.storage.SealWorldStorage;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.advancements.AdvancementsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.multiplayer.ClientAdvancementManager;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("seals")
public class Seals {

    public static final SealManager SEAL_MANAGER = new SealManager();
    public static final SimpleChannel NETWORK = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("seals", "network"),
            () -> "1.0",
            s -> true,
            s -> true
    );
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();
    public static final List<UUID> PATREONS = new ArrayList<>();

    public Seals() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the enqueueIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        new JSONConfigLoader();
        NETWORK.registerMessage(0, ClientSyncSealsMessage.class, ClientSyncSealsMessage::toBytes, packetBuffer -> new ClientSyncSealsMessage().fromBytes(packetBuffer),  ClientSyncSealsMessage::handle);
        NETWORK.registerMessage(1, SealRequestMessage.class, SealRequestMessage::toBytes, packetBuffer -> new SealRequestMessage().fromBytes(packetBuffer),  SealRequestMessage::handle);
        try {
            PATREONS.addAll(getPlayers(new URL("https://raw.githubusercontent.com/Buuz135/Industrial-Foregoing/master/contributors.json")));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private void setup(final FMLCommonSetupEvent event) {

    }


    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        replacePlayerRenderer();
        ClientAdvancementManager advancementManager = new ClientAdvancementManager(Minecraft.getInstance());
    }

    @OnlyIn(Dist.CLIENT)
    private void replacePlayerRenderer() {
        Minecraft.getInstance().getRenderManager().playerRenderer = new SealPlayerRenderer(Minecraft.getInstance().getRenderManager());
        Minecraft.getInstance().getRenderManager().skinMap.put("default", Minecraft.getInstance().getRenderManager().playerRenderer);
        Minecraft.getInstance().getRenderManager().skinMap.put("slim", new SealPlayerRenderer(Minecraft.getInstance().getRenderManager(), true));
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {

    }

    private void processIMC(final InterModProcessEvent event) {

    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        // do something when the server starts
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event){
        World world = event.getPlayer().getEntityWorld();
        if (world instanceof ServerWorld && event.getPlayer() instanceof ServerPlayerEntity){
            NETWORK.sendTo(new ClientSyncSealsMessage(SealWorldStorage.get((ServerWorld) world).serializeNBT()), ((ServerPlayerEntity) event.getPlayer()).connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onGuiOpen(GuiScreenEvent.InitGuiEvent.Post event) {
        if (event.getGui() instanceof AdvancementsScreen) {
            List<SealInfo> seals = new ArrayList<>(SEAL_MANAGER.getSeals());
            seals.removeIf(sealInfo -> sealInfo.isInvisible() && !sealInfo.hasAchievedSealClient(Minecraft.getInstance().player));
            Screen screen = event.getGui();
            int guiLeft = (screen.width - 252) / 2;
            int guiTop = (screen.height - 140) / 2;
            for (int i = 0; i < seals.size(); i++) {
                event.addWidget(new SealButton(seals.get(i), guiLeft - 26 * ((i / 6 + 1)), guiTop + 24 * (i % 6) - 6));
            }
        } else if (event.getGui().getClass().getName().equalsIgnoreCase("betteradvancements.gui.BetterAdvancementsScreen")) {
            List<SealInfo> seals = new ArrayList<>(SEAL_MANAGER.getSeals());
            seals.removeIf(sealInfo -> sealInfo.isInvisible() && !sealInfo.hasAchievedSealClient(Minecraft.getInstance().player));
            for (int i = 0; i < seals.size(); i++) {
                event.addWidget(new SealButton(seals.get(i), 5, 10 + 24 * i));
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRender(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (event.getGui() instanceof AdvancementsScreen || event.getGui().getClass().getName().equalsIgnoreCase("betteradvancements.gui.BetterAdvancementsScreen")) {
            Screen screen = event.getGui();
            screen.buttons.stream().filter(widget -> widget instanceof SealButton).forEach(widget -> widget.render(event.getMouseX(), event.getMouseY(), event.getRenderPartialTicks()));
        }
    }

    private static List<UUID> getPlayers(URL url) {
        try {
            List<UUID> players = new ArrayList();
            (new JsonParser()).parse(readUrl(url)).getAsJsonObject().get("uuid").getAsJsonArray().forEach((jsonElement) -> {
                players.add(UUID.fromString(jsonElement.getAsString()));
            });
            return players;
        } catch (IOException var2) {
            var2.printStackTrace();
            return new ArrayList();
        }
    }

    private static String readUrl(URL url) throws IOException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);

            return buffer.toString();
        } finally {
            if (reader != null)
                reader.close();
        }
    }
}
