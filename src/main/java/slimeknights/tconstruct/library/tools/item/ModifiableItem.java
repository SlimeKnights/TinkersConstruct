package slimeknights.tconstruct.library.tools.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlot.Type;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import slimeknights.mantle.client.SafeClientAccess;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.tools.IndestructibleItemEntity;
import slimeknights.tconstruct.library.tools.capability.ToolCapabilityProvider;
import slimeknights.tconstruct.library.tools.capability.ToolInventoryCapability;
import slimeknights.tconstruct.library.tools.context.ToolHarvestContext;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.ModifiableItemUtil;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.helper.ToolBuildHandler;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.helper.ToolHarvestLogic;
import slimeknights.tconstruct.library.tools.helper.TooltipUtil;
import slimeknights.tconstruct.library.tools.nbt.IModDataView;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.Util;
import slimeknights.tconstruct.tools.TinkerToolActions;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

/**
 * A standard modifiable item which implements melee hooks
 * This class handles how all the modifier hooks and display data for items made out of different materials
 */
public class ModifiableItem extends Item implements IModifiableDisplay {
  /** Tool definition for the given tool */
  @Getter
  private final ToolDefinition toolDefinition;

  /** Cached tool for rendering on UIs */
  private ItemStack toolForRendering;

  public ModifiableItem(Properties properties, ToolDefinition toolDefinition) {
    super(properties);
    this.toolDefinition = toolDefinition;
  }


  /* Basic properties */

  @Override
  public int getItemStackLimit(ItemStack stack) {
    return 1;
  }

  @Override
  public boolean isEnchantable(ItemStack stack) {
    return false;
  }

