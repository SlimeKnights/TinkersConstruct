package slimeknights.tconstruct.tools.ranged.item;

import com.google.common.collect.ImmutableList;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.library.client.BooleanItemPropertyGetter;
import slimeknights.tconstruct.library.client.crosshair.Crosshairs;
import slimeknights.tconstruct.library.client.crosshair.ICrosshair;
import slimeknights.tconstruct.library.client.crosshair.ICustomCrosshairUser;
import slimeknights.tconstruct.library.materials.BowMaterialStats;
import slimeknights.tconstruct.library.materials.BowStringMaterialStats;
import slimeknights.tconstruct.library.materials.ExtraMaterialStats;
import slimeknights.tconstruct.library.materials.HandleMaterialStats;
import slimeknights.tconstruct.library.materials.HeadMaterialStats;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.MaterialTypes;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.ProjectileLauncherNBT;
import slimeknights.tconstruct.library.tools.ranged.BowCore;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.ToolHelper;
import slimeknights.tconstruct.tools.TinkerMaterials;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.ranged.TinkerRangedWeapons;

public class CrossBow extends BowCore implements ICustomCrosshairUser {

  private static final String TAG_Loaded = "Loaded";

  protected static final ResourceLocation PROPERTY_IS_LOADED = new ResourceLocation("loaded");

  public CrossBow() {
    super(PartMaterialType.crossbow(TinkerTools.toughToolRod),
          PartMaterialType.bow(TinkerTools.bowLimb),
          PartMaterialType.extra(TinkerTools.toughBinding),
          PartMaterialType.bowstring(TinkerTools.bowString));

    this.addPropertyOverride(PROPERTY_PULL_PROGRESS, pullProgressPropertyGetter);
    this.addPropertyOverride(PROPERTY_IS_PULLING, isPullingPropertyGetter);
    this.addPropertyOverride(PROPERTY_IS_LOADED, new BooleanItemPropertyGetter() {
      @Override
      public boolean applyIf(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
        return entityIn != null && isLoaded(stack);
      }
    });
  }

  @Override
  public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
    if(this.isInCreativeTab(tab)) {
      addDefaultSubItems(subItems, null, null, null, TinkerMaterials.string);
    }
  }

  @Override
  public float damagePotential() {
    return 0.8f;
  }

  @Override
  public double attackSpeed() {
    return 2;
  }

  @Override
  public float baseProjectileDamage() {
    return 3f;
  }

  @Override
  protected float baseProjectileSpeed() {
    return 7f;
  }

  @Override
  public float projectileDamageModifier() {
    return 1.3f;
  }

  @Override
  public int getDrawTime() {
    return 45;
  }

  public boolean isLoaded(ItemStack stack) {
    return TagUtil.getTagSafe(stack).getBoolean(TAG_Loaded);
  }

  public void setLoaded(ItemStack stack, boolean isLoaded) {
    NBTTagCompound tag = TagUtil.getTagSafe(stack);
    tag.setBoolean(TAG_Loaded, isLoaded);
    stack.setTagCompound(tag);
  }

  @Override
  public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
    // has to be done in onUpdate because onTickUsing is too early and gets overwritten. bleh.
    preventSlowDown(entityIn, 0.195f);

    super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
  }

  @Nonnull
  @Override
  public EnumAction getItemUseAction(ItemStack stack) {
    return EnumAction.NONE;
  }

  @Nonnull
  @Override
  public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
    ItemStack itemStackIn = playerIn.getHeldItem(hand);
    if(isLoaded(itemStackIn) && !ToolHelper.isBroken(itemStackIn)) {
      super.onPlayerStoppedUsing(itemStackIn, worldIn, playerIn, 0);
      setLoaded(itemStackIn, false);
    }
    else {
      return super.onItemRightClick(worldIn, playerIn, hand);
    }
    return ActionResult.newResult(EnumActionResult.SUCCESS, itemStackIn);
  }

  @Override
  public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
    if(!ToolHelper.isBroken(stack) && (entityLiving instanceof EntityPlayer)) {
      int useTime = this.getMaxItemUseDuration(stack) - timeLeft;
      if(getDrawbackProgress(stack, useTime) >= 1f) {
        Sounds.PlaySoundForPlayer(entityLiving, Sounds.crossbow_reload, 1.5f, 0.9f + itemRand.nextFloat() * 0.1f);
        setLoaded(stack, true);
      }
    }
  }

  @Override
  public void playShootSound(float power, World world, EntityPlayer entityPlayer) {
    world.playSound(null, entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 1.0F, 0.5f + itemRand.nextFloat() * 0.1f);
  }

  @Override
  public ItemStack getAmmoToRender(ItemStack weapon, EntityLivingBase player) {
    if(!isLoaded(weapon)) {
      return ItemStack.EMPTY;
    }
    return super.getAmmoToRender(weapon, player);
  }

  private ImmutableList<Item> boltMatches = null;

  @Override
  protected List<Item> getAmmoItems() {
    if(boltMatches == null) {
      ImmutableList.Builder<Item> builder = ImmutableList.builder();
      if(TinkerRangedWeapons.bolt != null) {
        builder.add(TinkerRangedWeapons.bolt);
      }
      boltMatches = builder.build();
    }
    return boltMatches;
  }

  @Override
  public ProjectileLauncherNBT buildTagData(List<Material> materials) {
    ProjectileLauncherNBT data = new ProjectileLauncherNBT();
    HandleMaterialStats body = materials.get(0).getStatsOrUnknown(MaterialTypes.HANDLE);
    ExtraMaterialStats bodyExtra = materials.get(0).getStatsOrUnknown(MaterialTypes.EXTRA);
    HeadMaterialStats head = materials.get(1).getStatsOrUnknown(MaterialTypes.HEAD);
    BowMaterialStats limb = materials.get(1).getStatsOrUnknown(MaterialTypes.BOW);
    ExtraMaterialStats binding = materials.get(2).getStatsOrUnknown(MaterialTypes.EXTRA);
    BowStringMaterialStats bowstring = materials.get(3).getStatsOrUnknown(MaterialTypes.BOWSTRING);

    data.head(head);
    data.limb(limb);
    data.extra(binding, bodyExtra);
    data.handle(body);
    data.bowstring(bowstring);

    data.bonusDamage *= 1.5f;

    return data;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public ICrosshair getCrosshair(ItemStack itemStack, EntityPlayer player) {
    return Crosshairs.T;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public float getCrosshairState(ItemStack itemStack, EntityPlayer player) {
    if(isLoaded(itemStack)) {
      return 1f;
    }
    else if(player.getActiveItemStack() != itemStack) {
      return 0f;
    }
    return getDrawbackProgress(itemStack, player);
  }
}
