package slimeknights.tconstruct.library.tools.nbt;

import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.tconstruct.library.tools.ToolCore;

import java.util.Optional;

/**
 * The data of t he tool. Persisted in NBT.
 */
public class ToolData {

  protected static final String TAG_ITEM = "tic_item";
  protected static final String TAG_MATERIALS = "tic_materials";
  protected static final String TAG_STATS = "tic_stats";

  // tool so we don't need categories and original stats?

  public final ToolCore toolItem;
  //components=materials
  public final MaterialNBT materials;

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

  public ToolData(ToolCore toolItem, MaterialNBT materials) {
    this.toolItem = toolItem;
    this.materials = materials;
  }


  // todo: keep backing NBT and lazily initialize all the values? Might be worth it.. or a severe case of optimizing too early

  public static ToolData readFromNBT(CompoundNBT nbt) {
    ToolCore toolCore = deserializeItem(nbt);
    MaterialNBT materialNBT = MaterialNBT.readFromNBT(nbt.get(TAG_MATERIALS));

    return new ToolData(toolCore, materialNBT);
  }

  private static ToolCore deserializeItem(CompoundNBT nbt) {
    ResourceLocation itemRegistryName = new ResourceLocation(nbt.getString(TAG_ITEM));
    Item item = ForgeRegistries.ITEMS.getValue(itemRegistryName);
    if(item instanceof ToolCore) {
      return (ToolCore) item;
    }
    // todo: can we have a default?
    return null;
  }

  public CompoundNBT serializeToNBT() {
    CompoundNBT nbt = new CompoundNBT();

    nbt.put(TAG_ITEM, serializeItem());
    nbt.put(TAG_MATERIALS, materials.serializeToNBT());
    //nbt.put(TAG_MATERIALS, stats.write();)

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

  private StringNBT serializeItem() {
    return Optional.ofNullable(toolItem)
      .map(Item::getRegistryName)
      .map(ResourceLocation::toString)
      .map(StringNBT::new)
      .orElse(new StringNBT());
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
