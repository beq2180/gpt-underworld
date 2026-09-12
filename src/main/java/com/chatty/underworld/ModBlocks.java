package com.chatty.underworld;

import com.chatty.underworld.portal.UnderworldPortalBlock;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public final class ModBlocks {
    public static final Block UNDIUM = registerBlock(
            "undium",
            new Block(settings("undium")
                    .mapColor(net.minecraft.block.MapColor.TERRACOTTA_PURPLE)
                    .strength(5.0F, 9.0F)
                    .sounds(BlockSoundGroup.DEEPSLATE)
                    .requiresTool()),
            true
    );

    public static final Block GROIL = registerBlock(
            "groil",
            new Block(settings("groil")
                    .mapColor(net.minecraft.block.MapColor.DARK_CRIMSON)
                    .strength(0.65F)
                    .sounds(BlockSoundGroup.ROOTED_DIRT)),
            true
    );

    public static final UnderworldPortalBlock UNDERWORLD_PORTAL = (UnderworldPortalBlock) registerBlock(
            "underworld_portal",
            new UnderworldPortalBlock(settings("underworld_portal")
                    .mapColor(net.minecraft.block.MapColor.TERRACOTTA_PURPLE)
                    .noCollision()
                    .nonOpaque()
                    .breakInstantly()
                    .luminance(state -> 11)
                    .sounds(BlockSoundGroup.GLASS)
                    .dropsNothing()),
            false
    );

    private static AbstractBlock.Settings settings(String name) {
        RegistryKey<Block> key = RegistryKey.of(RegistryKeys.BLOCK, UnderworldMod.id(name));
        return AbstractBlock.Settings.create().registryKey(key);
    }

    private static Block registerBlock(String name, Block block, boolean withItem) {
        Identifier id = UnderworldMod.id(name);
        Registry.register(Registries.BLOCK, id, block);

        if (withItem) {
            RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, id);
            Item.Settings itemSettings = new Item.Settings().registryKey(itemKey).useBlockPrefixedTranslationKey();
            Registry.register(Registries.ITEM, id, new BlockItem(block, itemSettings));
        }
        return block;
    }

    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS)
                .register(entries -> {
                    entries.add(UNDIUM);
                    entries.add(GROIL);
                });
    }

    private ModBlocks() {}
}
