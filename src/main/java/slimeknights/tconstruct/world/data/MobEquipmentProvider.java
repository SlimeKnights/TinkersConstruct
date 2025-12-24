package slimeknights.tconstruct.world.data;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import slimeknights.mantle.data.predicate.item.ItemPredicate;
import slimeknights.mantle.recipe.data.ItemNameOutput;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.data.FakeRegistryEntry;
import slimeknights.tconstruct.library.data.tinkering.AbstractMobEquipmentProvider;
import slimeknights.tconstruct.library.materials.RandomMaterial;
import slimeknights.tconstruct.tools.TinkerTools;

import java.util.List;

/** Provider for custom mob equipment */
public class MobEquipmentProvider extends AbstractMobEquipmentProvider {
  public MobEquipmentProvider(PackOutput output) {
    super(output, TConstruct.MOD_ID);
  }

  @Override
  protected void addEquipment() {
    RandomMaterial random = RandomMaterial.ancient();

    // piglins spawn with battle signs
    equip(TinkerTags.EntityTypes.PIGLINS)
      .slot(EquipmentSlot.MAINHAND)
      // only replace golden weapons, never a crossbow
      .match(ItemPredicate.set(Items.GOLDEN_SWORD, Items.GOLDEN_AXE))
      .tool(TinkerTools.battlesign)
      .material(random, random);
    // want different fluid lists for wither skeletons vs drowned
    equip(EntityType.DROWNED)
      .slot(EquipmentSlot.MAINHAND)
      // only replace empty hand
      .match(ItemPredicate.set(Items.AIR))
      .tool(TinkerTools.swasher)
      .fluid(TinkerTags.Fluids.DROWNED_SWASHER)
      .material(random, random, random);
    equip(EntityType.WITHER_SKELETON)
      .slot(EquipmentSlot.MAINHAND)
      .tool(TinkerTools.swasher)
      .fluid(TinkerTags.Fluids.WITHER_SKELETON_SWASHER)
      .material(random, random, random);
    // zombies spawn with melting pans
    equip("melting_pan", List.of(EntityType.ZOMBIE, EntityType.ZOMBIE_VILLAGER, EntityType.HUSK))
      .slot(EquipmentSlot.OFFHAND)
      .tool(TinkerTools.meltingPan)
      .material(random, random);
    // evil villagers spawn with war picks
    equip("war_pick", List.of(EntityType.ZOMBIE_VILLAGER, EntityType.VINDICATOR))
      .slot(EquipmentSlot.MAINHAND)
      .tool(TinkerTools.warPick)
      .material(random, random, random);
    // twilight forest compat
    String tf = "twilightforest";
    equip(FakeRegistryEntry.entity(new ResourceLocation(tf, "minotaur")), new ModLoadedCondition(tf))
      .slot(EquipmentSlot.MAINHAND)
      .tool(ItemNameOutput.fromName(TinkerTools.minotaurAxe.getId()))
      .material(random, random, random);
  }

  @Override
  public String getName() {
    return "Tinkers' Construct mob equipment";
  }
}
