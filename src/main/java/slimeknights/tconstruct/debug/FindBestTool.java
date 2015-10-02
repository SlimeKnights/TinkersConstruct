package slimeknights.tconstruct.debug;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentSelector;
import net.minecraft.util.ChatComponentText;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Nullable;

import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.ToolMaterialStats;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.utils.ToolHelper;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.item.Pickaxe;

public class FindBestTool extends CommandBase {

  @Override
  public String getCommandName() {
    return "findBestTool";
  }

  @Override
  public String getCommandUsage(ICommandSender sender) {
    return "";
  }

  @Override
  public void processCommand(ICommandSender sender, String[] args) throws CommandException {
    if(sender.getEntityWorld().isRemote) return;
    ToolCore tool = TinkerTools.shovel;
    List<Triple<ItemStack, ImmutableList<Material>, Object[]>> results = Lists.newArrayList();

    @SuppressWarnings("unchecked")
    Function<ItemStack, ?> functions[] = new Function[] {
        new Function<ItemStack, Integer>() {
          @Override
          public Integer apply(ItemStack itemStack) {
            return ToolHelper.getDurability(itemStack);
          }
        },
        new Function<ItemStack, Float>() {
          @Override
          public Float apply(ItemStack itemStack) {
            return ToolHelper.getMiningSpeed(itemStack);
          }
        },
        new Function<ItemStack, Float>() {
          @Override
          public Float apply(ItemStack itemStack) {
            return ToolHelper.getAttack(itemStack);
          }
        }
    };

    recurse(tool, ImmutableList.<Material>of(), results, functions);

    //Collections.sort(results, Comp.INSTANCE);

    List<Integer> durabilities = Lists.transform(results, new com.google.common.base.Function<Triple<ItemStack,ImmutableList<Material>,Object[]>, Integer>() {
      @Nullable
      @Override
      public Integer apply(Triple<ItemStack, ImmutableList<Material>, Object[]> input) {
        return (Integer) input.getRight()[0];
      }
    });

    List<Float> speeds = Lists.transform(results, new com.google.common.base.Function<Triple<ItemStack,ImmutableList<Material>,Object[]>, Float>() {
      @Nullable
      @Override
      public Float apply(Triple<ItemStack, ImmutableList<Material>, Object[]> input) {
        return (Float) input.getRight()[1];
      }
    });
    List<Float> attacks = Lists.transform(results, new com.google.common.base.Function<Triple<ItemStack,ImmutableList<Material>,Object[]>, Float>() {
      @Nullable
      @Override
      public Float apply(Triple<ItemStack, ImmutableList<Material>, Object[]> input) {
        return (Float) input.getRight()[2];
      }
    });

    // calculate upper quartile of durability
    final int durQuart = (int) getQuartile(Ordering.natural().reverse().sortedCopy(durabilities));
    final float speedQuart = (float) getQuartile(Ordering.natural().reverse().sortedCopy(speeds));
    final float attackQuart = (float) getQuartile(Ordering.natural().reverse().sortedCopy(attacks));

    Predicate<Triple<ItemStack, ImmutableList<Material>, Object[]>> filter1 = new Predicate<Triple<ItemStack, ImmutableList<Material>, Object[]>>() {

      @Override
      public boolean apply(@Nullable Triple<ItemStack, ImmutableList<Material>, Object[]> entry) {
        return ((Integer)entry.getRight()[0]) > durQuart
               && ((Float)entry.getRight()[1]) > speedQuart;
      }
    };

    Predicate<Triple<ItemStack, ImmutableList<Material>, Object[]>> filter2 = new Predicate<Triple<ItemStack, ImmutableList<Material>, Object[]>>() {

      @Override
      public boolean apply(@Nullable Triple<ItemStack, ImmutableList<Material>, Object[]> entry) {
        return ((Integer)entry.getRight()[0]) > durQuart
               && ((Float)entry.getRight()[2]) > attackQuart;
      }
    };

    Predicate<Triple<ItemStack, ImmutableList<Material>, Object[]>> filter3 = new Predicate<Triple<ItemStack, ImmutableList<Material>, Object[]>>() {

      @Override
      public boolean apply(@Nullable Triple<ItemStack, ImmutableList<Material>, Object[]> entry) {
        return ((Integer)entry.getRight()[0]) > durQuart
               && ((Float)entry.getRight()[1]) > speedQuart
               && ((Float)entry.getRight()[2]) > attackQuart;
      }
    };

    // get all tools that are above in both quartiles
    Collection<Triple<ItemStack, ImmutableList<Material>, Object[]>> best = Collections2.filter(results, filter1);

    sender.addChatMessage(new ChatComponentText(String.format("%d are in the upper quartile of stats (%d; %f; %f)", best.size(), durQuart, speedQuart, attackQuart)));

    best = new Ordering<Triple<ItemStack, ImmutableList<Material>, Object[]>>() {

      @Override
      public int compare(@Nullable Triple<ItemStack, ImmutableList<Material>, Object[]> left, @Nullable Triple<ItemStack, ImmutableList<Material>, Object[]> right) {
        return (Integer)right.getRight()[0] - (Integer)left.getRight()[0];
      }
    }.sortedCopy(best);

    for(Triple<ItemStack, ImmutableList<Material>, Object[]> foo : best) {
      StringBuilder text = new StringBuilder();
      text.append("Materials: ");
      for(Material mat : foo.getMiddle()) {
        text.append(mat.getIdentifier());
        text.append(" ");
      }

      text.append("- ");
      text.append("Dur: ");
      text.append(foo.getRight()[0]);
      text.append(" Speed: ");
      text.append(foo.getRight()[1]);
      text.append(" Dmg: ");
      text.append(foo.getRight()[2]);

      sender.addChatMessage(foo.getLeft().getChatComponent().appendSibling(new ChatComponentText(text.toString())));
      //System.out.println(text.toString());
    }
  }

  public void recurse(ToolCore tool, ImmutableList<Material> materials, List<Triple<ItemStack, ImmutableList<Material>, Object[]>> results, Function<ItemStack, ?> fns[]) {
    // not enough materials yet, recurse
    if(tool.requiredComponents.length > materials.size()) {
      for(Material mat : TinkerRegistry.getAllMaterials()) {
        if(!mat.hasStats(ToolMaterialStats.TYPE)) continue;
        ImmutableList.Builder<Material> mats = ImmutableList.builder();
        mats.addAll(materials);
        mats.add(mat);
        recurse(tool, mats.build(), results, fns);
      }
    }
    // enough materials, build it and do stuff with it!
    else {
      ItemStack stack = tool.buildItem(materials);
      Object[] values = new Object[fns.length];
      for(int i = 0; i < fns.length; i++) {
        values[i] = fns[i].apply(stack);
      }
      results.add(Triple.of(stack, materials, values));
    }
  }

  private enum Comp implements Comparator<Pair<ImmutableList<Material>, Integer>> {
    INSTANCE;

    @Override
    public int compare(Pair<ImmutableList<Material>, Integer> o1, Pair<ImmutableList<Material>, Integer> o2) {
      return o2.getRight() - o1.getRight();
    }
  }

  private <T extends Number> double getQuartile(List<T> entries) {
    int quartile = 8;
    if(entries.size()%2 == 1) {
      return entries.get(entries.size()/quartile).doubleValue();
    }

    T v1 = entries.get(entries.size()/quartile);
    T v2 = entries.get(entries.size()/quartile + 1);
    return ((v1.doubleValue() + v2.doubleValue())/2d);
  }
}
