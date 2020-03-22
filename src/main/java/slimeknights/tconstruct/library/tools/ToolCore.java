package slimeknights.tconstruct.library.tools;

import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.tinkering.IModifiable;
import slimeknights.tconstruct.library.tinkering.ITinkerable;
import slimeknights.tconstruct.library.tools.helper.ToolInteractionUtil;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolData;
import slimeknights.tconstruct.tools.ToolStatsBuilder;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * An indestructible item constructed from different parts.
 * This class handles how all the data for items made out of different
 * The NBT representation of tool stats, what the tool is made of, which modifier have been applied, etc.
 */
public abstract class ToolCore extends Item implements ITinkerable, IModifiable {

  private final ToolDefinition toolDefinition;

  public ToolCore(Properties properties, ToolDefinition toolDefinition) {
    super(properties.maxStackSize(1).setNoRepair());
    this.toolDefinition = toolDefinition;
  }

  public ToolDefinition getToolDefinition() {
    return toolDefinition;
  }

  public StatsNBT buildToolStats(List<IMaterial> materials) {
    return ToolStatsBuilder.from(materials, toolDefinition).buildDefaultStats();
  }

  @Override
  public int getMaxDamage(ItemStack stack) {
    StatsNBT stats = ToolData.from(stack).getStats();
    // the tool can only have damage when it's not broken, to prevent vanilla from deleting the itemstack
    return stats.broken ? 0 : stats.durability;
  }

  @Override
  public void setDamage(ItemStack stack, int damage) {
    int max = getMaxDamage(stack);
    super.setDamage(stack, Math.min(max, damage));

    if (getDamage(stack) >= max) {
      ToolData toolData = ToolData.from(stack);
      if (!toolData.getStats().broken) {
        ToolData newData = toolData.createNewDataWithBroken(true);
        newData.updateStack(stack);
      }
    }
  }

  @Override
  public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T damager, Consumer<T> onBroken) {
    ToolInteractionUtil.damageTool(stack, amount, damager);

    return 0;
  }

  @Override
  public boolean isDamageable() {
    return true;
  }

  @Override
  public boolean showDurabilityBar(ItemStack stack) {
    return super.showDurabilityBar(stack) && !ToolData.from(stack).getStats().broken;
  }

  public static int getCurrentDurability(ItemStack stack) {
    return stack.getMaxDamage() - stack.getDamage();
  }

  /* Mining */


  @Override
  public Set<ToolType> getToolTypes(ItemStack stack) {
    // no classes if broken
    if (ToolData.from(stack).getStats().broken) {
      return Collections.emptySet();
    }
    return super.getToolTypes(stack);
  }

  @Override
  public int getHarvestLevel(ItemStack stack, ToolType toolClass, @Nullable PlayerEntity player, @Nullable BlockState blockState) {
    StatsNBT stats = ToolData.from(stack).getStats();

    // brokenness is calculated in by the toolTypes check
    if (getToolTypes(stack).contains(toolClass)) {
      return stats.harvestLevel;
    }

    return -1;
  }

  public void afterBlockBreak(ItemStack stack, World world, BlockState state, BlockPos pos, PlayerEntity player, int damage, boolean wasEffective) {
    //TinkerUtil.getTraitsOrdered(stack).forEach(trait -> trait.afterBlockBreak(stack, world, state, pos, player, wasEffective));
    //stack.damageItem(damage, player, );

  }
  /* World interaction */
