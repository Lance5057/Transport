package xyz.brassgoggledcoders.transport.model.patternedraillayer;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.EmptyModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PatternedRailLayerBakedModel implements BakedModel {
    private final PatternedRailLayerItemOverrides itemOverrides;
    private final BakedModel background;

    public PatternedRailLayerBakedModel(BakedModel background) {
        this.background = background;
        this.itemOverrides = new PatternedRailLayerItemOverrides(background);
    }

    @Override
    @NotNull
    public List<BakedQuad> getQuads(@Nullable BlockState pState, @Nullable Direction pSide, @NotNull Random pRand) {
        return Collections.emptyList();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean usesBlockLight() {
        return false;
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @Override
    @NotNull
    public TextureAtlasSprite getParticleIcon() {
        return background.getParticleIcon(EmptyModelData.INSTANCE);
    }

    @Override
    @NotNull
    public ItemOverrides getOverrides() {
        return itemOverrides;
    }
}
