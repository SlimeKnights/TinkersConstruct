package slimeknights.tconstruct.common.data.loot;

import net.minecraft.data.server.EntityLootTableGenerator;
import net.minecraft.entity.EntityType;
import net.minecraft.loot.ConstantLootTableRange;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.UniformLootTableRange;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.LootingEnchantLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.world.TinkerWorld;

import java.util.Objects;
import java.util.stream.Collectors;

public class TConstructEntityLootTables extends EntityLootTableGenerator {

  @Override
  protected Iterable<EntityType<?>> getKnownEntities() {
    return ForgeRegistries.ENTITIES.getValues().stream()
                                   .filter((block) -> TConstruct.modID.equals(Objects.requireNonNull(block.getRegistryName()).getNamespace()))
                                   .collect(Collectors.toList());
  }

  @Override
  protected void addTables() {
    this.register(TinkerWorld.skySlimeEntity.get(), LootTable.builder().pool(LootPool.builder().rolls(ConstantLootTableRange.create(1)).with(ItemEntry.builder(TinkerCommons.slimeball.get(SlimeType.SKY)).apply(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F))).apply(LootingEnchantLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F))))));
  }
}
