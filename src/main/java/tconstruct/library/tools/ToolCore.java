package tconstruct.library.tools;

import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import java.util.ArrayList;
import java.util.List;

import tconstruct.library.TinkerRegistry;
import tconstruct.library.Util;
import tconstruct.library.materials.Material;
import tconstruct.library.materials.ToolMaterialStats;
import tconstruct.library.tinkering.Category;
import tconstruct.library.tinkering.PartMaterialType;
import tconstruct.library.tinkering.TinkersItem;
import tconstruct.library.utils.TagUtil;
import tconstruct.library.utils.ToolBuilder;
import tconstruct.library.utils.ToolHelper;
import tconstruct.library.utils.ToolTagUtil;
import tconstruct.library.utils.TooltipBuilder;

/**
 * Intermediate abstraction layer for all tools/melee weapons. This class has all the callbacks for blocks and enemies
 * so tools and weapons can share behaviour.
 */
public abstract class ToolCore extends TinkersItem {

  public ToolCore(PartMaterialType... requiredComponents) {
    super(requiredComponents);

    this.setCreativeTab(TinkerRegistry.tabTools);

    TinkerRegistry.registerTool(this);
    addCategory(Category.TOOL);
  }

  @Override
  public float getDigSpeed(ItemStack itemstack, IBlockState state) {
    return ToolHelper.calcDigSpeed(itemstack, state);
  }

  @Override
  public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
    // deal damage
    return true;
  }

  @Override
  public String[] getInformation(ItemStack stack) {
    TooltipBuilder info = new TooltipBuilder(stack);

    info.addDurability();
    if(hasCategory(Category.HARVEST)) {
      info.addHarvestLevel();
      info.addMiningSpeed();
    }
    if(hasCategory(Category.WEAPON)) {
      info.addAttack();
    }

    if(ToolHelper.getFreeModifiers(stack) > 0) {
      info.addFreeModifiers();
    }

    return info.getTooltip();
  }

  @Override
  public ItemStack buildItem(List<Material> materials) {
    ItemStack tool = super.buildItem(materials);

    // reset to prevent the ITALIC prepended by tooltip rendering
    tool.setStackDisplayName(EnumChatFormatting.RESET + getLocalizedToolName(materials.get(0)));

    return tool;
  }

  @Override
  public NBTTagCompound buildTag(List<Material> materials) {
    // assume a simple Head + Handle tool
    return ToolBuilder.buildSimpleTool(materials.get(0), materials.get(1)); // todo: remove or add safety checks
  }

  // Creative tab items
  @Override
  public void getSubItems(Item itemIn, CreativeTabs tab, List subItems) {
    for(Material head : TinkerRegistry.getAllMaterials()) {
      if(!head.hasStats(ToolMaterialStats.TYPE))
        continue;

      List<Material> mats = new ArrayList<Material>(requiredComponents.length);

      for(int i = 0; i < requiredComponents.length; i++) {
        mats.add(head);
      }

      ItemStack tool = buildItem(mats);
      subItems.add(tool);
    }
  }

  @Override
  public int getHarvestLevel(ItemStack stack, String toolClass) {
    if(this.getToolClasses(stack).contains(toolClass)) {
      NBTTagCompound tag = TagUtil.getToolTag(stack);
      // will return 0 if the tag has no info anyway
      return ToolTagUtil.getHarvestLevel(tag);
    }
    return super.getHarvestLevel(stack, toolClass);
  }

  /** A simple string identifier for the tool, used for identification in texture generation etc. */
  public String getIdentifier() {
    return Util.getItemLocation(this).getResourcePath();
  }

  /** The tools name completely without material information */
  public String getLocalizedToolName() {
    return Util.translate(getUnlocalizedName() + ".name");
  }

  /** The tools name with the given material. e.g. "Wooden Pickaxe" */
  public String getLocalizedToolName(Material material) {
    return material.getLocalizedItemName(getLocalizedToolName());
  }

  /** Returns info about the Tool. Displayed in the tool stations etc. */
  public String getLocalizedDescription() {
    return Util.translate(getUnlocalizedName() + ".desc");
  }
}
