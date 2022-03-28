package xyz.brassgoggledcoders.transport.data.shellcontent;

import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.block.Blocks;
import xyz.brassgoggledcoders.transport.api.shellcontent.ShellContentCreatorInfo;
import xyz.brassgoggledcoders.transport.content.TransportBlocks;
import xyz.brassgoggledcoders.transport.data.shellcontent.builders.FluidStorageShellContentBuilder;
import xyz.brassgoggledcoders.transport.data.shellcontent.builders.ItemStorageShellContentBuilder;
import xyz.brassgoggledcoders.transport.shellcontent.storage.item.StorageSize;

import java.util.function.Consumer;

public class TransportShellContentDataProvider extends ShellContentDataProvider {
    public TransportShellContentDataProvider(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void gather(Consumer<ShellContentCreatorInfo> consumer) {
        ShellContentInfoBuilder.of(Blocks.BARREL)
                .withShellContentCreator(ItemStorageShellContentBuilder.of(StorageSize.THREE_BY_NINE))
                .build(consumer);

        ShellContentInfoBuilder.of(TransportBlocks.FLUID_STORAGE.get())
                .withShellContentCreator(FluidStorageShellContentBuilder.ofBuckets(100))
                .build(consumer);
    }
}
