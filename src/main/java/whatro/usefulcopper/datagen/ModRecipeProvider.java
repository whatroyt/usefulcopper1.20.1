package whatro.usefulcopper.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import whatro.usefulcopper.item.ModItems;

import java.util.function.Consumer;

public class ModRecipeProvider extends FabricRecipeProvider {

    public ModRecipeProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(Consumer<RecipeJsonProvider> exporter) {
        // Recipe for crafting 9 Copper Nuggets from 1 Copper Ingot
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.COPPER_NUGGET, 9)
                .input(Items.COPPER_INGOT) // Ingredient can be placed anywhere in the grid
                .criterion("has_copper_ingot", conditionsFromItem(Items.COPPER_INGOT))
                .offerTo(exporter);

        // Recipe for crafting 1 Copper Ingot from 9 Copper Nuggets
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Items.COPPER_INGOT)
                .pattern("XXX")
                .pattern("XXX")
                .pattern("XXX")
                .input('X', ModItems.COPPER_NUGGET)
                .criterion("has_copper_nugget", conditionsFromItem(ModItems.COPPER_NUGGET))
                .offerTo(exporter);

        // Recipe for crafting 1 Copper Speedloader from 6 Copper Nuggets
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.COPPER_SPEEDLOADER)
                .pattern("   ")
                .pattern("XXX")
                .pattern("XXX")
                .input('X', ModItems.COPPER_NUGGET)
                .criterion("has_copper_nugget", conditionsFromItem(ModItems.COPPER_NUGGET))
                .offerTo(exporter);

        // Recipe for crafting 1 Copper Revolver from this
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.COPPER_MACHINE_GUN)
                .pattern("R  ")
                .pattern("III")
                .pattern("I  ")
                .input('I', Items.COPPER_INGOT)
                .input('R', Items.REDSTONE)
                .criterion("has_copper_ingot", conditionsFromItem(Items.COPPER_INGOT))
                .criterion("has_redstone_dust", conditionsFromItem(Items.REDSTONE))
                .offerTo(exporter);

        // Recipe for crafting 1 Copper Revolver from this
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.COPPER_MACE)
                .pattern("CCC")
                .pattern("CCC")
                .pattern(" BD")
                .input('C', Items.COPPER_BLOCK)
                .input('B', Items.BLAZE_ROD)
                .input('D', Items.LIGHT_BLUE_DYE)
                .criterion("has_copper_block", conditionsFromItem(Items.COPPER_BLOCK))
                .criterion("has_blaze_rod", conditionsFromItem(Items.BLAZE_ROD))
                .criterion("has_dye", conditionsFromItem(Items.LIGHT_BLUE_DYE))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.COPPER_HELMET)
                .pattern("CCC")
                .pattern("C C")
                .pattern("   ")
                .input('C', Items.COPPER_INGOT)
                .criterion("has_copper_ingot", conditionsFromItem(Items.COPPER_INGOT))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.COPPER_CHESTPLATE)
                .pattern("C C")
                .pattern("CCC")
                .pattern("CCC")
                .input('C', Items.COPPER_INGOT)
                .criterion("has_copper_ingot", conditionsFromItem(Items.COPPER_INGOT))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.COPPER_LEGGINGS)
                .pattern("CCC")
                .pattern("C C")
                .pattern("C C")
                .input('C', Items.COPPER_INGOT)
                .criterion("has_copper_ingot", conditionsFromItem(Items.COPPER_INGOT))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.COPPER_BOOTS)
                .pattern("   ")
                .pattern("C C")
                .pattern("C C")
                .input('C', Items.COPPER_INGOT)
                .criterion("has_copper_ingot", conditionsFromItem(Items.COPPER_INGOT))
                .offerTo(exporter);

    }
}
