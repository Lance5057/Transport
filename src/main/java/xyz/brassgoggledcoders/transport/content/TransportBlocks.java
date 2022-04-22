package xyz.brassgoggledcoders.transport.content;

import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.Tags;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import xyz.brassgoggledcoders.transport.Transport;
import xyz.brassgoggledcoders.transport.block.jobsite.RailWorkerBenchBlock;
import xyz.brassgoggledcoders.transport.block.rail.DumpRailBlock;
import xyz.brassgoggledcoders.transport.block.rail.OneWayBoosterRailBlock;
import xyz.brassgoggledcoders.transport.block.storage.CapabilityStorageBlock;
import xyz.brassgoggledcoders.transport.blockentity.DumpRailBlockEntity;
import xyz.brassgoggledcoders.transport.blockentity.storage.EnergyStorageBlockEntity;
import xyz.brassgoggledcoders.transport.blockentity.storage.FluidStorageBlockEntity;
import xyz.brassgoggledcoders.transport.recipe.RailWorkerBenchRecipeBuilder;
import xyz.brassgoggledcoders.transport.util.BlockModelHelper;

import javax.annotation.Nonnull;

@SuppressWarnings("unused")
public class TransportBlocks {

    public static final BlockEntry<DumpRailBlock<IItemHandler>> ITEM_DUMP_RAIL = Transport.getRegistrate()
            .object("item_dump_rail")
            .block(DumpRailBlock::itemDumpRail)
            .transform(TransportBlocks::defaultRail)
            .blockstate(BlockModelHelper::straightPoweredRailBlockState)
            .transform(TransportBlocks::defaultRailItem)
            .recipe((context, provider) -> RailWorkerBenchRecipeBuilder.of(context.get())
                    .withInput(Ingredient.of(TransportItemTags.RAILS_IRON))
                    .save(provider)
            )
            .register();

    public static final BlockEntry<DumpRailBlock<IFluidHandler>> FLUID_DUMP_RAIL = Transport.getRegistrate()
            .object("fluid_dump_rail")
            .block(DumpRailBlock::fluidDumpRail)
            .transform(TransportBlocks::defaultRail)
            .blockstate(BlockModelHelper::straightPoweredRailBlockState)
            .transform(TransportBlocks::defaultRailItem)
            .recipe((context, provider) -> RailWorkerBenchRecipeBuilder.of(context.get())
                    .withInput(Ingredient.of(TransportItemTags.RAILS_IRON))
                    .save(provider)
            )
            .register();

    public static final BlockEntry<DumpRailBlock<IEnergyStorage>> ENERGY_DUMP_RAIL = Transport.getRegistrate()
            .object("energy_dump_rail")
            .block(DumpRailBlock::energyDumpRail)
            .transform(TransportBlocks::defaultRail)
            .blockstate(BlockModelHelper::straightPoweredRailBlockState)
            .transform(TransportBlocks::defaultRailItem)
            .recipe((context, provider) -> RailWorkerBenchRecipeBuilder.of(context.get())
                    .withInput(Ingredient.of(TransportItemTags.RAILS_IRON))
                    .save(provider)
            )
            .register();

    public static final RegistryEntry<BlockEntityType<DumpRailBlockEntity>> DUMP_RAIL_BLOCK_ENTITY = Transport.getRegistrate()
            .object("dump_rail")
            .blockEntity(DumpRailBlockEntity::new)
            .validBlock(ITEM_DUMP_RAIL)
            .validBlock(FLUID_DUMP_RAIL)
            .validBlock(ENERGY_DUMP_RAIL)
            .register();

    public static final BlockEntry<OneWayBoosterRailBlock> ONE_WAY_BOOSTER_RAIL = Transport.getRegistrate()
            .object("one_way_booster_rail")
            .block(OneWayBoosterRailBlock::new)
            .transform(TransportBlocks::defaultRail)
            .blockstate(BlockModelHelper::straightPoweredInvertedRailBlockState)
            .transform(TransportBlocks::defaultRailItem)
            .recipe((context, provider) -> RailWorkerBenchRecipeBuilder.of(context.get())
                    .withInput(Ingredient.of(TransportItemTags.RAILS_GOLD))
                    .save(provider)
            )
            .register();

    public static final BlockEntry<CapabilityStorageBlock<FluidStorageBlockEntity>> FLUID_STORAGE = Transport.getRegistrate()
            .object("fluid_storage")
            .block(properties -> new CapabilityStorageBlock<>(properties, FluidStorageBlockEntity::new))
            .blockstate(BlockModelHelper::storageBlock)
            .item()
            .build()
            .blockEntity(FluidStorageBlockEntity::new)
            .build()
            .register();

    public static final BlockEntry<CapabilityStorageBlock<EnergyStorageBlockEntity>> ENERGY_STORAGE = Transport.getRegistrate()
            .object("energy_storage")
            .block(properties -> new CapabilityStorageBlock<>(properties, EnergyStorageBlockEntity::new))
            .blockstate(BlockModelHelper::storageBlock)
            .item()
            .build()
            .blockEntity(EnergyStorageBlockEntity::new)
            .build()
            .register();

    public static final BlockEntry<RailWorkerBenchBlock> RAIL_WORKER_BENCH = Transport.getRegistrate()
            .object("rail_worker_bench")
            .block(RailWorkerBenchBlock::new)
            .blockstate((context, provider) -> provider.simpleBlock(
                    context.get(),
                    provider.models().getExistingFile(provider.modLoc("block/rail_worker_bench"))
            ))
            .item()
            .recipe((context, provider) -> ShapedRecipeBuilder.shaped(context.get())
                    .pattern("RRR")
                    .pattern("SSS")
                    .define('R', ItemTags.RAILS)
                    .define('S', Tags.Items.STONE)
                    .unlockedBy("has_item", RegistrateRecipeProvider.has(ItemTags.RAILS))
                    .save(provider)
            )
            .build()
            .register();

    @Nonnull
    public static <T extends BaseRailBlock> BlockBuilder<T, Registrate> defaultRail(BlockBuilder<T, Registrate> builder) {
        return builder.initialProperties(Material.DECORATION)
                .properties(properties -> properties.noCollission()
                        .strength(0.7F)
                        .sound(SoundType.METAL)
                )
                .addLayer(() -> RenderType::cutout)
                .tag(BlockTags.RAILS);
    }

    @Nonnull
    public static <T extends BaseRailBlock> BlockBuilder<T, Registrate> defaultRailItem(BlockBuilder<T, Registrate> builder) {
        return builder.item()
                .model((context, provider) -> provider.generated(context, provider.modLoc("block/rail/" + context.getName())))
                .tag(ItemTags.RAILS)
                .build();
    }

    public static void setup() {

    }
}
