package slimeknights.tconstruct.tools.data.material;

import net.minecraft.data.PackOutput;
import net.minecraft.tags.FluidTags;
import net.minecraftforge.common.crafting.conditions.OrCondition;
import slimeknights.mantle.recipe.condition.TagFilledCondition;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.data.material.AbstractMaterialDataProvider;
import slimeknights.tconstruct.library.materials.definition.MaterialId;

import static slimeknights.mantle.Mantle.commonResource;

public class MaterialDataProvider extends AbstractMaterialDataProvider {
  public MaterialDataProvider(PackOutput packOutput) {
    super(packOutput);
  }

  @Override
  public String getName() {
    return "Tinker's Construct Materials";
  }

  @Override
  protected void addMaterials() {
    // tier 1
    material(MaterialIds.wood  ).tier(0).sort(ORDER_GENERAL).craftable();
    material(MaterialIds.rock  ).tier(1).sort(ORDER_HARVEST).craftable();
    material(MaterialIds.flint ).tier(1).sort(ORDER_WEAPON ).craftable();
    material(MaterialIds.copper).tier(1).sort(ORDER_SPECIAL).craftable();
    material(MaterialIds.bone  ).tier(1).sort(ORDER_SPECIAL).craftable();
    material(MaterialIds.bamboo).tier(1).sort(ORDER_RANGED ).craftable();
    // tier 1 - end
    material(MaterialIds.chorus).tier(1).sort(ORDER_END).craftable();
    // tier 1 - binding
    material(MaterialIds.string ).tier(0).sort(ORDER_GENERAL).craftable();
    material(MaterialIds.leather).tier(0).sort(ORDER_BINDING).craftable();
    material(MaterialIds.vine   ).tier(1).sort(ORDER_BINDING).craftable();
    // tier 1 - shield cores
    material(MaterialIds.cactus).tier(1).sort(ORDER_BINDING).craftable();
    // tier 1 - ammo
    material(MaterialIds.feather).tier(0).sort(ORDER_GENERAL).craftable();
    material(MaterialIds.wool   ).tier(1).sort(ORDER_BINDING).craftable();
    material(MaterialIds.leaves ).tier(1).sort(ORDER_BINDING).craftable();
    material(MaterialIds.paper  ).tier(1).sort(ORDER_BINDING).craftable();

    // tier 2
    material(MaterialIds.iron       ).tier(2).sort(ORDER_GENERAL);
    material(MaterialIds.searedStone).tier(2).sort(ORDER_HARVEST);
    material(MaterialIds.venombone  ).tier(2).sort(ORDER_WEAPON ).craftable();
    material(MaterialIds.slimewood  ).tier(2).sort(ORDER_SPECIAL).craftable();
    material(MaterialIds.slimeskin  ).tier(2).sort(ORDER_BINDING);
    material(MaterialIds.gold       ).tier(2).sort(ORDER_REPAIR );
    // tier 2 - nether
    material(MaterialIds.scorchedStone).tier(2).sort(ORDER_NETHER);
    material(MaterialIds.necroticBone ).tier(2).sort(ORDER_NETHER).craftable();
    // tier 2 - end
    material(MaterialIds.whitestone).tier(2).sort(ORDER_END).craftable();
    // tier 2 - binding
    material(MaterialIds.skyslimeVine).tier(2).sort(ORDER_BINDING).craftable();
    material(MaterialIds.weepingVine ).tier(2).sort(ORDER_BINDING).craftable();
    material(MaterialIds.twistingVine).tier(2).sort(ORDER_BINDING).craftable();
    // slimesuit
    material(MaterialIds.turtle  ).tier(2).sort(ORDER_BINDING).craftable();
    material(MaterialIds.nautilus).tier(2).sort(ORDER_BINDING).craftable();
    // tier 2 - ammo
    material(MaterialIds.amethyst  ).tier(2).sort(ORDER_REPAIR);
    material(MaterialIds.prismarine).tier(2).sort(ORDER_REPAIR).craftable();
    material(MaterialIds.earthslime).tier(2).sort(ORDER_REPAIR).craftable();
    material(MaterialIds.skyslime  ).tier(2).sort(ORDER_REPAIR).craftable();
    material(MaterialIds.blaze     ).tier(2).sort(ORDER_REPAIR).craftable();
    material(MaterialIds.enderPearl).tier(2).sort(ORDER_REPAIR);
    material(MaterialIds.glass     ).tier(2).sort(ORDER_REPAIR);
    material(MaterialIds.slimeball ).tier(2).sort(ORDER_REPAIR).craftable();
    material(MaterialIds.gunpowder ).tier(2).sort(ORDER_REPAIR).craftable();
    material(MaterialIds.redstone  ).tier(2).sort(ORDER_REPAIR).craftable();

    // tier 3
    material(MaterialIds.slimesteel    ).tier(3).sort(ORDER_GENERAL);
    material(MaterialIds.amethystBronze).tier(3).sort(ORDER_HARVEST);
    material(MaterialIds.nahuatl       ).tier(3).sort(ORDER_WEAPON ).craftable();
    material(MaterialIds.obsidian      ).tier(3).sort(ORDER_WEAPON );
    material(MaterialIds.roseGold      ).tier(3).sort(ORDER_SPECIAL);
    material(MaterialIds.pigIron       ).tier(3).sort(ORDER_SPECIAL);
    // tier 3 (nether)
    material(MaterialIds.steel ).tier(3).sort(ORDER_NETHER);
    material(MaterialIds.cobalt).tier(3).sort(ORDER_NETHER);
    // tier 3 - binding
    material(MaterialIds.darkthread).tier(3).sort(ORDER_BINDING);
    material(MaterialIds.ichorskin ).tier(3).sort(ORDER_BINDING);
    // tier 3 - ammo
    material(MaterialIds.magnetite).tier(3).sort(ORDER_REPAIR).craftable();
    material(MaterialIds.quartz   ).tier(3).sort(ORDER_REPAIR + ORDER_NETHER);
    material(MaterialIds.glowstone).tier(3).sort(ORDER_REPAIR + ORDER_NETHER).craftable();
    material(MaterialIds.ichor    ).tier(3).sort(ORDER_REPAIR + ORDER_NETHER).craftable();
    material(MaterialIds.kobold   ).tier(3).sort(ORDER_REPAIR + ORDER_NETHER).craftable();
    material(MaterialIds.magma    ).tier(3).sort(ORDER_REPAIR + ORDER_NETHER).craftable();
    // tier 3 - misc
    material(MaterialIds.ice    ).tier(3).sort(ORDER_BINDING).craftable();
    material(MaterialIds.jadeite).tier(3).sort(ORDER_BINDING).craftable();

    // tier 4
    material(MaterialIds.queensSlime).tier(4).sort(ORDER_GENERAL);
    material(MaterialIds.cinderslime).tier(4).sort(ORDER_GENERAL);
    material(MaterialIds.hepatizon  ).tier(4).sort(ORDER_HARVEST);
    material(MaterialIds.manyullyn  ).tier(4).sort(ORDER_WEAPON );
    material(MaterialIds.blazingBone).tier(4).sort(ORDER_SPECIAL).craftable();
    material(MaterialIds.knightmetal).tier(4).sort(ORDER_END    );
    material(MaterialIds.knightslime).tier(4).sort(ORDER_END    );
    material(MaterialIds.ancient    ).tier(4).sort(ORDER_NETHER ).hidden();
    // tier 4 - binding
    material(MaterialIds.jeweledHide   ).tier(4).sort(ORDER_BINDING);
    material(MaterialIds.ancientHide   ).tier(4).sort(ORDER_BINDING).hidden();
    material(MaterialIds.blazewood     ).tier(4).sort(ORDER_BINDING).craftable();
    material(MaterialIds.enderslimeVine).tier(4).sort(ORDER_BINDING).craftable();
    // tier 4 - ammo
    material(MaterialIds.shulker    ).tier(4).sort(ORDER_REPAIR).craftable();
    material(MaterialIds.dragonScale).tier(4).sort(ORDER_REPAIR).craftable();
    material(MaterialIds.enderslime ).tier(4).sort(ORDER_REPAIR).craftable();
    material(MaterialIds.knightly   ).tier(4).sort(ORDER_REPAIR).craftable();
    material(MaterialIds.endRod     ).tier(4).sort(ORDER_REPAIR).craftable();

    // slimesuit
    material(MaterialIds.clay ).tier(2).sort(ORDER_REPAIR + 5);
    material(MaterialIds.honey).tier(2).sort(ORDER_REPAIR + 5);
    material(MaterialIds.blood).tier(5).sort(ORDER_REPAIR).hidden();
    // slimesuit parts
    material(MaterialIds.horn   ).tier(1).sort(ORDER_REPAIR).craftable();
    material(MaterialIds.cheese ).tier(2).sort(ORDER_REPAIR).craftable();
    material(MaterialIds.phantom).tier(2).sort(ORDER_REPAIR + 5).craftable();

    // tier 2 (mod integration)
    material(MaterialIds.osmium  ).tier(2).sort(ORDER_COMPAT + ORDER_GENERAL).compatMetal();
    material(MaterialIds.lead    ).tier(2).sort(ORDER_COMPAT + ORDER_HARVEST).compatMetal();
    material(MaterialIds.silver  ).tier(2).sort(ORDER_COMPAT + ORDER_WEAPON ).compatMetal();
    material(MaterialIds.aluminum).tier(2).sort(ORDER_COMPAT + ORDER_RANGED ).compatMetal();
    // ironwood works in a part builder even though its ingots
    material(MaterialIds.ironwood).tier(2).sort(ORDER_COMPAT + ORDER_GENERAL).craftable().compatMetal();
    // treated wood comes from treated wood or creosote oil
    material(MaterialIds.treatedWood).tier(2).sort(ORDER_COMPAT + ORDER_GENERAL).craftable()
      .compat(tagExistsCondition("treated_wood"), new TagFilledCondition<>(FluidTags.create(commonResource("creosote"))));
    // tier 3 (mod integration)
    material(MaterialIds.electrum       ).tier(3).sort(ORDER_COMPAT + ORDER_GENERAL).compatAlloy("silver");
    material(MaterialIds.bronze         ).tier(3).sort(ORDER_COMPAT + ORDER_HARVEST).compatAlloy("tin");
    material(MaterialIds.constantan     ).tier(3).sort(ORDER_COMPAT + ORDER_HARVEST).compatAlloy("nickel");
    material(MaterialIds.invar          ).tier(3).sort(ORDER_COMPAT + ORDER_WEAPON ).compatAlloy("nickel");
    material(MaterialIds.pewter         ).tier(3).sort(ORDER_COMPAT + ORDER_WEAPON ).compatAlloy("tin", "lead");
    material(MaterialIds.platedSlimewood).tier(3).sort(ORDER_COMPAT + ORDER_SPECIAL).compatAlloy("zinc");
    material(MaterialIds.necronium      ).tier(3).sort(ORDER_COMPAT + ORDER_WEAPON ).compatMetal("uranium").craftable();
    material(MaterialIds.steeleaf       ).tier(3).sort(ORDER_COMPAT + ORDER_SPECIAL).compatMetal();
    // tier 4 (mod integration)
    material(MaterialIds.fiery   ).tier(4).sort(ORDER_COMPAT + ORDER_END   ).compatMetal();
    material(MaterialIds.nicrosil).tier(4).sort(ORDER_COMPAT + ORDER_WEAPON).compatAlloy("tin", "nickel", "chromium");

    // redirects
    // rose gold is most comparable to chain as you can use the extra slot for reinforced
    material("chain").redirect(MaterialIds.roseGold);
    // bloodbone reworked into venombone
    material("bloodbone").redirect(MaterialIds.venombone);
    // zombies now use leather instead of flesh for their skull
    material("rotten_flesh").redirect(MaterialIds.leather);
    material("platinum").redirect(MaterialIds.searedStone);
    material("tungsten")
      .redirect(MaterialIds.lead, tagExistsCondition("ingots/lead"))
      .redirect(MaterialIds.invar, new OrCondition(tagExistsCondition("ingots/invar"), tagExistsCondition("ingots/nickel")))
      .redirect(MaterialIds.iron);
  }

  /** Makes a material under our mod ID */
  private MaterialBuilder material(String name) {
    return material(new MaterialId(TConstruct.MOD_ID, name));
  }
}
