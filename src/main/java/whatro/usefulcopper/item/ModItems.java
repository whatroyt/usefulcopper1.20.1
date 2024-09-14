package whatro.usefulcopper.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ToolMaterials;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import whatro.usefulcopper.Usefulcopper;
import whatro.usefulcopper.item.custom.CopperArmorItem;
import whatro.usefulcopper.item.custom.CopperMaceItem;
import whatro.usefulcopper.item.custom.CopperRevolverItem;
import whatro.usefulcopper.item.custom.CopperSpeedloaderItem;

public class ModItems {
    public static final Item COPPER_REVOLVER = registerItem("copper_revolver",
            new CopperRevolverItem(new FabricItemSettings().maxCount(1)));

    public static final Item COPPER_BULLET = registerItem("copper_bullet", new Item(new FabricItemSettings()));
    public static final Item COPPER_SPEEDLOADER = registerItem("copper_speedloader", new CopperSpeedloaderItem(new FabricItemSettings()));
    public static final Item COPPER_NUGGET = registerItem("copper_nugget", new Item(new FabricItemSettings()));
    public static final Item COPPER_MACE = registerItem("copper_mace", new CopperMaceItem(ToolMaterials.NETHERITE, 1, -3.0F, new FabricItemSettings().maxDamage(500)));

    public static final Item COPPER_HELMET = registerItem("copper_helmet",
            new CopperArmorItem(ModArmorMaterials.COPPER, ArmorItem.Type.HELMET, new FabricItemSettings()));
    public static final Item COPPER_CHESTPLATE = registerItem("copper_chestplate",
            new CopperArmorItem(ModArmorMaterials.COPPER, ArmorItem.Type.CHESTPLATE, new FabricItemSettings()));
    public static final Item COPPER_LEGGINGS = registerItem("copper_leggings",
            new CopperArmorItem(ModArmorMaterials.COPPER, ArmorItem.Type.LEGGINGS, new FabricItemSettings()));
    public static final Item COPPER_BOOTS = registerItem("copper_boots",
            new CopperArmorItem(ModArmorMaterials.COPPER, ArmorItem.Type.BOOTS, new FabricItemSettings()));

    private static void addItemsToIngredientItemGroup(FabricItemGroupEntries entries) {
        entries.add(COPPER_REVOLVER);
    }

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(Usefulcopper.MOD_ID, name), item);
    }

    public static void registerModItems() {
        Usefulcopper.LOGGER.info("Registering Mod Items for " + Usefulcopper.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(ModItems::addItemsToIngredientItemGroup);
    }
}
