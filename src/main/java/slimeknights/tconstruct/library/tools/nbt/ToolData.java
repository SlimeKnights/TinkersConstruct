package slimeknights.tconstruct.library.tools.nbt;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.With;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.tools.ToolCore;

import java.util.List;

/**
 * The data of t he tool. Persisted in NBT.
 */
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ToolData {

  protected static final String TAG_ITEM = "tic_item";
  protected static final String TAG_MATERIALS = "tic_materials";
  protected static final String TAG_STATS = "tic_stats";

  // tool so we don't need categories and original stats?
  private final ToolItemNBT toolItem;
  //components=materials
  private final MaterialNBT materials;
  @With(AccessLevel.PRIVATE)
  private final StatsNBT stats;

  //stats
  // modifiers
//  private final List<IModifier> modifiers;
  // traits
//  private final List<ITrait> traits;
  // custom special data
  // used modifiers?
  // categories are present in the tools NBT since we need the for NBT only operations where we don't have the tool
  //List<Category> categories;
  // original stats

  //private final StatsNBT stats;

  public ToolCore getToolItem() {
    return toolItem.getToolItem();
  }

  public List<IMaterial> getMaterials() {
    return materials.getMaterials();
  }

  public StatsNBT getStats() {
    return stats;
  }

  public ToolData createNewDataWithBroken(boolean isBroken) {
    StatsNBT newStats = getStats().withBroken(isBroken);
    return this.withStats(newStats);
  }

  public void updateStack(ItemStack stack) {
    stack.getOrCreateTag().merge(serializeToNBT());
  }

  // todo: keep backing NBT and lazily initialize all the values? Might be worth it.. or a severe case of optimizing too early

  public static boolean isBroken(ItemStack stack) {
    return from(stack).getStats().broken;
  }

  public static ToolData from(ItemStack stack) {
    return readFromNBT(stack.getOrCreateTag());
  }

  public static ToolData readFromNBT(CompoundNBT nbt) {
    ToolItemNBT toolCore = ToolItemNBT.readFromNBT(nbt.get(TAG_ITEM));
    MaterialNBT materialNBT = MaterialNBT.readFromNBT(nbt.get(TAG_MATERIALS));
    StatsNBT statsNBT = StatsNBT.readFromNBT(nbt.get(TAG_STATS));

    return new ToolData(toolCore, materialNBT, statsNBT);
  }

  public CompoundNBT serializeToNBT() {
    CompoundNBT nbt = new CompoundNBT();

    nbt.put(TAG_ITEM, toolItem.serializeToNBT());
    nbt.put(TAG_MATERIALS, materials.serializeToNBT());
    nbt.put(TAG_STATS, stats.serializeToNBT());

    // base data about the tool, what it's built out of
//    nbt.put(Tags.BASE, null);
//      nbt.put("item", null);
//      nbt.put(Tags.BASE_MATERIALS, null);
//      nbt.put(Tags.BASE_MODIFIERS, null);
//      nbt.put(Tags.BASE_USED_MODIFIERS, null); // todo: maybe replace with free modifier after all?
//    // modifier custom data - the usage data the modifiers themselves keep
//    nbt.put(Tags.TOOL_MODIFIERS, null);
//    // traits custom data - the usage data the traits themselves keep
//    nbt.put(Tags.TOOL_TRAITS, null);
//    // the stats of the tool
//    nbt.put(Tags.TOOL_STATS, null);

    return nbt;
  }

//
//
//  /** Checks whether an Item built from materials has only valid materials. Uses the standard NBT to determine materials. */
//  public boolean hasValidMaterials(ItemStack stack) {
//    // checks if the materials used support all stats needed
//    NBTTagList list = TagUtil.getBaseMaterialsTagList(stack);
//    List<Material> materials = TinkerUtil.getMaterialsFromTagList(list);
//
//    // something went wrooooong
//    if(materials.size() != requiredComponents.length) {
//      return false;
//    }
//
//    // check if all materials used have the stats needed
//    for(int i = 0; i < materials.size(); i++) {
//      Material material = materials.get(i);
//      PartMaterialType required = requiredComponents[i];
//      if(!required.isValidMaterial(material)) {
//        return false;
//      }
//    }
//
//    return true;
//  }
//
//  /**
//   * Builds the NBT for a new tinker item with the given data.
//   *
//   * @param materials Materials to build with. Have to be in the correct order. No nulls!
//   * @return The built nbt
//   */
//  public CompoundNBT buildItemNBT() {
//    CompoundNBT basetag = new CompoundNBT();
//    CompoundNBT toolTag = buildTag(materials);
//    CompoundNBT dataTag = buildData(materials);
//
//    basetag.put(Tags.BASE, dataTag);
//    basetag.put(Tags.TOOL_STATS, toolTag);
//    // copy of the original tool data
//    basetag.put(Tags.TOOL_DATA_ORIG, toolTag.copy());
//
//    // save categories on the tool
//    //TagUtil.setCategories(basetag, getCategories());
//
//    // add traits
//    addMaterialTraits(basetag, materials);
//
//    // fire toolbuilding event
//    TinkerEvent.OnItemBuilding.fireEvent(basetag, ImmutableList.copyOf(materials), this);
//
//    return basetag;
//  }
//
//  /**
//   * Creates an NBT Tag with the materials that were used to build the item.
//   */
//  private CompoundNBT buildData(List<Material> materials) {
//    CompoundNBT base = new CompoundNBT();
//    NBTTagList materialList = new NBTTagList();
//
//    for(Material material : materials) {
//      materialList.appendTag(new NBTTagString(material.identifier));
//    }
//
//    // pre-type base-modifier list
//    NBTTagList modifierList = new NBTTagList();
//    // we cannot set the type directly, but it gets typed by adding a tag, so we add and remove one
//    modifierList.appendTag(new NBTTagString());
//    modifierList.removeTag(0);
//
//    base.setTag(Tags.BASE_MATERIALS, materialList);
//    base.setTag(Tags.BASE_MODIFIERS, modifierList);
//
//    return base;
//  }
//
//
//  public void addMaterialTraits(CompoundNBT root, List<Material> materials) {
//    int size = requiredComponents.length;
//    // safety
//    if(materials.size() < size) {
//      size = materials.size();
//    }
//    // add corresponding traits per material usage
//    for(int i = 0; i < size; i++) {
//      PartMaterialType required = requiredComponents[i];
//      Material material = materials.get(i);
//      for(ITrait trait : required.getApplicableTraitsForMaterial(material)) {
//        ToolBuilder.addTrait(root, trait, material.materialTextColor);
//      }
//    }
//  }
}
