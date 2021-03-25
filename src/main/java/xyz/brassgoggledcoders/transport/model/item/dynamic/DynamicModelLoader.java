package xyz.brassgoggledcoders.transport.model.item.dynamic;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.model.*;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import xyz.brassgoggledcoders.transport.Transport;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@EventBusSubscriber(value = Dist.CLIENT, modid = Transport.ID, bus = Bus.MOD)
public class DynamicModelLoader {
    private static final Set<RenderMaterial> requestedTextures = new HashSet<>();
    private static final Multimap<ModelWithTransforms, ModelResourceLocation> requestedModels = HashMultimap.create();
    private static final Map<ModelWithTransforms, IUnbakedModel> unbakedModels = new HashMap<>();

    @SubscribeEvent
    public static void modelBake(ModelBakeEvent evt) {
        Transport.LOGGER.debug("Baking models");
        for (Map.Entry<ModelWithTransforms, IUnbakedModel> unbaked : unbakedModels.entrySet()) {
            ModelRequest conf = unbaked.getKey().model;
            IModelTransform state;
            if (unbaked.getKey().transforms.isEmpty()) {
                state = ModelRotation.getModelRotation(conf.rotX, conf.rotY);
            } else {
                state = new SimpleUVModelTransform(ImmutableMap.copyOf(unbaked.getKey().transforms), conf.uvLock);
            }
            IBakedModel baked = unbaked.getValue().bakeModel(evt.getModelLoader(), ModelLoader.defaultTextureGetter(),
                    state, conf.name);
            for (ModelResourceLocation mrl : requestedModels.get(unbaked.getKey())) {
                evt.getModelRegistry().put(mrl, baked);
            }
        }
    }

    @SubscribeEvent
    public static void textureStitch(TextureStitchEvent.Pre evt) {
        if (!evt.getMap().getTextureLocation().equals(PlayerContainer.LOCATION_BLOCKS_TEXTURE))
            return;
        Transport.LOGGER.debug("Loading dynamic models");
        try {
            for (ModelWithTransforms reqModel : requestedModels.keySet()) {
                BlockModel model = ModelLoaderRegistry.ExpandedBlockModelDeserializer.INSTANCE.fromJson(reqModel.model.data, BlockModel.class);
                Set<Pair<String, String>> missingTexErrors = new HashSet<>();
                requestedTextures.addAll(model.getTextures(DynamicModelLoader::getVanillaModel, missingTexErrors));
                if (!missingTexErrors.isEmpty())
                    throw new RuntimeException("Missing textures: " + missingTexErrors);
                unbakedModels.put(reqModel, model);
            }
        } catch (Throwable throwable) {
            Transport.LOGGER.error("Failed to Load Dynamic Models", throwable);
        }

        Transport.LOGGER.debug("Stitching textures!");
        for (RenderMaterial rl : requestedTextures) {
            evt.addSprite(rl.getTextureLocation());
        }
    }

    @EventBusSubscriber(modid = Transport.ID, bus = Bus.FORGE)
    public static class ForgeBusSubscriber {
        @SubscribeEvent(priority = EventPriority.LOW)
        public static void modelRegistry(ModelRegistryEvent evt) {
            requestedTextures.clear();
            unbakedModels.clear();
        }
    }

    private static IUnbakedModel getVanillaModel(ResourceLocation loc) {
        if (loc.getPath().equals("builtin/generated")) {
            return Util.make(BlockModel.deserialize("{}"), (blockModel) -> blockModel.name = "generation marker");
        } else {
            return ModelLoader.defaultModelGetter().apply(loc);
        }
    }

    public static void requestModel(ModelRequest reqModel, ModelResourceLocation name) {
        requestModel(reqModel, name, ImmutableMap.of());
    }

    public static void requestModel(ModelRequest reqModel, ModelResourceLocation name,
                                    Map<ItemCameraTransforms.TransformType, TransformationMatrix> transforms) {
        requestedModels.put(new ModelWithTransforms(reqModel, transforms), name);
    }

    public static class ModelRequest {
        private final JsonObject data;
        private final int rotX;
        private final int rotY;
        private final boolean uvLock;
        private final ResourceLocation name;

        public ModelRequest(ResourceLocation name, ResourceLocation loader, JsonObject data, int rotX, int rotY, boolean uvLock) {
            this.name = name;
            this.data = data;
            this.rotX = rotX;
            this.rotY = rotY;
            this.uvLock = uvLock;
            Preconditions.checkArgument(!data.has("loader"));
            this.data.addProperty("loader", loader.toString());
        }

        public static ModelRequest obj(ResourceLocation loc, int rotY) {
            return withModel(loc, new ResourceLocation("forge", "obj"), rotY);
        }

        private static ModelRequest withModel(ResourceLocation model, ResourceLocation loader, int rotY) {
            JsonObject json = new JsonObject();
            json.addProperty("model", new ResourceLocation(model.getNamespace(), "models/" + model.getPath()).toString());
            json.addProperty("flip-v", true);
            return new ModelRequest(model, loader, json, 0, rotY, true);
        }
    }

    private static class ModelWithTransforms {
        final ModelRequest model;
        final Map<ItemCameraTransforms.TransformType, TransformationMatrix> transforms;

        private ModelWithTransforms(ModelRequest model, Map<ItemCameraTransforms.TransformType, TransformationMatrix> transforms) {
            this.model = model;
            this.transforms = transforms;
        }
    }
}