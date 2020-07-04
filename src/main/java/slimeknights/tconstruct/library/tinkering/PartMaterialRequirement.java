package slimeknights.tconstruct.library.tinkering;

import com.google.common.collect.ImmutableList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.library.MaterialRegistry;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class PartMaterialRequirement {

  // ANY of these has to match
  private final Supplier<? extends Item> neededPart;
  // ALL of the material stats have to be there
  private final List<MaterialStatsId> neededTypes;

  public PartMaterialRequirement(Supplier<? extends Item> part, MaterialStatsId... statIDs) {
    this.neededPart = part;
    this.neededTypes = ImmutableList.copyOf(statIDs);
  }

  public boolean isValid(ItemStack stack) {
    if (stack.getItem() instanceof IMaterialItem) {
      IMaterialItem toolPart = (IMaterialItem) stack.getItem();

      return isValid(stack.getItem(), toolPart.getMaterial(stack));
    }
    return false;
  }

  public boolean isValid(Item part, IMaterial material) {
    return isValidItem(part) && isValidMaterial(material);
  }

  public boolean isValidItem(Item part) {
    return Objects.equals(part.getRegistryName(), this.neededPart.get().getRegistryName());
  }

  public boolean isValidMaterial(IMaterial material) {
    return this.neededTypes.stream().allMatch(
      statsId -> MaterialRegistry.getInstance().getMaterialStats(material.getIdentifier(), statsId).isPresent()
    );
  }

  /**
   * Returns true if the passed stat is used by this.</br>
   * This does NOT mean that a material having this stat is usable, since multiple stats might be required!
   */
  public boolean usesStat(MaterialStatsId statID) {
    for (MaterialStatsId type : this.neededTypes) {
      if (type.equals(statID)) {
        return true;
      }
    }

    return false;
  }

  public Item getPossiblePart() {
    return this.neededPart.get();
  }
//
//  public Collection<ITrait> getApplicableTraitsForMaterial(Material material) {
//    if(!isValidMaterial(material)) {
//      return ImmutableList.of();
//    }
//
//    ImmutableList.Builder<ITrait> traits = ImmutableList.builder();
//    // traits of the types used
//    for(MaterialStatsId type : neededTypes) {
//      traits.addAll(material.getAllTraitsForStats(type));
//    }
//
//    // use default trait if none is present
//    if(traits.build().isEmpty()) {
//      traits.addAll(material.getDefaultTraits());
//    }
//
//    return traits.build();
//  }
//
  // todo: readd
/*
  public static PartMaterialType head(IToolPart part) {
    return new PartMaterialType(part, MaterialTypes.HEAD);
  }

  public static PartMaterialType handle(IToolPart part) {
    return new PartMaterialType(part, MaterialTypes.HANDLE);
  }

  public static PartMaterialType extra(IToolPart part) {
    return new PartMaterialType(part, MaterialTypes.EXTRA);
  }

  public static PartMaterialType bow(IToolPart part) {
    return new PartMaterialType(part, MaterialTypes.BOW, MaterialTypes.HEAD);
  }

  public static PartMaterialType bowstring(IToolPart part) {
    return new PartMaterialType(part, MaterialTypes.BOWSTRING);
  }

  public static PartMaterialType arrowHead(IToolPart part) {
    return new PartMaterialType(part, MaterialTypes.HEAD, MaterialTypes.PROJECTILE);
  }

  public static PartMaterialType arrowShaft(IToolPart part) {
    return new PartMaterialType(part, MaterialTypes.SHAFT);
  }

  public static PartMaterialType fletching(IToolPart part) {
    return new PartMaterialType(part, MaterialTypes.FLETCHING);
  }

  public static PartMaterialType crossbow(IToolPart part) {
    return new PartMaterialType(part, MaterialTypes.HANDLE, MaterialTypes.EXTRA);
  }*/
}
