package slimeknights.tconstruct.library.tools.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import lombok.Getter;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.util.ITooltipFlag.TooltipFlags;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.EquipmentSlotType.Group;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Rarity;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants.NBT;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.IndestructibleItemEntity;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.capability.ToolCapabilityProvider;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.helper.ToolBuildHandler;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.helper.TooltipUtil;
import slimeknights.tconstruct.library.tools.nbt.MaterialIdNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.TooltipKey;
import slimeknights.tconstruct.library.utils.Util;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * A standard modifiable item which implements melee hooks
 * This class handles how all the modifier hooks and display data for items made out of different materials
 */
public class ModifiableItem extends Item implements IModifiableDisplay, IModifiableWeapon {
  protected static final UUID REACH_MODIFIER = UUID.fromString("9b26fa32-5774-4b4e-afc3-b4055ecb1f6a");

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


  /* Loading */

  @Nullable
  @Override
  public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
    return new ToolCapabilityProvider(stack);
  }

  @Override
  public boolean updateItemStackNBT(CompoundNBT nbt) {
    // get the internal tag, because this method is weird
    if (nbt.contains("tag", NBT.TAG_COMPOUND)) {
      CompoundNBT tag = nbt.getCompound("tag");

      // if the stack has materials, resolve all material redirects
      if (tag.contains(ToolStack.TAG_MATERIALS, NBT.TAG_LIST)) {
        MaterialIdNBT stored = MaterialIdNBT.readFromNBT(tag.getList(ToolStack.TAG_MATERIALS, NBT.TAG_STRING));
        MaterialIdNBT resolved = stored.resolveRedirects();
        if (resolved != stored) {
          resolved.updateNBT(tag);
        }
      }

      // when the itemstack is loaded from NBT we recalculate all the data
      // stops things from being wrong if modifiers or materials change
      ToolStack.from(this, getToolDefinition(), tag).rebuildStats();
    }
    // return value shouldn't matter since it's never checked
    return true;
  }

  @Override
  public void onCreated(ItemStack stack, World worldIn, PlayerEntity playerIn) {
    ToolStack.ensureInitialized(stack, getToolDefinition());
  }


  /* Display */

  @Override
  public boolean hasEffect(ItemStack stack) {
    // we use enchantments to handle some modifiers, so don't glow from them
    // however, if a modifier wants to glow let them
    return ToolStack.from(stack).getVolatileData().getBoolean(SHINY);
  }

  @Override
  public Rarity getRarity(ItemStack stack) {
    int rarity = ToolStack.from(stack).getVolatileData().getInt(RARITY);
    return Rarity.values()[MathHelper.clamp(rarity, 0, 3)];
  }


  /* Indestructible items */

  @Override
  public boolean hasCustomEntity(ItemStack stack) {
    return IndestructibleItemEntity.hasCustomEntity(stack);
  }

  @Override
  public Entity createEntity(World world, Entity original, ItemStack stack) {
    return IndestructibleItemEntity.createFrom(world, original, stack);
  }


  /* Damage/Durability */

  @Override
  public boolean isRepairable(ItemStack stack) {
    // handle in the tinker station
    return false;
  }

  @Override
  public boolean isDamageable() {
    return true;
  }

  @Override
  public int getMaxDamage(ItemStack stack) {
    if (!isDamageable()) {
      return 0;
    }
    ToolStack tool = ToolStack.from(stack);
    int durability = tool.getStats().getInt(ToolStats.DURABILITY);
    // vanilla deletes tools if max damage == getDamage, so tell vanilla our max is one higher when broken
    return tool.isBroken() ? durability + 1 : durability;
  }

  @Override
  public int getDamage(ItemStack stack) {
    if (!isDamageable()) {
      return 0;
    }
    return ToolStack.from(stack).getDamage();
  }

  @Override
  public void setDamage(ItemStack stack, int damage) {
    if (isDamageable()) {
      ToolStack.from(stack).setDamage(damage);
    }
  }

  @Override
  public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T damager, Consumer<T> onBroken) {
    // We basically emulate Itemstack.damageItem here. We always return 0 to skip the handling in ItemStack.
    // If we don't tools ignore our damage logic
    if (isDamageable() && ToolDamageUtil.damage(ToolStack.from(stack), amount, damager, stack)) {
      onBroken.accept(damager);
    }

    return 0;
  }


  /* Durability display */

  @Override
  public boolean showDurabilityBar(ItemStack stack) {
    return ToolDamageUtil.showDurabilityBar(stack);
  }

  @Override
  public double getDurabilityForDisplay(ItemStack stack) {
    return ToolDamageUtil.getDamageForDisplay(stack);
  }

  @Override
  public int getRGBDurabilityForDisplay(ItemStack stack) {
    return ToolDamageUtil.getRGBDurabilityForDisplay(stack);
  }


  /* Attacking */

  @Override
  public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity entity) {
    return ToolAttackUtil.attackEntity(stack, this, player, entity);
  }

  @Override
  public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
    CompoundNBT nbt = stack.getTag();
    if (nbt == null || nbt.getBoolean(TooltipUtil.KEY_DISPLAY)) {
      return ImmutableMultimap.of();
    }

    ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
    ToolStack tool = ToolStack.from(stack);
    if (!tool.isBroken()) {
      // base stats
      if (slot == EquipmentSlotType.MAINHAND) {
        StatsNBT statsNBT = tool.getStats();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "tconstruct.tool.attack_damage", statsNBT.getFloat(ToolStats.ATTACK_DAMAGE), AttributeModifier.Operation.ADDITION));
        // base attack speed is 4, but our numbers start from 4
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(ATTACK_SPEED_MODIFIER, "tconstruct.tool.attack_speed", statsNBT.getFloat(ToolStats.ATTACK_SPEED) - 4d, AttributeModifier.Operation.ADDITION));
      }

      // grab attributes from modifiers, only do for hands (other slots would just be weird)
      if (slot.getSlotType() == Group.HAND) {
        BiConsumer<Attribute,AttributeModifier> attributeConsumer = builder::put;
        for (ModifierEntry entry : tool.getModifierList()) {
          entry.getModifier().addAttributes(tool, entry.getLevel(), slot, attributeConsumer);
        }
      }
    }

    return builder.build();
  }


  /* Modifier interactions */

  @Override
  public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
    super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);

    // don't care about non-living, they skip most tool context
    if (entityIn instanceof LivingEntity) {
      ToolStack tool = ToolStack.from(stack);
      List<ModifierEntry> modifiers = tool.getModifierList();
      if (!modifiers.isEmpty()) {
        LivingEntity living = (LivingEntity) entityIn;
        // we pass in the stack for most custom context, but for the sake of armor its easier to tell them that this is the correct slot for effects
        boolean isHeld = isSelected || living.getHeldItemOffhand() == stack;
        for (ModifierEntry entry : modifiers) {
          entry.getModifier().onInventoryTick(tool, entry.getLevel(), worldIn, living, itemSlot, isSelected, isHeld, stack);
        }
      }
    }
  }
  
  /* Right click hooks */

  /** If true, this interaction hook should defer to the offhand */
  protected static boolean shouldInteract(@Nullable LivingEntity player, ToolStack toolStack, Hand hand) {
    return hand == Hand.OFF_HAND || player == null || !toolStack.getVolatileData().getBoolean(DEFER_OFFHAND) || player.getHeldItemOffhand().isEmpty();
  }
  
  @Override
  public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
    ToolStack tool = ToolStack.from(stack);
    Hand hand = context.getHand();
    if (shouldInteract(context.getPlayer(), tool, hand)) {
      EquipmentSlotType slot = Util.getSlotType(hand);
      for (ModifierEntry entry : tool.getModifierList()) {
        ActionResultType result = entry.getModifier().beforeBlockUse(tool, entry.getLevel(), context, slot);
        if (result.isSuccessOrConsume()) {
          return result;
        }
      }
    }
    return ActionResultType.PASS;
  }

  @Override
  public ActionResultType onItemUse(ItemUseContext context) {
    ToolStack tool = ToolStack.from(context.getItem());
    Hand hand = context.getHand();
    if (shouldInteract(context.getPlayer(), tool, hand)) {
      EquipmentSlotType slot = Util.getSlotType(hand);
      for (ModifierEntry entry : tool.getModifierList()) {
        ActionResultType result = entry.getModifier().afterBlockUse(tool, entry.getLevel(), context, slot);
        if (result.isSuccessOrConsume()) {
          return result;
        }
      }
    }
    return ActionResultType.PASS;
  }

  @Override
  public ActionResultType itemInteractionForEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand) {
    ToolStack tool = ToolStack.from(stack);
    if (shouldInteract(playerIn, tool, hand)) {
      EquipmentSlotType slot = Util.getSlotType(hand);
      for (ModifierEntry entry : tool.getModifierList()) {
        ActionResultType result = entry.getModifier().afterEntityUse(tool, entry.getLevel(), playerIn, target, hand, slot);
        if (result.isSuccessOrConsume()) {
          return result;
        }
      }
    }
    return ActionResultType.PASS;
  }

  @Override
  public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand hand) {
    ItemStack stack = playerIn.getHeldItem(hand);
    ToolStack tool = ToolStack.from(playerIn.getHeldItem(hand));
    if (shouldInteract(playerIn, tool, hand)) {
      EquipmentSlotType slot = Util.getSlotType(hand);
      for (ModifierEntry entry : tool.getModifierList()) {
        ActionResultType result = entry.getModifier().onToolUse(tool, entry.getLevel(), worldIn, playerIn, hand, slot);
        if (result.isSuccessOrConsume()) {
          return new ActionResult<>(result, stack);
        }
      }
    }
    return ActionResult.resultPass(stack);
  }

  @Override
  public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving) {
    ToolStack tool = ToolStack.from(stack);
    for (ModifierEntry entry : tool.getModifierList()) {
      if (entry.getModifier().onFinishUsing(tool, entry.getLevel(), worldIn, entityLiving)) {
        return stack;
      }
    }
    return stack;
  }

  @Override
  public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
    ToolStack tool = ToolStack.from(stack);
    for (ModifierEntry entry : tool.getModifierList()) {
      boolean result = entry.getModifier().onStoppedUsing(tool, entry.getLevel(), worldIn, entityLiving, timeLeft);
      if (result) {
        return;
      }
    }
  }

  @Override
  public int getUseDuration(ItemStack stack) {
    ToolStack tool = ToolStack.from(stack);
    for (ModifierEntry entry : tool.getModifierList()) {
      int result = entry.getModifier().getUseDuration(tool, entry.getLevel());
      if (result > 0) {
        return result;
      }
    }
    return 0;
  }

  @Override
  public UseAction getUseAction(ItemStack stack) {
    ToolStack tool = ToolStack.from(stack);
    for (ModifierEntry entry : tool.getModifierList()) {
      UseAction result = entry.getModifier().getUseAction(tool, entry.getLevel());
      if (result != UseAction.NONE) {
        return result;
      }
    }
    return UseAction.NONE;
  }


  /* Tooltips */

  @Override
  public ITextComponent getDisplayName(ItemStack stack) {
    return TooltipUtil.getDisplayName(stack, getToolDefinition());
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
    TooltipUtil.addInformation(this, stack, tooltip, TooltipKey.fromScreen(), flagIn == TooltipFlags.ADVANCED);
  }


  /* Display items */

  @Override
  public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
    if (this.isInGroup(group)) {
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
    if (oldStack == newStack) {
      return false;
    }
    // basic changes
    if (slotChanged || oldStack.getItem() != newStack.getItem()) {
      return true;
    }

    // if the tool props changed,
    ToolStack oldTool = ToolStack.from(oldStack);
    ToolStack newTool = ToolStack.from(newStack);

    // check if modifiers or materials changed
    if (!oldTool.getMaterialsList().equals(newTool.getMaterialsList())) {
      return true;
    }
    if (!oldTool.getModifierList().equals(newTool.getModifierList())) {
      return true;
    }

    // if the attributes changed, reequip
    Multimap<Attribute, AttributeModifier> attributesNew = newStack.getAttributeModifiers(EquipmentSlotType.MAINHAND);
    Multimap<Attribute, AttributeModifier> attributesOld = oldStack.getAttributeModifiers(EquipmentSlotType.MAINHAND);
    if (attributesNew.size() != attributesOld.size()) {
      return true;
    }
    for (Attribute attribute : attributesOld.keySet()) {
      if (!attributesNew.containsKey(attribute)) {
        return true;
      }
      Iterator<AttributeModifier> iter1 = attributesNew.get(attribute).iterator();
      Iterator<AttributeModifier> iter2 = attributesOld.get(attribute).iterator();
      while (iter1.hasNext() && iter2.hasNext()) {
        if (!iter1.next().equals(iter2.next())) {
          return true;
        }
      }
    }
    // no changes, no reequip
    return false;
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
  public static BlockRayTraceResult blockRayTrace(World worldIn, PlayerEntity player, RayTraceContext.FluidMode fluidMode) {
    return Item.rayTrace(worldIn, player, fluidMode);
  }
}
