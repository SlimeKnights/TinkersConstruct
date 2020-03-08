package slimeknights.tconstruct.library.tinkering;

import com.google.common.collect.ImmutableList;
import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.library.MaterialRegistry;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;

import java.util.List;

public class PartMaterialType {

  // ANY of these has to match
  private final IMaterialItem neededPart;
  // ALL of the material stats have to be there
  private final List<MaterialStatsId> neededTypes;

  public PartMaterialType(IMaterialItem part, MaterialStatsId... statIDs) {
    neededPart = part;
    neededTypes = ImmutableList.copyOf(statIDs);
  }

  public boolean isValid(ItemStack stack) {
    if (stack.getItem() instanceof IMaterialItem) {
      IMaterialItem toolPart = (IMaterialItem) stack.getItem();
      return isValid(toolPart, toolPart.getMaterial(stack));
    }
    return false;
  }

  public boolean isValid(IMaterialItem part, IMaterial material) {
    return isValidItem(part) && isValidMaterial(material);
  }

  public boolean isValidItem(IMaterialItem part) {
    return neededPart == part;
  }

  public boolean isValidMaterial(IMaterial material) {
    return neededTypes.stream().allMatch(
      statsId -> MaterialRegistry.getInstance().getMaterialStats(material.getIdentifier(), statsId).isPresent()
    );
  }

  /**
   * Returns true if the passed stat is used by this.</br>
   * This does NOT mean that a material having this stat is usable, since multiple stats might be required!
   */
  public boolean usesStat(MaterialStatsId statID) {
    for (MaterialStatsId type : neededTypes) {
      if (type.equals(statID)) {
        return true;
      }
    }

    return false;
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
//  public Set<IToolPart> getPossibleParts() {
//    return ImmutableSet.of(neededPart);
//  }
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
