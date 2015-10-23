package slimeknights.tconstruct.tools.item;

import com.google.common.collect.ImmutableSet;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import java.util.List;

import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.ToolMaterialStats;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.AoeToolCore;
import slimeknights.tconstruct.library.tools.ToolNBT;
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
    super(new PartMaterialType.ToolPartType(TinkerTools.toolRod),
          new PartMaterialType.ToolPartType(TinkerTools.axeHead),
          new PartMaterialType.ToolPartType(TinkerTools.shovelHead));

    addCategory(Category.HARVEST);

    // unused, but we give mattock its own tool class
    this.setHarvestLevel("mattock", 0);
  }

  // custom dig speed calculation!
  @Override
  public float getDigSpeed(ItemStack itemstack, IBlockState state) {
    Block block = state.getBlock();

    if(ToolHelper.canHarvest(itemstack, state)) {
      // axe effective?
      if(effective_materials_axe.contains(block.getMaterial()) || block.isToolEffective("axe", state)) {
        float strength = itemstack.getItem().getStrVsBlock(itemstack, state.getBlock());
        float speed = TagUtil.getToolTag(itemstack).getFloat(MattockToolNBT.TAG_Axe);

        return strength * speed;
      }
      // shovel effective?
      else if(effective_materials_shovel.contains(block.getMaterial()) || block.isToolEffective("shovel", state)) {
        float strength = itemstack.getItem().getStrVsBlock(itemstack, state.getBlock());
        float speed = TagUtil.getToolTag(itemstack).getFloat(MattockToolNBT.TAG_Shovel);

        return strength * speed;
      }
    }

    // durp
    return super.getDigSpeed(itemstack, state);
  }

  @Override
  public int getHarvestLevel(ItemStack stack, String toolClass) {
    if(toolClass == null) {
      return -1;
    }

    // axe harvestlevel
    if(toolClass.equals("axe")) {
      return TagUtil.getToolTag(stack).getInteger(MattockToolNBT.TAG_AxeLevel);
    }
    // shovel harvestlevel
    if(toolClass.equals("shovel")) {
      return TagUtil.getToolTag(stack).getInteger(MattockToolNBT.TAG_ShovelLevel);
    }

    // none of them
    return super.getHarvestLevel(stack, toolClass);
  }

  @Override
  public boolean isEffective(Block block) {
    return effective_materials_axe.contains(block.getMaterial()) || effective_materials_shovel.contains(block.getMaterial());
  }

  @Override
  public float damagePotential() {
    return 0.66f;
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

    boolean ret = Items.diamond_hoe.onItemUse(stack, playerIn, worldIn, pos, side, hitX, hitY, hitZ);;
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
  protected int calculateRepair(ItemStack tool, int materialValue, int index) {
    List<Material> materials = TinkerUtil.getMaterialsFromTagList(TagUtil.getBaseMaterialsTagList(tool));

    ToolMaterialStats stats[] = new ToolMaterialStats[2];
    stats[0] = materials.get(1).getStats(ToolMaterialStats.TYPE);
    stats[1] = materials.get(2).getStats(ToolMaterialStats.TYPE);
    int total = stats[0].durability + stats[1].durability;

    float coeff = stats[index-1].durability / (float)total;

    return (int)Math.max(1f,super.calculateRepair(tool, materialValue, index)*coeff);
  }

  @Override
  public String[] getInformation(ItemStack stack) {
    TooltipBuilder info = new TooltipBuilder(stack);

    MattockToolNBT data = new MattockToolNBT();
    data.read(TagUtil.getToolTag(stack));

    info.addDurability();
    // todo: make this proper
    info.addCustom("Axe:");
    info.addCustom(ToolMaterialStats.formatMiningSpeed(data.axeSpeed));
    info.addCustom(ToolMaterialStats.formatHarvestLevel(data.axeLevel));

    info.addCustom("Shovel:");
    info.addCustom(ToolMaterialStats.formatMiningSpeed(data.shovelSpeed));
    info.addCustom(ToolMaterialStats.formatHarvestLevel(data.shovelLevel));

    if(ToolHelper.getFreeModifiers(stack) > 0) {
      info.addFreeModifiers();
    }

    return info.getTooltip();
  }

  @Override
  public NBTTagCompound buildTag(List<Material> materials) {
    ToolMaterialStats handle = materials.get(0).getStats(ToolMaterialStats.TYPE);
    ToolMaterialStats axe = materials.get(1).getStats(ToolMaterialStats.TYPE);
    ToolMaterialStats shovel = materials.get(2).getStats(ToolMaterialStats.TYPE);
    //ToolMaterialStats binding = materials.get(3).getStats(ToolMaterialStats.TYPE);

    MattockToolNBT data = new MattockToolNBT();

    // durability
    data.durability = (axe.durability + shovel.durability)/2;
    data.durability *= 1f + 0.05f * (axe.extraQuality*shovel.extraQuality);

    // backup speed
    data.speed = (axe.miningspeed + shovel.miningspeed)/2f;
    data.speed *= 1f + 0.3f * (handle.handleQuality * handle.miningspeed);
    // real speed
    data.axeSpeed = axe.miningspeed * 0.8f + 0.2f * handle.handleQuality;
    data.shovelSpeed = shovel.miningspeed * 0.8f + 0.2f * handle.handleQuality;

    // a bit slower because.. it's a mattock
    data.axeSpeed *= 0.7f;
    data.shovelSpeed *= 0.7f;

    // harvest level
    data.axeLevel = axe.harvestLevel;
    data.shovelLevel = shovel.harvestLevel;
    data.harvestLevel = Math.round((axe.harvestLevel + shovel.harvestLevel) / 2f); // backup

    // damage
    data.attack = 4f;
    data.attack += (axe.attack * 0.7f + shovel.attack * 0.3f)/2f;

    // 3 free modifiers
    data.modifiers = DEFAULT_MODIFIERS;

    return data.get();
  }

  public static class MattockToolNBT extends ToolNBT {

    private static final String TAG_Axe = Tags.MININGSPEED + "Axe";
    private static final String TAG_Shovel = Tags.MININGSPEED + "Shovel";
    private static final String TAG_AxeLevel = Tags.HARVESTLEVEL + "Axe";
    private static final String TAG_ShovelLevel = Tags.HARVESTLEVEL + "Shovel";

    public float axeSpeed;
    public float shovelSpeed;
    public int axeLevel;
    public int shovelLevel;

    @Override
    public void read(NBTTagCompound tag) {
      super.read(tag);
      axeSpeed = tag.getFloat(TAG_Axe);
      shovelSpeed = tag.getFloat(TAG_Shovel);
      axeLevel = tag.getInteger(TAG_AxeLevel);
      shovelLevel = tag.getInteger(TAG_ShovelLevel);
    }

    @Override
    public void write(NBTTagCompound tag) {
      super.write(tag);
      tag.setFloat(TAG_Axe, axeSpeed);
      tag.setFloat(TAG_Shovel, shovelSpeed);
      tag.setInteger(TAG_AxeLevel, axeLevel);
      tag.setInteger(TAG_ShovelLevel, shovelLevel);
    }
  }
}