//
//
//  @Override
//  public float getDestroySpeed(ItemStack stack, BlockState state) {
//    if(isEffective(state) || ToolHelper.isToolEffective(stack, state)) {
//      return ToolHelper.calcDigSpeed(stack, state);
//    }
//    return super.getDestroySpeed(stack, state);
//  }
//
//  public boolean isEffective(BlockState state) {
//    return false;
//  }
//
//  @Override
//  public boolean canHarvestBlock(ItemStack stack, BlockState state) {
//    return isEffective(state) && !ToolHelper.isBroken(stack);
//  }
//
//  @Override
//  public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, PlayerEntity player) {
//    if(!ToolHelper.isBroken(itemstack) && this instanceof IAoeTool && ((IAoeTool) this).isAoeHarvestTool()) {
//      for(BlockPos extraPos : ((IAoeTool) this).getAOEBlocks(itemstack, player.getEntityWorld(), player, pos)) {
//        breakExtraBlock(itemstack, player.getEntityWorld(), player, extraPos, pos);
//      }
//    }
//
//    // this is a really dumb hack.
//    // Basically when something with silktouch harvests a block from the offhand
//    // the game can't detect that. so we have to switch around the items in the hands for the break call
//    // it's switched back in onBlockDestroyed
//    if(DualToolHarvestUtils.shouldUseOffhand(player, pos, player.getHeldItemMainhand())) {
//      ItemStack off = player.getHeldItemOffhand();
//      switchItemsInHands(player);
//      // remember, off is in the mainhand now
//      NBTTagCompound tag = TagUtil.getTagSafe(off);
//      tag.setLong(TAG_SWITCHED_HAND_HAX, player.getEntityWorld().getTotalWorldTime());
//      off.setTagCompound(tag);
//    }
//
//    return breakBlock(itemstack, pos, player);
//  }
//
//  /**
//   * Called to break the base block, return false to perform no breaking
//   * @param itemstack Tool ItemStack
//   * @param pos       Current position
//   * @param player    Player instance
//   * @return true if the normal block break code should be skipped
//   */
//  // todo: find a better way to solve this and breakExtraBlock?
//  protected boolean breakBlock(ItemStack itemstack, BlockPos pos, PlayerEntity player) {
//    return super.onBlockStartBreak(itemstack, pos, player);
//  }
//
//  /**
//   * Called when an AOE block is broken by the tool. Use to oveerride the block breaking logic
//   * @param tool      Tool ItemStack
//   * @param world     World instance
//   * @param player    Player instance
//   * @param pos       Current position
//   * @param refPos    Base position
//   */
//  protected void breakExtraBlock(ItemStack tool, World world, PlayerEntity player, BlockPos pos, BlockPos refPos) {
//    ToolHelper.breakExtraBlock(tool, world, player, pos, refPos);
//  }
//
//  @Override
//  public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity entity) {
//    return ToolHelper.attackEntity(stack, this, player, entity);
//  }
//
//  @Override
//  public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
//    // todo: got disabled in 02062bf45652c5aaed9756871806cc542a3a96ea when stuff got moved to attribute maps.. I suppose it's handled by vanilla now?
//    /*if(attackSpeed() > 0) {
//      int speed = Math.min(5, attackSpeed());
//      ToolHelper.swingItem(speed, entityLiving);
//      return true;
//    }*/
//    return super.onEntitySwing(stack, entity);
//  }
//
//  @Override
//  public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
//    float speed = ToolHelper.getActualAttackSpeed(stack);
//    int time = Math.round(20f / speed);
//    if(time < target.hurtResistantTime / 2) {
//      target.hurtResistantTime = (target.hurtResistantTime + time) / 2;
//      target.hurtTime = (target.hurtTime + time) / 2;
//    }
//    return super.hitEntity(stack, target, attacker);
//  }
//
//  @Override
//  public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
//    Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);
//
//    if(slot == EntityEquipmentSlot.MAINHAND && !ToolHelper.isBroken(stack)) {
//      multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", ToolHelper.getActualAttack(stack), 0));
//      multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", ToolHelper.getActualAttackSpeed(stack) - 4d, 0));
//    }
//
//    TinkerUtil.getTraitsOrdered(stack).forEach(trait -> trait.getAttributeModifiers(slot, stack, multimap));
//
//    return multimap;
//  }
//
//  /* Trait interactions */
//
//  @Override
//  public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
//    super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
//
//    onUpdateTraits(stack, worldIn, entityIn, itemSlot, isSelected);
//  }
//
//  protected void onUpdateTraits(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
//    final boolean isSelectedOrOffhand = isSelected ||
//      (entityIn instanceof EntityPlayer && ((EntityPlayer) entityIn).getHeldItemOffhand() == stack);
//
//    TinkerUtil.getTraitsOrdered(stack).forEach(trait -> trait.onUpdate(stack, worldIn, entityIn, itemSlot, isSelectedOrOffhand));
//  }
//
//  /* INDESTRUCTIBLE */
//
//  @Override
//  public boolean hasCustomEntity(ItemStack stack) {
//    return true;
//  }
//
//  @Override
//  public Entity createEntity(World world, Entity original, ItemStack itemstack) {
//    ItemEntity entity = new IndestructibleEntityItem(world, original.posX, original.posY, original.posZ, itemstack);
//    // workaround for private access on pickup delay. We simply read it from the items NBT representation ;)
//    if(original instanceof ItemEntity) {
//      CompoundNBT tag = new CompoundNBT();
//      ((ItemEntity) original).writeAdditional(tag);
//      entity.setPickupDelay(tag.getShort("PickupDelay"));
//    }
//    entity.setMotion(original.getMotion());
//    return entity;
//  }
//
//  /* Information */
//

  @Override
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
    // todo: evaluate if we want to keep the material info
    CompoundNBT tag = stack.getTag();
    if (tag == null) {
      tooltip.add(new StringTextComponent("No tool data. NBT missing."));
      return;
    }
    ToolData toolData = ToolData.readFromNBT(tag);

    toolData.getMaterials().stream()
      .map(material -> new StringTextComponent(material.getIdentifier().toString()))
      .forEach(tooltip::add);

    tooltip.add(new StringTextComponent(toolData.getStats().toString()));
  }

