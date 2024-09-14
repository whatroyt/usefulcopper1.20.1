package whatro.usefulcopper.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import whatro.usefulcopper.Usefulcopper;

public class ModItemGroups {
    public static final ItemGroup COPPER_GROUP = Registry.register(Registries.ITEM_GROUP,
            new Identifier(Usefulcopper.MOD_ID, "copper_revolver"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.usefulcopper"))
                    .icon(() -> new ItemStack(ModItems.COPPER_NUGGET)).entries((displayContext, entries) -> {
                        entries.add(ModItems.COPPER_MACE);
                        entries.add(ModItems.COPPER_HELMET);
                        entries.add(ModItems.COPPER_CHESTPLATE);
                        entries.add(ModItems.COPPER_LEGGINGS);
                        entries.add(ModItems.COPPER_BOOTS);
                        entries.add(ModItems.COPPER_REVOLVER);
                        entries.add(ModItems.COPPER_SPEEDLOADER);
                        entries.add(ModItems.COPPER_NUGGET);

                    }).build());


    public static void registerItemGroups() {
        Usefulcopper.LOGGER.info("Registering Item Groups for " + Usefulcopper.MOD_ID);
    }
}
