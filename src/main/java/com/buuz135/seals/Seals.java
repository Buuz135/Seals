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
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.RecipesUpdatedEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
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

@Mod("seals")
public class Seals {

    public static String MOD_ID = "seals";
    public static final SealManager SEAL_MANAGER = new SealManager();
    public static final PayloadRegistrar NETWORK = new PayloadRegistrar(MOD_ID);
    private static final Logger LOGGER = LogManager.getLogger();
    public static final List<UUID> PATREONS = new ArrayList<>();

    public static DeferredRegister<RecipeSerializer<?>> RECIPE_SER = DeferredRegister.create(Registries.RECIPE_SERIALIZER, MOD_ID);
    public static final DeferredHolder<RecipeSerializer<?>, SealInfoSerializer> EMOJI_RECIPE_SERIALIZER = RECIPE_SER.register("seal", SealInfoSerializer::new);

    public static DeferredRegister<RecipeType<?>> RECIPE_TYPE = DeferredRegister.create(Registries.RECIPE_TYPE, MOD_ID);
    public static final DeferredHolder<RecipeType<?>, RecipeType<SealInfo>> SEAL_RECIPE_TYPE = RECIPE_TYPE.register("seal", () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(MOD_ID, "seal")));

    public Seals(IEventBus modEventBus, ModContainer modContainer) {
        if (FMLEnvironment.dist.isClient()) {
            modEventBus.addListener(this::doClientStuff);
        }

        RECIPE_SER.register(modEventBus);
        RECIPE_TYPE.register(modEventBus);

        // Register ourselves for server and other game events we are interested in
        NeoForge.EVENT_BUS.register(this);

        NETWORK.playToServer(SealRequestMessage.TYPE, SealRequestMessage.CODEC, SealRequestMessage::handle);
        NETWORK.playToClient(ClientSyncSealsMessage.TYPE, ClientSyncSealsMessage.CODEC, ClientSyncSealsMessage::handle);

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

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onRecipesUpdated(RecipesUpdatedEvent event) {
        SEAL_MANAGER.setSeals(Minecraft.getInstance().level, event.getRecipeManager().getAllRecipesFor(Seals.SEAL_RECIPE_TYPE.get()).stream().map(RecipeHolder::value).toList());
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        Level world = event.getEntity().level();
        if (world instanceof ServerLevel && event.getEntity() instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.send(new ClientSyncSealsMessage(SealWorldStorage.get((ServerLevel) world).save(new CompoundTag(), null)));
        }
    }


    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onGuiOpen(ScreenEvent.Init.Pre event) {
        if (event.getScreen() instanceof AdvancementsScreen || event.getScreen().getClass().getName().equalsIgnoreCase("betteradvancements.gui.BetterAdvancementsScreen")) {
            List<SealInfo> seals = new ArrayList<>(SEAL_MANAGER.getSeals());
            seals.removeIf(sealInfo -> sealInfo.isInvisible() && !sealInfo.hasAchievedSealClient(Minecraft.getInstance().player));
            int guiLeft = 35 - 26;
            int guiTop = 30;
            int number = event.getScreen().height / 26;
            for (int i = 0; i < seals.size(); i++) {
                event.addListener(new SealButton(seals.get(i), guiLeft + 26 * ((i / number)), guiTop + 24 * (i % number) - 6, true));
            }
        }

    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRender(ScreenEvent.Render.Post event) {
        if (event.getScreen() instanceof AdvancementsScreen || event.getScreen().getClass().getName().equalsIgnoreCase("betteradvancements.gui.BetterAdvancementsScreen")) {
            Screen screen = event.getScreen();
            event.getGuiGraphics().drawString(Minecraft.getInstance().font, Component.translatable("seals.seals").getString(), 8, 10, 0xFFFFFF, false);
            screen.children().stream().filter(widget -> widget instanceof SealButton).forEach(widget -> ((SealButton) widget).render(event.getGuiGraphics(), event.getMouseX(), event.getMouseY(), event.getPartialTick()));
        }
    }

    /*@OnlyIn(Dist.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRender(RenderNameTagEvent event) {
        var entity = event.getEntity();
        var pose = event.getPoseStack();
        var partialTick = event.getPartialTick();
        var multiBufferSource = event.getMultiBufferSource();
        var packedLight = event.getPackedLight();
        if (ClientSealWorldStorage.SEALS.getClientSeals().containsKey(entity.getUUID().toString()) && Seals.SEAL_MANAGER.getSeal(ClientSealWorldStorage.SEALS.getClientSeals().get(entity.getUUID().toString())) != null) {
            var seal = Seals.SEAL_MANAGER.getSeal(ClientSealWorldStorage.SEALS.getClientSeals().get(entity.getUUID().toString()));
            //super.renderNameTag(entity, Component.translatable("seal." + seal.getSealLangKey()).withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.ITALIC), pose, multiBufferSource, packedLight, partialTick);
            if (seal.getIcon() instanceof ItemStackIcon icon) {
                Vec3 vec3 = entity.getAttachments().getNullable(EntityAttachment.NAME_TAG, 0, entity.getViewYRot(partialTick));
                pose.pushPose();

                pose.translate(vec3.x, vec3.y - 0.099f + 0.5f + 0.10, vec3.z);
                pose.last().pose().rotate(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
                pose.translate(0,-0.10,0);
                //pose.last().normal().rotate(Axis.ZP.rotationDegrees(-90.0F));
                //pose.mulPose(Axis.YN.rotation(Minecraft.getInstance().gameRenderer.getMainCamera().getYRot()));
                //pose.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());

                //pose.mulPose(Axis.YP.rotation(Minecraft.getInstance().player.yRotO * -0.017453292F));
                //pose.mulPose(Axis.YP.rotation(180));

                pose.translate(Minecraft.getInstance().font.width(Component.translatable("seal." + seal.getSealLangKey()).withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.ITALIC)) * -0.01f - 0.3, 0, 0);
                var scale = 0.5f;
                BakedModel model = Minecraft.getInstance().getItemRenderer().getModel(icon.getCachedStack(), Minecraft.getInstance().level, null, 0);
                if (!model.isGui3d()) {
                    scale = 0.3f;
                }
                pose.scale(scale, scale, 0.01F);
                Minecraft.getInstance().getItemRenderer().render(icon.getCachedStack(), ItemDisplayContext.GUI, false, pose, multiBufferSource, packedLight, OverlayTexture.NO_OVERLAY, model);
                pose.popPose();
            }

        }
    }*/

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
