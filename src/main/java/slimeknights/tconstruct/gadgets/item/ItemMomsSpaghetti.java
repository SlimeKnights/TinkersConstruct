package slimeknights.tconstruct.gadgets.item;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import slimeknights.mantle.util.LocUtils;
import slimeknights.tconstruct.common.ClientProxy;
import slimeknights.tconstruct.gadgets.TinkerGadgets;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.CustomFontColor;
import slimeknights.tconstruct.library.tinkering.IModifyable;
import slimeknights.tconstruct.library.tinkering.IRepairable;
import slimeknights.tconstruct.library.tinkering.IToolStationDisplay;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.TinkerUtil;
import slimeknights.tconstruct.library.utils.ToolHelper;
import slimeknights.tconstruct.library.utils.TooltipBuilder;

public class ItemMomsSpaghetti extends ItemFood implements IRepairable, IModifyable, IToolStationDisplay {

  public static final String LOC_NAME = "item.tconstruct.moms_spaghetti.name";
  public static final String LOC_DESC = "item.tconstruct.moms_spaghetti.desc";
  public static final String LOC_USES = "stat.spaghetti.uses.name";
  public static final String LOC_NOURISHMENT = "stat.spaghetti.nourishment.name";
  public static final String LOC_SATURATION = "stat.spaghetti.saturation.name";
  public static final String LOC_TOOLTIP = "item.tconstruct.moms_spaghetti.tooltip";

  public static final int MAX_USES = 100;
  public static final int USES_PER_WHEAT = 1;

  public ItemMomsSpaghetti() {
    super(2, 0.2f, false);
    this.setMaxDamage(MAX_USES);
    this.setMaxStackSize(1);
    this.setNoRepair();

    this.setCreativeTab(null);
  }

  @Override
  public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
    // no creative items, nono
  }

  @Override
  public float getSaturationModifier(ItemStack stack) {
    float saturation = super.getSaturationModifier(stack);
    if(hasSauce(stack)) {
      saturation += 0.2f;
    }
    return saturation;
  }

  @Override
  public int getHealAmount(ItemStack stack) {
    int heal = super.getHealAmount(stack);
    if(hasMeat(stack)) {
      heal += 1;
    }
    return heal;
  }

  protected static boolean hasModifier(ItemStack stack, String identifier) {
    return TinkerUtil.hasModifier(TagUtil.getTagSafe(stack), identifier);
  }

  public static boolean hasSauce(ItemStack stack) {
    return hasModifier(stack, TinkerGadgets.modSpaghettiSauce.getIdentifier());
  }

  public static boolean hasMeat(ItemStack stack) {
    return hasModifier(stack, TinkerGadgets.modSpaghettiMeat.getIdentifier());
  }

  @Override
  @Nonnull
  public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
    stack.setItemDamage(stack.getItemDamage() + 1);

    if(entityLiving instanceof EntityPlayer) {
      EntityPlayer entityplayer = (EntityPlayer) entityLiving;
      entityplayer.getFoodStats().addStats(this, stack);
      worldIn.playSound(null, entityplayer.posX, entityplayer.posY, entityplayer.posZ, SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, worldIn.rand.nextFloat() * 0.1F + 0.9F);
      StatBase statBase = StatList.getObjectUseStats(this);
      assert statBase != null;
      entityplayer.addStat(statBase);
    }

    return stack;
  }

  /**
   * How long it takes to use or consume an item
   */
  @Override
  public int getMaxItemUseDuration(ItemStack stack) {
    return 10;
  }

  /**
   * returns the action that specifies what animation to play when the items is being used
   */
  @Nonnull
  @Override
  public EnumAction getItemUseAction(ItemStack stack) {
    return EnumAction.EAT;
  }

  @Nonnull
  @Override
  public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
    ItemStack itemStackIn = playerIn.getHeldItem(hand);
    if(playerIn.canEat(false) && getUses(itemStackIn) > 0) {
      playerIn.setActiveHand(hand);
      return new ActionResult<>(EnumActionResult.SUCCESS, itemStackIn);
    }
    else {
      return new ActionResult<>(EnumActionResult.FAIL, itemStackIn);
    }
  }

  public int getUses(ItemStack stack) {
    return stack.getMaxDamage() - stack.getItemDamage();
  }

  @Override
  public ItemStack repair(ItemStack repairable, NonNullList<ItemStack> repairItems) {
    if(repairable.getItemDamage() == 0) {
      // nothing to repair, full durability
      return ItemStack.EMPTY;
    }

    // don't accept anything that's not wheat and don't accept all empty
    boolean allEmpty = true;
    for(ItemStack repairItem : repairItems) {
      if(!repairItem.isEmpty()) {
        allEmpty = false;
        if(repairItem.getItem() != Items.WHEAT) {
          return ItemStack.EMPTY;
        }
      }
    }
    if (allEmpty) {
      return ItemStack.EMPTY;
    }

    ItemStack stack = repairable.copy();
    int index = 0;
    while(stack.getItemDamage() > 0 && index < repairItems.size()) {
      ItemStack repairItem = repairItems.get(index);
      if(!repairItem.isEmpty() && repairItem.getCount() > 0) {
        repairItem.shrink(1);

        //change = Math.min(change, stack.getMaxDamage() - stack.getItemDamage());
        ToolHelper.healTool(stack, USES_PER_WHEAT, null);
      }
      else {
        index++;
      }
    }

    return stack;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
    tooltip.add(String.format("%s: %s", Util.translate(LOC_USES),
                              CustomFontColor.formatPartialAmount(getUses(stack), getMaxDamage(stack))));
    TooltipBuilder.addModifierTooltips(stack, tooltip);

    tooltip.add("");
    int i = 1;
    if(hasMeat(stack)) {
      i = 3;
    }
    else if(hasSauce(stack)) {
      i = 2;
    }
    tooltip.addAll(LocUtils.getTooltips(Util.translate(LOC_TOOLTIP + i)));
  }

  @Nonnull
  @SideOnly(Side.CLIENT)
  @Override
  public FontRenderer getFontRenderer(ItemStack stack) {
    return ClientProxy.fontRenderer;
  }

  @Override
  public String getLocalizedToolName() {
    return Util.translate(LOC_NAME);
  }

  @Override
  public List<String> getInformation(ItemStack stack) {
    int nourishment = getHealAmount(stack);
    float saturation = getSaturationModifier(stack);

    return ImmutableList.of(
        Util.translate(LOC_DESC),
        String.format("%s: %s", Util.translate(LOC_USES), getUses(stack)) + TextFormatting.RESET,
        String.format("%s: %s", Util.translate(LOC_NOURISHMENT), nourishment) + TextFormatting.RESET,
        String.format("%s: %s", Util.translate(LOC_SATURATION), Util.dfPercent.format(saturation)) + TextFormatting.RESET
    );
  }
}
