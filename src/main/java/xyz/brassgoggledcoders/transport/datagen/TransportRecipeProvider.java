package xyz.brassgoggledcoders.transport.datagen;

import net.minecraft.data.*;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.common.Tags;
import xyz.brassgoggledcoders.transport.content.TransportBlocks;
import xyz.brassgoggledcoders.transport.content.TransportEngineModules;
import xyz.brassgoggledcoders.transport.content.TransportEntities;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class TransportRecipeProvider extends RecipeProvider {
    public TransportRecipeProvider(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void registerRecipes(@Nonnull Consumer<IFinishedRecipe> consumer) {
        //region Entities
        ShapedRecipeBuilder.shapedRecipe(TransportEntities.CARGO_MINECART_ITEM.get())
                .patternLine(" S ")
                .patternLine("RMR")
                .patternLine(" S ")
                .key('S', Ingredient.fromTag(Tags.Items.SLIMEBALLS))
                .key('R', Ingredient.fromTag(Tags.Items.DUSTS_REDSTONE))
                .key('M', Ingredient.fromItems(Items.MINECART))
                .addCriterion("has_item", hasItem(Items.MINECART))
                .build(consumer);
        //endregion
    }

    @Override
    @Nonnull
    public String getName() {
        return "Transport Recipes";
    }
}
