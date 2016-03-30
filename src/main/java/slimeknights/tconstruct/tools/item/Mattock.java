package slimeknights.tconstruct.tools.item;

import com.google.common.collect.ImmutableSet;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import java.util.List;

import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.HandleMaterialStats;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.HeadMaterialStats;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.AoeToolCore;
import slimeknights.tconstruct.library.tools.ToolNBT;
import slimeknights.tconstruct.library.utils.HarvestLevels;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.Tags;
import slimeknights.tconstruct.library.utils.TinkerUtil;
import slimeknights.tconstruct.library.utils.ToolHelper;
import slimeknights.tconstruct.library.utils.TooltipBuilder;
import slimeknights.tconstruct.tools.TinkerTools;

public class Mattock extends AoeToolCore {

  public static final ImmutableSet<net.minecraft.block.material.Material> effective_materials_axe =
      ImmutableSet.of(net.minecraft.block.material.Material.wood,
                      net.minecraft.block.material.Material.cactus,
                      net.minecraft.block.material.Material.plants,
                      net.minecraft.block.material.Material.vine,
                      net.minecraft.block.material.Material.gourd);

  public static final ImmutableSet<net.minecraft.block.material.Material> effective_materials_shovel =
      ImmutableSet.of(net.minecraft.block.material.Material.grass,
                      net.minecraft.block.material.Material.ground,
                      net.minecraft.block.material.Material.clay);

  public Mattock() {
    super(PartMaterialType.handle(TinkerTools.toolRod),
          PartMaterialType.head(TinkerTools.axeHead),
          PartMaterialType.head(TinkerTools.shovelHead));

    addCategory(Category.HARVEST);

    // unused, but we give mattock its own tool class
    this.setHarvestLevel("mattock", 0);
  }

  @Override
  public int getHarvestLevel(ItemStack stack, String toolClass) {
    if(toolClass == null) {
      return -1;
    }

    // axe harvestlevel
    if(toolClass.equals("axe")) {
      return getAxeLevel(stack);
    }
    // shovel harvestlevel
    else if(toolClass.equals("shovel")) {
      return getShovelLevel(stack);
    }

    // none of them
    return super.getHarvestLevel(stack, toolClass);
  }

  @Override
  public boolean isEffective(Block block) {
    return effective_materials_axe.contains(block.getMaterial()) || effective_materials_shovel.contains(block.getMaterial());
  }

  @Override
  public float miningSpeedModifier() {
    return 0.95f;
  }

  @Override
  public float damagePotential() {
    return 0.90f;
  }

  @Override
  public float knockback() {
    return 1.1f;
  }

  @Override
  public int[] getRepairParts() {
    return new int[] {1,2};
  }

  @Override
  public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
    if(ToolHelper.isBroken(stack)) {
      return false;
    }

    boolean ret = Items.diamond_hoe.onItemUse(stack, playerIn, worldIn, pos, side, hitX, hitY, hitZ);
    for(BlockPos blockPos : getAOEBlocks(stack, worldIn, playerIn, pos)) {
      if(ToolHelper.isBroken(stack)) {
        break;
      }

      ret |= Items.diamond_hoe.onItemUse(stack, playerIn, worldIn, blockPos, side, hitX, hitY, hitZ);
    }

    return ret;
  }

  @Override
  public boolean isAoeHarvestTool() {
    return false;
  }

  @Override
  public boolean canUseSecondaryItem() {
    return false;
  }

  @Override
  public List<String> getInformation(ItemStack stack, boolean detailed) {
    TooltipBuilder info = new TooltipBuilder(stack);

    info.addDurability(!detailed);

    // special axe harvest level
    String text = Util.translate("stat.mattock.axelevel.name");
    info.add(String.format("%s: %s", text, HarvestLevels.getHarvestLevelName(getAxeLevel(stack))) + EnumChatFormatting.RESET);

    // special shovel harvest level
    text = Util.translate("stat.mattock.shovellevel.name");
    info.add(String.format("%s: %s", text, HarvestLevels.getHarvestLevelName(getShovelLevel(stack))) + EnumChatFormatting.RESET);

    info.addMiningSpeed();
    info.addAttack();

    if(ToolHelper.getFreeModifiers(stack) > 0) {
      info.addFreeModifiers();
    }

    if(detailed) {
      info.addModifierInfo();
    }

    return info.getTooltip();
  }

  @Override
  public NBTTagCompound buildTag(List<Material> materials) {
    HandleMaterialStats handle = materials.get(0).getStatsOrUnknown(HandleMaterialStats.TYPE);
    HeadMaterialStats axe = materials.get(1).getStatsOrUnknown(HeadMaterialStats.TYPE);
    HeadMaterialStats shovel = materials.get(2).getStatsOrUnknown(HeadMaterialStats.TYPE);

    MattockToolNBT data = new MattockToolNBT();
    data.head(axe, shovel);
    data.handle(handle);

    // special harvest levels
    data.axeLevel = axe.harvestLevel;
    data.shovelLevel = shovel.harvestLevel;

    // base damage!
    data.attack += 3;

    // 3 free modifiers
    data.modifiers = DEFAULT_MODIFIERS;

    return data.get();
  }

  protected int getAxeLevel(ItemStack stack) {
    return new MattockToolNBT(TagUtil.getToolTag(stack)).axeLevel;
  }

  protected int getShovelLevel(ItemStack stack) {
    return new MattockToolNBT(TagUtil.getToolTag(stack)).shovelLevel;
  }

  public static class MattockToolNBT extends ToolNBT {

    private static final String TAG_AxeLevel = Tags.HARVESTLEVEL + "Axe";
    private static final String TAG_ShovelLevel = Tags.HARVESTLEVEL + "Shovel";

    public int axeLevel;
    public int shovelLevel;

    public MattockToolNBT() {
    }

    public MattockToolNBT(NBTTagCompound tag) {
      super(tag);
    }

    @Override
    public void read(NBTTagCompound tag) {
      super.read(tag);
      axeLevel = tag.getInteger(TAG_AxeLevel);
      shovelLevel = tag.getInteger(TAG_ShovelLevel);
    }

    @Override
    public void write(NBTTagCompound tag) {
      super.write(tag);
      tag.setInteger(TAG_AxeLevel, axeLevel);
      tag.setInteger(TAG_ShovelLevel, shovelLevel);
    }
  }
}
