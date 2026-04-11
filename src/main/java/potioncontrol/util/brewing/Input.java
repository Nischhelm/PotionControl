package potioncontrol.util.brewing;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public interface Input {
    boolean isInput(ItemStack stack);
    ItemStack getOutput(ItemStack input);
    List<ItemStack> asItemStacks();

    class ItemStackInput implements Input {
        public final ItemStack stack;
        public ItemStackInput(ItemStack stack) {
            this.stack = stack;
        }
        @Override
        public boolean isInput(ItemStack stack) {
            return areItemStacksEqual(this.stack, stack);
        }
        @Override
        public ItemStack getOutput(ItemStack input) {
            return stack.copy();
        }
        @Override
        public List<ItemStack> asItemStacks(){
            return Collections.singletonList(stack);
        }
    }
    class PotionTypeInput implements Input {
        public final PotionType type;
        public PotionTypeInput(PotionType type) {
            this.type = type;
        }
        @Override
        public boolean isInput(ItemStack stack) {
            return isVanillaContainer(stack) && this.type == PotionUtils.getPotionFromItem(stack);
        }
        @Override
        public ItemStack getOutput(ItemStack input) {
            return PotionUtils.addPotionToItemStack(input.copy(), this.type);
        }
        @Override
        public List<ItemStack> asItemStacks(){
            return items.stream().map(item -> PotionUtils.addPotionToItemStack(new ItemStack(item), type)).collect(Collectors.toList());
        }

        private static final List<Item> items = Arrays.asList(Items.POTIONITEM, Items.SPLASH_POTION, Items.LINGERING_POTION);

        private static boolean isVanillaContainer(ItemStack stack) {
            return items.contains(stack.getItem());
        }
    }
    static Input getFromObj(Object obj) {
        if(obj instanceof PotionType) return new PotionTypeInput((PotionType)obj);
        else if(obj instanceof ItemStack) return new ItemStackInput((ItemStack)obj);
        return null;
    }

    static boolean isPotionTypeOf(Input input, PotionType potionType){
        return input instanceof Input.PotionTypeInput && ((Input.PotionTypeInput) input).type == potionType;
    }

    static boolean areItemStacksEqual(ItemStack stack1, ItemStack stack2) {
        if(!ItemStack.areItemsEqual(stack1, stack2)) return false; //no count check
        if(!NBTUtil.areNBTEquals(stack1.getTagCompound(), stack2.getTagCompound(), true)) return false; //this is asymmetric. tags2 only needs to contain tags1, can have more content
        return true;
    }
}
