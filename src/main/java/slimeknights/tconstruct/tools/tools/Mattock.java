package slimeknights.tconstruct.tools.tools;

import com.google.common.collect.ImmutableSet;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.events.TinkerToolEvent;
import slimeknights.tconstruct.library.materials.HandleMaterialStats;
import slimeknights.tconstruct.library.materials.HeadMaterialStats;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.MaterialTypes;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.AoeToolCore;
import slimeknights.tconstruct.library.tools.ToolNBT;
import slimeknights.tconstruct.library.utils.HarvestLevels;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.Tags;
import slimeknights.tconstruct.library.utils.ToolHelper;
import slimeknights.tconstruct.library.utils.TooltipBuilder;
import slimeknights.tconstruct.tools.TinkerTools;

public class Mattock extends AoeToolCore {

  public static final ImmutableSet<net.minecraft.block.material.Material> effective_materials_axe =
      ImmutableSet.of(net.minecraft.block.material.Material.WOOD,
                      net.minecraft.block.material.Material.CACTUS,
                      net.minecraft.block.material.Material.PLANTS,
                      net.minecraft.block.material.Material.VINE,
                      net.minecraft.block.material.Material.GOURD);

  public static final ImmutableSet<net.minecraft.block.material.Material> effective_materials_shovel =
      ImmutableSet.of(net.minecraft.block.material.Material.GRASS,
                      net.minecraft.block.material.Material.GROUND,
                      net.minecraft.block.material.Material.CLAY);

  public Mattock() {
    super(PartMaterialType.handle(TinkerTools.toolRod),
          PartMaterialType.head(TinkerTools.axeHead),
          PartMaterialType.head(TinkerTools.shovelHead));

    addCategory(Category.HARVEST);

    // unused, but we give mattock its own tool class
    this.setHarvestLevel("mattock", 0);
  }

  @Override
  public int getHarvestLevel(ItemStack stack, String toolClass, @Nullable EntityPlayer player, @Nullable IBlockState blockState) {
    if(StringUtils.isNullOrEmpty(toolClass)) {
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
    return super.getHarvestLevel(stack, toolClass, player, blockState);
  }

  @Override
  public boolean isEffective(IBlockState state) {
    return effective_materials_axe.contains(state.getMaterial()) || effective_materials_shovel.contains(state.getMaterial());
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
  public double attackSpeed() {
    return 0.9f;
  }

  @Override
  public int[] getRepairParts() {
    return new int[]{1, 2};
  }

  @Nonnull
  @Override
  public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
    ItemStack stack = player.getHeldItem(hand);
    if(ToolHelper.isBroken(stack)) {
      return EnumActionResult.FAIL;
    }

    EnumActionResult ret = useHoe(stack, player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
    for(BlockPos blockPos : getAOEBlocks(stack, worldIn, player, pos)) {
      if(ToolHelper.isBroken(stack)) {
        break;
      }

      EnumActionResult ret2 = useHoe(stack, player, worldIn, blockPos, hand, facing, hitX, hitY, hitZ);
      if(ret != EnumActionResult.SUCCESS) {
        ret = ret2;
      }
    }

    if(ret == EnumActionResult.SUCCESS) {
      TinkerToolEvent.OnMattockHoe.fireEvent(stack, player, worldIn, pos);
    }

    return ret;
  }

  private EnumActionResult useHoe(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos blockPos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
    // make sure no damage is taken
    int damage = stack.getItemDamage();
    EnumActionResult ret = Items.DIAMOND_HOE.onItemUse(playerIn, worldIn, blockPos, hand, facing, hitX, hitY, hitZ);
    stack.setItemDamage(damage);

    // do tinkers damaging
    if(!worldIn.isRemote && ret == EnumActionResult.SUCCESS) {
      ToolHelper.damageTool(stack, 1, playerIn);
    }
    return ret;
  }


  @Override
  public boolean isAoeHarvestTool() {
    return false;
  }

  @Override
  public List<String> getInformation(ItemStack stack, boolean detailed) {
    TooltipBuilder info = new TooltipBuilder(stack);

    info.addDurability(!detailed);

    // special axe harvest level
    String text = Util.translate("stat.mattock.axelevel.name");
    info.add(String.format("%s: %s", text, HarvestLevels.getHarvestLevelName(getAxeLevel(stack))) + TextFormatting.RESET);

    // special shovel harvest level
    text = Util.translate("stat.mattock.shovellevel.name");
    info.add(String.format("%s: %s", text, HarvestLevels.getHarvestLevelName(getShovelLevel(stack))) + TextFormatting.RESET);

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
  public ToolNBT buildTagData(List<Material> materials) {
    HandleMaterialStats handle = materials.get(0).getStatsOrUnknown(MaterialTypes.HANDLE);
    HeadMaterialStats axe = materials.get(1).getStatsOrUnknown(MaterialTypes.HEAD);
    HeadMaterialStats shovel = materials.get(2).getStatsOrUnknown(MaterialTypes.HEAD);

    MattockToolNBT data = new MattockToolNBT();
    data.head(axe, shovel);
    data.handle(handle);

    // special harvest levels
    data.axeLevel = axe.harvestLevel;
    data.shovelLevel = shovel.harvestLevel;

    // base damage!
    data.attack += 3;

    return data;
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