//
//  @Override
//  public Rarity getRarity(ItemStack stack) {
//    // prevents enchanted items to have a different name color
//    return Rarity.COMMON;
//  }
//
//  @Override
//  public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
//    return false;
//  }
//
//  @Nonnull
//  @OnlyIn(Dist.CLIENT)
//  @Override
//  public FontRenderer getFontRenderer(ItemStack stack) {
//    return ClientProxy.fontRenderer;
//  }
//
//  @OnlyIn(Dist.CLIENT)
//  @Override
//  public boolean hasEffect(ItemStack stack) {
//    return TagUtil.hasEnchantEffect(stack);
//  }
//
//  @Override
//  public ITextComponent getDisplayName(ItemStack stack) {
//    // if the tool is not named we use the repair tools for a prefix like thing
//    List<Material> materials = TinkerUtil.getMaterialsFromTagList(TagUtil.getBaseMaterialsTagList(stack));
//    // we save all the ones for the name in a set so we don't have the same material in it twice
//    Set<Material> nameMaterials = Sets.newLinkedHashSet();
//
//    for(int index : getRepairParts()) {
//      if(index < materials.size()) {
//        nameMaterials.add(materials.get(index));
//      }
//    }
//
//    return Material.getCombinedItemName(super.getItemStackDisplayName(stack), nameMaterials);
//  }
//
//  /* NBT loading */
//
//  @Override
//  public boolean updateItemStackNBT(CompoundNBT nbt) {
//    // when the itemstack is loaded from NBT we recalculate all the data
//    if(nbt.contains(Tags.BASE)) {
//      try {
//        // todo ToolBuilder.rebuildTool(nbt, this);
//        throw new TinkerGuiException();
//      }
//      catch(TinkerGuiException e) {
//        // nothing to do
//      }
//    }
//
//    // return value shouldn't matter since it's never checked
//    return true;
//  }
//
//  /* Misc */
//
//  // elevate to public, used in AOE preview
//  public RayTraceResult rayTraceExposed(World worldIn, PlayerEntity player, RayTraceContext.FluidMode fluidMode) {
//    return Item.rayTrace(worldIn, player, fluidMode);
//  }
//
//
//  @Override
//  public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
//    return shouldCauseReequipAnimation(oldStack, newStack, false);
//  }
//
//  @Override
//  public boolean shouldCauseReequipAnimation(ItemStack oldStack, @Nonnull ItemStack newStack, boolean slotChanged) {
//    if(TagUtil.getResetFlag(newStack)) {
//      TagUtil.setResetFlag(newStack, false);
//      return true;
//    }
//    if(oldStack == newStack) {
//      return false;
//    }
//    if(slotChanged) {
//      return true;
//    }
//
//    if(oldStack.hasEffect() != newStack.hasEffect()) {
//      return true;
//    }
//
//    Multimap<String, AttributeModifier> attributesNew = newStack.getAttributeModifiers(EntityEquipmentSlot.MAINHAND);
//    Multimap<String, AttributeModifier> attributesOld = oldStack.getAttributeModifiers(EntityEquipmentSlot.MAINHAND);
//
//    if(attributesNew.size() != attributesOld.size()) {
//      return true;
//    }
//    for(String key : attributesOld.keySet()) {
//      if(!attributesNew.containsKey(key)) {
//        return true;
//      }
//      Iterator<AttributeModifier> iter1 = attributesNew.get(key).iterator();
//      Iterator<AttributeModifier> iter2 = attributesOld.get(key).iterator();
//      while(iter1.hasNext() && iter2.hasNext()) {
//        if(!iter1.next().equals(iter2.next())) {
//          return true;
//        }
//      }
//    }
//
//    if(oldStack.getItem() == newStack.getItem() && newStack.getItem() instanceof ToolCore) {
//      return !isEqualTinkersItem(oldStack, newStack);
//    }
//    return !ItemStack.areItemStacksEqual(oldStack, newStack);
//  }
}
