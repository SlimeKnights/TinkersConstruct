package slimeknights.tconstruct.common.data.model;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile.UncheckedModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.registration.CastItemObject;
import slimeknights.tconstruct.library.tools.part.MaterialItem;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tools.TinkerToolParts;
import slimeknights.tconstruct.world.TinkerWorld;

import static slimeknights.tconstruct.TConstruct.getResource;

@SuppressWarnings("UnusedReturnValue")
public class TinkerItemModelProvider extends ItemModelProvider {
  private final UncheckedModelFile GENERATED = new UncheckedModelFile("item/generated");
  public TinkerItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
    super(output, TConstruct.MOD_ID, existingFileHelper);
  }

  @Override
  protected void registerModels() {
    // tool parts //
    // rock
    part(TinkerToolParts.pickHead, "pickaxe/head").offset(-2, 1);
    part(TinkerToolParts.hammerHead, "sledge_hammer/head").offset(-3, 3);
    // axe
    part(TinkerToolParts.smallAxeHead, "hand_axe/head").offset(-2, 3);
    part(TinkerToolParts.broadAxeHead, "broad_axe/blade").offset(0, 3);
    // blades
    part(TinkerToolParts.smallBlade);
    part(TinkerToolParts.broadBlade, "cleaver/head").offset(-1, 1);
    // plates
    part(TinkerToolParts.adzeHead, "pickadze/adze").offset(-5, 1);
    part(TinkerToolParts.largePlate);
    // bows
    part(TinkerToolParts.bowLimb, "longbow/limb_bottom").offset(5, -2);
    part(TinkerToolParts.bowGrip, "crossbow/body").offset(-2, -2);
    part(TinkerToolParts.bowstring);
    part(TinkerToolParts.arrowHead, "ammo/arrow_head").offset(-4, 3);
    part(TinkerToolParts.arrowShaft, "ammo/arrow_shaft").offset(1, -1);;
    part(TinkerToolParts.fletching, "ammo/arrow_feather").offset(4, -5);;
    // other
    part(TinkerToolParts.toolBinding);
    part(TinkerToolParts.toolHandle);
    part(TinkerToolParts.toughHandle);
    part(TinkerToolParts.toughBinding);
    part(TinkerToolParts.repairKit);
    part(TinkerToolParts.fakeIngot, "parts/ingot");
    // armor
    TinkerToolParts.plating.forEach((slot, item) -> {
      MaterialModelBuilder<ItemModelBuilder> b = this.part(item, "armor/plate/" + slot.getName() + "/plating");
      if (slot == ArmorItem.Type.HELMET) {
        b.offset(0, 2);
      } else if (slot == ArmorItem.Type.LEGGINGS) {
        b.offset(0, 1);
      }
    });
    part(TinkerToolParts.maille);
    part(TinkerToolParts.shieldCore, "armor/plate/shield/core");

    // gauges
    generated(TinkerSmeltery.copperGauge, "block/smeltery/io/gauge");
    generated(TinkerSmeltery.obsidianGauge, "block/foundry/io/gauge");

    // casts //
    // basic
    basicItem(TinkerSmeltery.blankSandCast, "sand_cast/blank");
    basicItem(TinkerSmeltery.blankRedSandCast, "red_sand_cast/blank");
    cast(TinkerSmeltery.ingotCast);
    cast(TinkerSmeltery.nuggetCast);
    cast(TinkerSmeltery.gemCast);
    cast(TinkerSmeltery.rodCast);
    cast(TinkerSmeltery.repairKitCast);
    // compat
    cast(TinkerSmeltery.plateCast);
    cast(TinkerSmeltery.gearCast);
    cast(TinkerSmeltery.coinCast);
    cast(TinkerSmeltery.wireCast);
    // small heads
    cast(TinkerSmeltery.pickHeadCast);
    cast(TinkerSmeltery.smallAxeHeadCast);
    cast(TinkerSmeltery.smallBladeCast);
    cast(TinkerSmeltery.adzeHeadCast);
    // large heads
    cast(TinkerSmeltery.hammerHeadCast);
    cast(TinkerSmeltery.broadBladeCast);
    cast(TinkerSmeltery.broadAxeHeadCast);
    cast(TinkerSmeltery.largePlateCast);
    // bindings
    cast(TinkerSmeltery.toolBindingCast);
    cast(TinkerSmeltery.toughBindingCast);
    // tool rods
    cast(TinkerSmeltery.toolHandleCast);
    cast(TinkerSmeltery.toughHandleCast);
    // bow
    cast(TinkerSmeltery.bowLimbCast);
    cast(TinkerSmeltery.bowGripCast);
    basicItem(TinkerSmeltery.arrowCast.getId(), "cast/arrow");
    // armor
    cast(TinkerSmeltery.helmetPlatingCast);
    cast(TinkerSmeltery.chestplatePlatingCast);
    cast(TinkerSmeltery.leggingsPlatingCast);
    cast(TinkerSmeltery.bootsPlatingCast);
    cast(TinkerSmeltery.mailleCast);
    // dummy parts
    TinkerSmeltery.dummyPlating.forEach((type, item) -> basicItem(item, "tool/parts/plating_" + type.getName()));

    // world //
    // shards
    basicItem(TinkerWorld.steelShard, "materials/steel_shard");
    basicItem(TinkerWorld.knightmetalShard, "materials/knightmetal_shard");
    generated(TinkerWorld.steelCluster, "block/geode/steel_cluster");
    generated(TinkerWorld.knightmetalCluster, "block/geode/knightmetal_cluster");
  }

  @SuppressWarnings("deprecation") // no its not
  private ResourceLocation id(ItemLike item) {
    return BuiltInRegistries.ITEM.getKey(item.asItem());
  }

  /** Generated item with a texture */
  private ItemModelBuilder generated(ResourceLocation item, ResourceLocation texture) {
    return getBuilder(item.toString()).parent(GENERATED).texture("layer0", texture);
  }

  /** Generated item with a texture */
  private ItemModelBuilder generated(ResourceLocation item, String texture) {
    return generated(item, new ResourceLocation(item.getNamespace(), texture));
  }

  /** Generated item with a texture */
  private ItemModelBuilder generated(ItemLike item, String texture) {
    return generated(id(item), texture);
  }

  /** Generated item with a texture */
  private ItemModelBuilder basicItem(ResourceLocation item, String texture) {
    return generated(item, "item/" + texture);
  }

  /** Generated item with a texture */
  private ItemModelBuilder basicItem(ItemLike item, String texture) {
    return basicItem(id(item), texture);
  }


  /* Parts */

  /** Creates a part model with the given texture */
  private MaterialModelBuilder<ItemModelBuilder> part(ResourceLocation part, String texture) {
    return withExistingParent(part.getPath(), "forge:item/default")
      .texture("texture", getResource("item/tool/" + texture))
      .customLoader(MaterialModelBuilder::new);
  }

  /** Creates a part model in the parts folder */
  private MaterialModelBuilder<ItemModelBuilder> part(Item item, String texture) {
    return part(id(item), texture);
  }

  /** Creates a part model with the given texture */
  private MaterialModelBuilder<ItemModelBuilder> part(ItemObject<? extends MaterialItem> part, String texture) {
    return part(part.getId(), texture);
  }

  /** Creates a part model in the parts folder */
  private void part(ItemObject<? extends MaterialItem> part) {
    part(part, "parts/" + part.getId().getPath());
  }


  /** Creates models for the given cast object */
  private void cast(CastItemObject cast) {
    String name = cast.getName().getPath();
    basicItem(cast.getId(), "cast/" + name);
    basicItem(cast.getSand(), "sand_cast/" + name);
    basicItem(cast.getRedSand(), "red_sand_cast/" + name);
  }
}
