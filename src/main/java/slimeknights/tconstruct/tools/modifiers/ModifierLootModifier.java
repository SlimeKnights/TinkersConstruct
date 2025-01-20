package slimeknights.tconstruct.tools.modifiers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import slimeknights.mantle.loot.AbstractLootModifierBuilder.GenericLootModifierBuilder;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.tools.capability.EntityModifierCapability;
import slimeknights.tconstruct.library.tools.capability.PersistentDataCapability;
import slimeknights.tconstruct.library.tools.helper.ModifierLootingHandler;
import slimeknights.tconstruct.library.tools.nbt.DummyToolStack;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import javax.annotation.Nonnull;

/** Global loot modifier for modifiers */
public class ModifierLootModifier extends LootModifier {
  public static final Codec<ModifierLootModifier> CODEC = RecordCodecBuilder.create(inst -> codecStart(inst).apply(inst, ModifierLootModifier::new));

  protected ModifierLootModifier(LootItemCondition[] conditionsIn) {
    super(conditionsIn);
  }

  /** Creates a builder for datagen */
  public static GenericLootModifierBuilder<ModifierLootModifier> builder() {
    return new GenericLootModifierBuilder<>(ModifierLootModifier::new);
  }

  @Nonnull
  @Override
  protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
    // tool is for harvest
    ItemStack stack = context.getParamOrNull(LootContextParams.TOOL);

    // if null, try killer entity
    if (stack == null) {
      // if this loot is due to a projectile fired by one of our tools, then use that projectile as the loot source
      // prevents weirdness when held tool switches after firing a projectile
      if (context.getParamOrNull(LootContextParams.DIRECT_KILLER_ENTITY) instanceof Projectile projectile) {
        ModifierNBT modifiers = EntityModifierCapability.getOrEmpty(projectile);

        // no need to build the dummy tool if we lack modifiers
        if (!modifiers.isEmpty()) {
          ModDataNBT persistentData = projectile.getCapability(PersistentDataCapability.CAPABILITY).orElseGet(ModDataNBT::new);
          IToolStackView dummyTool = new DummyToolStack(Items.AIR, modifiers, persistentData);
          for (ModifierEntry entry : modifiers) {
            entry.getHook(ModifierHooks.PROCESS_LOOT).processLoot(dummyTool, entry, generatedLoot, context);
          }
        }
        // don't run held item hook for projectiles, if you didn't put the modifier on the projectile we shouldn't count it
        return generatedLoot;
      }

      // not a projectile causing it, fetch the killer entity directly from loot context
      if (context.getParamOrNull(LootContextParams.KILLER_ENTITY) instanceof LivingEntity living) {
        stack = living.getItemBySlot(ModifierLootingHandler.getLootingSlot(living));
      }
    }
    // hopefully one of the two worked
    if (stack != null && stack.is(TinkerTags.Items.LOOT_CAPABLE_TOOL)) {
      ToolStack tool = ToolStack.from(stack);
      if (!tool.isBroken()) {
        for (ModifierEntry entry : tool.getModifierList()) {
          entry.getHook(ModifierHooks.PROCESS_LOOT).processLoot(tool, entry, generatedLoot, context);
        }
      }
    }
    return generatedLoot;
  }

  @Override
  public Codec<? extends IGlobalLootModifier> codec() {
    return CODEC;
  }
}
