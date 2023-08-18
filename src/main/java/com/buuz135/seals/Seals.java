package com.buuz135.seals;

import com.buuz135.seals.client.SealButton;
import com.buuz135.seals.config.SealManager;
import com.buuz135.seals.datapack.SealInfo;
import com.buuz135.seals.datapack.SealInfoSerializer;
import com.buuz135.seals.network.ClientSyncSealsMessage;
import com.buuz135.seals.network.SealRequestMessage;
import com.buuz135.seals.storage.SealWorldStorage;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
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

;

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

    public static DeferredRegister<RecipeSerializer<?>> RECIPE_SER = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, "seals");
    public static final RegistryObject<SealInfoSerializer> EMOJI_RECIPE_SERIALIZER = RECIPE_SER.register("seal", SealInfoSerializer::new);

    public static DeferredRegister<RecipeType<?>> RECIPE_TYPE = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, "seals");
    public static final RegistryObject<RecipeType<SealInfo>> SEAL_RECIPE_TYPE = RECIPE_TYPE.register("seal", () -> RecipeType.simple(new ResourceLocation("seals", "seal")));

    public Seals() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        RECIPE_SER.register(FMLJavaModLoadingContext.get().getModEventBus());
        RECIPE_TYPE.register(FMLJavaModLoadingContext.get().getModEventBus());

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        NETWORK.registerMessage(0, ClientSyncSealsMessage.class, ClientSyncSealsMessage::toBytes, packetBuffer -> new ClientSyncSealsMessage().fromBytes(packetBuffer), ClientSyncSealsMessage::handle);
        NETWORK.registerMessage(1, SealRequestMessage.class, SealRequestMessage::toBytes, packetBuffer -> new SealRequestMessage().fromBytes(packetBuffer), SealRequestMessage::handle);
        new Thread(() -> {
            try {
                PATREONS.addAll(getPlayers(new URL("https://raw.githubusercontent.com/Buuz135/Industrial-Foregoing/master/contributors.json")));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }).start();

    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        //ClientAdvancements advancementManager = new ClientAdvancements(Minecraft.getInstance());
    }


    @SubscribeEvent
    public void onRecipesUpdated(RecipesUpdatedEvent event) {
        SEAL_MANAGER.setSeals(event.getRecipeManager().getAllRecipesFor(SEAL_RECIPE_TYPE.get()));
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        Level world = event.getEntity().getLevel();
        if (world instanceof ServerLevel && event.getEntity() instanceof ServerPlayer) {
            NETWORK.sendTo(new ClientSyncSealsMessage(SealWorldStorage.get((ServerLevel) world).save(new CompoundTag())), ((ServerPlayer) event.getEntity()).connection.connection, NetworkDirection.PLAY_TO_CLIENT);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onGuiOpen(ScreenEvent.Init.Post event) {
        if (event.getScreen() instanceof AdvancementsScreen || event.getScreen().getClass().getName().equalsIgnoreCase("betteradvancements.gui.BetterAdvancementsScreen")) {
            List<SealInfo> seals = new ArrayList<>(SEAL_MANAGER.getSeals());
            seals.removeIf(sealInfo -> sealInfo.isInvisible() && !sealInfo.hasAchievedSealClient(Minecraft.getInstance().player));
            int guiLeft = 35;
            int guiTop = 30;
            for (int i = 0; i < seals.size(); i++) {
                event.addListener(new SealButton(seals.get(i), guiLeft - 26 * ((i / 6 + 1)), guiTop + 24 * (i % 6) - 6, true));
            }
        }

    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRender(ScreenEvent.Render.Post event) {
        if (event.getScreen() instanceof AdvancementsScreen || event.getScreen().getClass().getName().equalsIgnoreCase("betteradvancements.gui.BetterAdvancementsScreen")) {
            Screen screen = event.getScreen();
            Minecraft.getInstance().font.draw(event.getPoseStack(), Component.translatable("seals.seals").getString(), 8, 10, 0xFFFFFF);
            screen.children().stream().filter(widget -> widget instanceof SealButton).forEach(widget -> ((SealButton) widget).render(event.getPoseStack(), event.getMouseX(), event.getMouseY(), event.getPartialTick()));
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