  @Override
  public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
    return false;
  }

  @Override
  public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
    return enchantment.isCurse() && super.canApplyAtEnchantingTable(stack, enchantment);
  }


  /* Loading */

  @Nullable
  @Override
  public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
    return new ToolCapabilityProvider(stack);
  }

  @Override
  public void verifyTagAfterLoad(CompoundTag nbt) {
    ToolStack.verifyTag(this, nbt, getToolDefinition());
  }

  @Override
  public void onCraftedBy(ItemStack stack, Level worldIn, Player playerIn) {
    ToolStack.ensureInitialized(stack, getToolDefinition());
  }


  /* Display */

  @Override
  public boolean isFoil(ItemStack stack) {
    // we use enchantments to handle some modifiers, so don't glow from them
    // however, if a modifier wants to glow let them
    return ModifierUtil.checkVolatileFlag(stack, SHINY);
  }

  @Override
  public Rarity getRarity(ItemStack stack) {
    int rarity = ModifierUtil.getVolatileInt(stack, RARITY);
    return Rarity.values()[Mth.clamp(rarity, 0, 3)];
  }


  /* Indestructible items */

  @Override
  public boolean hasCustomEntity(ItemStack stack) {
    return IndestructibleItemEntity.hasCustomEntity(stack);
  }

  @Override
  public Entity createEntity(Level world, Entity original, ItemStack stack) {
    return IndestructibleItemEntity.createFrom(world, original, stack);
  }


  /* Damage/Durability */

  @Override
  public boolean isRepairable(ItemStack stack) {
    // handle in the tinker station
    return false;
  }

  @Override
  public boolean canBeDepleted() {
    return true;
  }

  @Override
  public int getMaxDamage(ItemStack stack) {
    if (!canBeDepleted()) {
      return 0;
    }
    ToolStack tool = ToolStack.from(stack);
    int durability = tool.getStats().getInt(ToolStats.DURABILITY);
    // vanilla deletes tools if max damage == getDamage, so tell vanilla our max is one higher when broken
    return tool.isBroken() ? durability + 1 : durability;
  }

  @Override
  public int getDamage(ItemStack stack) {
    if (!canBeDepleted()) {
      return 0;
    }
    return ToolStack.from(stack).getDamage();
  }

  @Override
  public void setDamage(ItemStack stack, int damage) {
    if (canBeDepleted()) {
      ToolStack.from(stack).setDamage(damage);
    }
  }

  @Override
  public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T damager, Consumer<T> onBroken) {
    // We basically emulate Itemstack.damageItem here. We always return 0 to skip the handling in ItemStack.
    // If we don't tools ignore our damage logic
    if (canBeDepleted() && ToolDamageUtil.damage(ToolStack.from(stack), amount, damager, stack)) {
      onBroken.accept(damager);
    }

    return 0;
  }


  /* Durability display */

  @Override
  public boolean isBarVisible(ItemStack pStack) {
    return ToolDamageUtil.showDurabilityBar(pStack);
  }

  @Override
  public int getBarColor(ItemStack pStack) {
    return ToolDamageUtil.getRGBDurabilityForDisplay(pStack);
  }

  @Override
  public int getBarWidth(ItemStack pStack) {
    return ToolDamageUtil.getDamageForDisplay(pStack);
  }


  /* Attacking */

  @Override
  public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
    return ToolAttackUtil.attackEntity(stack, player, entity);
  }

  @Override
  public Multimap<Attribute,AttributeModifier> getAttributeModifiers(IToolStackView tool, EquipmentSlot slot) {
    return ModifiableItemUtil.getMeleeAttributeModifiers(tool, slot);
  }

  @Override
  public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
    CompoundTag nbt = stack.getTag();
    if (nbt == null || slot.getType() != Type.HAND) {
      return ImmutableMultimap.of();
    }
    return getAttributeModifiers(ToolStack.from(stack), slot);
  }

  @Override
  public boolean canDisableShield(ItemStack stack, ItemStack shield, LivingEntity entity, LivingEntity attacker) {
    return !ToolDamageUtil.isBroken(stack) && toolDefinition.getData().canPerformAction(TinkerToolActions.SHIELD_DISABLE);
  }


  /* Harvest logic */

  @Override
  public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
    return ToolHarvestLogic.isEffective(ToolStack.from(stack), state);
  }

  @Override
  public boolean mineBlock(ItemStack stack, Level worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving) {
    ToolStack tool = ToolStack.from(stack);
    if (tool.isBroken()) {
      return false;
    }

    if (!worldIn.isClientSide && worldIn instanceof ServerLevel) {
      boolean isEffective = ToolHarvestLogic.isEffective(tool, state);
      ToolHarvestContext context = new ToolHarvestContext((ServerLevel) worldIn, entityLiving, state, pos, Direction.UP, true, isEffective);
      for (ModifierEntry entry : tool.getModifierList()) {
        entry.getModifier().afterBlockBreak(tool, entry.getLevel(), context);
      }
      ToolDamageUtil.damageAnimated(tool, ToolHarvestLogic.getDamage(tool, worldIn, pos, state), entityLiving);
    }

    return true;
  }

  @Override
  public float getDestroySpeed(ItemStack stack, BlockState state) {
    return ToolHarvestLogic.getDestroySpeed(stack, state);
  }

  @Override
  public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, Player player) {
    return ToolHarvestLogic.handleBlockBreak(stack, pos, player);

    // TODO: offhand harvest reconsidering
    /*// this is a really dumb hack.
    // Basically when something with silktouch harvests a block from the offhand
    // the game can't detect that. so we have to switch around the items in the hands for the break call
    // it's switched back in onBlockDestroyed
    if (DualToolHarvestUtil.shouldUseOffhand(player, pos, player.getHeldItemMainhand())) {
      ItemStack off = player.getHeldItemOffhand();

      this.switchItemsInHands(player);
      // remember, off is in the mainhand now
      CompoundNBT tag = off.getOrCreateTag();
      tag.putLong(TAG_SWITCHED_HAND_HAX, player.getEntityWorld().getGameTime());
      off.setTag(tag);
    }*/

    //return this.breakBlock(stack, pos, player);
  }


  /* Modifier interactions */

  @Override
  public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
    ModifiableItemUtil.heldInventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
  }
  
  /* Right click hooks */

  /** If true, this interaction hook should defer to the offhand */
  protected static boolean shouldInteract(@Nullable LivingEntity player, ToolStack toolStack, InteractionHand hand) {
    IModDataView volatileData = toolStack.getVolatileData();
    if (volatileData.getBoolean(NO_INTERACTION)) {
      return false;
    }
    boolean deferOffhand = volatileData.getBoolean(DEFER_OFFHAND);

    // two handed tools cannot be used in the offhand without offhanded
    if (hand == InteractionHand.OFF_HAND) {
      return deferOffhand || !toolStack.hasTag(TinkerTags.Items.TWO_HANDED);
    }

    // if mainhand is told to defer, offhand must be empty to run
    return player == null || !deferOffhand || player.getOffhandItem().isEmpty();
  }
  
  @Override
  public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
    ToolStack tool = ToolStack.from(stack);
    InteractionHand hand = context.getHand();
    if (shouldInteract(context.getPlayer(), tool, hand)) {
      for (ModifierEntry entry : tool.getModifierList()) {
        InteractionResult result = entry.getHook(TinkerHooks.BLOCK_INTERACT).beforeBlockUse(tool, entry, context, InteractionSource.RIGHT_CLICK);
        if (result.consumesAction()) {
          return result;
        }
      }
    }
    return InteractionResult.PASS;
  }

  @Override
  public InteractionResult useOn(UseOnContext context) {
    ToolStack tool = ToolStack.from(context.getItemInHand());
    InteractionHand hand = context.getHand();
    if (shouldInteract(context.getPlayer(), tool, hand)) {
      for (ModifierEntry entry : tool.getModifierList()) {
        InteractionResult result = entry.getHook(TinkerHooks.BLOCK_INTERACT).afterBlockUse(tool, entry, context, InteractionSource.RIGHT_CLICK);
        if (result.consumesAction()) {
          return result;
        }
      }
    }
    return InteractionResult.PASS;
  }

  @Override
  public InteractionResult interactLivingEntity(ItemStack stack, Player playerIn, LivingEntity target, InteractionHand hand) {
    ToolStack tool = ToolStack.from(stack);
    if (shouldInteract(playerIn, tool, hand)) {
      for (ModifierEntry entry : tool.getModifierList()) {
        InteractionResult result = entry.getHook(TinkerHooks.ENTITY_INTERACT).afterEntityUse(tool, entry, playerIn, target, hand, InteractionSource.RIGHT_CLICK);
        if (result.consumesAction()) {
          return result;
        }
      }
    }
    return InteractionResult.PASS;
  }

  @Override
  public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand hand) {
    ItemStack stack = playerIn.getItemInHand(hand);
    ToolStack tool = ToolStack.from(stack);
    if (shouldInteract(playerIn, tool, hand)) {
      for (ModifierEntry entry : tool.getModifierList()) {
        InteractionResult result = entry.getHook(TinkerHooks.GENERAL_INTERACT).onToolUse(tool, entry, playerIn, hand, InteractionSource.RIGHT_CLICK);
        if (result.consumesAction()) {
          return new InteractionResultHolder<>(result, stack);
        }
      }
      // two handed tools consume action if nothing else ran
      if (hand == InteractionHand.MAIN_HAND && stack.is(TinkerTags.Items.TWO_HANDED) && !tool.getVolatileData().getBoolean(DEFER_OFFHAND)) {
        return InteractionResultHolder.consume(stack);
      }
    }
    InteractionResult result = ToolInventoryCapability.tryOpenContainer(stack, tool, playerIn, Util.getSlotType(hand));
    return new InteractionResultHolder<>(result, stack);
  }

  @Override
  public ItemStack finishUsingItem(ItemStack stack, Level worldIn, LivingEntity entityLiving) {
    ToolStack tool = ToolStack.from(stack);
    for (ModifierEntry entry : tool.getModifierList()) {
      if (entry.getHook(TinkerHooks.GENERAL_INTERACT).onFinishUsing(tool, entry, entityLiving)) {
        return stack;
      }
    }
    return stack;
  }

  @Override
  public void releaseUsing(ItemStack stack, Level worldIn, LivingEntity entityLiving, int timeLeft) {
    ToolStack tool = ToolStack.from(stack);
    for (ModifierEntry entry : tool.getModifierList()) {
      boolean result = entry.getHook(TinkerHooks.GENERAL_INTERACT).onStoppedUsing(tool, entry, entityLiving, timeLeft);
      if (result) {
        return;
      }
    }
  }

  @Override
  public int getUseDuration(ItemStack stack) {
    ToolStack tool = ToolStack.from(stack);
    for (ModifierEntry entry : tool.getModifierList()) {
      int result = entry.getHook(TinkerHooks.GENERAL_INTERACT).getUseDuration(tool, entry);
      if (result > 0) {
        return result;
      }
    }
    return 0;
  }

  @Override
  public UseAnim getUseAnimation(ItemStack stack) {
    ToolStack tool = ToolStack.from(stack);
    for (ModifierEntry entry : tool.getModifierList()) {
      UseAnim result = entry.getHook(TinkerHooks.GENERAL_INTERACT).getUseAction(tool, entry);
      if (result != UseAnim.NONE) {
        return result;
      }
    }
    return UseAnim.NONE;
  }

  @Override
  public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
    return ModifierUtil.canPerformAction(ToolStack.from(stack), toolAction);
  }


  /* Tooltips */

  @Override
  public Component getName(ItemStack stack) {
    return TooltipUtil.getDisplayName(stack, getToolDefinition());
  }

  @Override
  public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
    TooltipUtil.addInformation(this, stack, level, tooltip, SafeClientAccess.getTooltipKey(), flag);
  }

  @Override
  public int getDefaultTooltipHideFlags(ItemStack stack) {
    return TooltipUtil.getModifierHideFlags(getToolDefinition());
  }
  

  /* Display items */

  @Override
  public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
    if (this.allowdedIn(group)) {
      ToolBuildHandler.addDefaultSubItems(this, items);
    }
  }

  @Override
  public ItemStack getRenderTool() {
    if (toolForRendering == null) {
      toolForRendering = ToolBuildHandler.buildToolForRendering(this, this.getToolDefinition());
    }
    return toolForRendering;
  }


  /* Misc */

  @Override
  public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
    return shouldCauseReequipAnimation(oldStack, newStack, false);
  }

  @Override
  public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
    return ModifiableItemUtil.shouldCauseReequip(oldStack, newStack, slotChanged);
  }


  /* Helpers */

  /**
   * Creates a raytrace and casts it to a BlockRayTraceResult
   *
   * @param worldIn the world
   * @param player the given player
   * @param fluidMode the fluid mode to use for the raytrace event
   *
   * @return  Raytrace
   */
  public static BlockHitResult blockRayTrace(Level worldIn, Player player, ClipContext.Fluid fluidMode) {
    return Item.getPlayerPOVHitResult(worldIn, player, fluidMode);
  }
}
