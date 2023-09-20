package com.omnipotent.util;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;

import java.util.ArrayList;
import java.util.Iterator;

public class NbtListUtil {
    public static final String divisionUUIDAndName = "::";

    /**
     * Este método retorna todos os elementos de uma NBTTagList, ele não remove os caracteres "" da taglist.
     *
     * @Author gamerYToffi
     */
    public static ArrayList<NBTBase> getElementsOfNbt(NBTTagList nbt) {
        Iterator<NBTBase> iterator = nbt.iterator();
        ArrayList<NBTBase> nbtList = new ArrayList<>();
        while (iterator.hasNext()) {
            nbtList.add(iterator.next());
        }
        return nbtList;
    }

    /**
     * Este método retorna os valores de uma NBTTagList em formato de String, ele remove os caracteres "".
     *
     * @Author gamerYToffi
     */
    public static ArrayList<String> getValueOfElementsOfNbtList(NBTTagList nbt) {
        ArrayList<String> values = new ArrayList<>();
        getElementsOfNbt(nbt).forEach(element -> values.add(element.toString().substring(1, element.toString().length() - 1)));
        return values;
    }


    /**
     * Este método serve para NBTtagList que guardam UUID e usam o divisionUUIDAndName ele retorna o uuid, ele remove os caracteres "".
     *
     * @Author gamerYToffi
     */
    public static ArrayList<String> getUUIDOfNbtList(NBTTagList nbt) {
        ArrayList<String> values = new ArrayList<>();
        getElementsOfNbt(nbt).forEach(element -> values.add(element.toString().substring(1, element.toString().length() - 1).split(divisionUUIDAndName)[0]));
        return values;
    }

    /**
     * Este método serve para NBTtagList que guardam names e usam o divisionUUIDAndName, ele retorna o name, ele remove os caracteres "".
     *
     * @Author gamerYToffi
     */
    public static ArrayList<String> getNameDOfNbtList(NBTTagList nbt) {
        ArrayList<String> values = new ArrayList<>();
        getElementsOfNbt(nbt).forEach(element -> values.add(element.toString().substring(1, element.toString().length() - 1).split(divisionUUIDAndName)[1]));
        return values;
    }

    /**
     * Este método serve para NBTtagList que guardam names e uuid com o divisionUUIDAndName, ele remove o elemento da tagList que retorna true em seu método equals.
     * Se o int passado para esse método for 0 o equals usara uuid para comparação se for 1 sera name.
     *
     * @Author gamerYToffi
     */
    public static void removeString(NBTTagList nbt, String element, int type) {
        if (type > 1 || type < 0)
            throw new IllegalArgumentException("number of type is invalid");
        ArrayList<String> listElements = removeRedudantChars(nbt);
        for (int c = 0; c < listElements.size(); c++) {
            String next = listElements.get(c).toString().split(divisionUUIDAndName)[type];
            if (next.equals(element)) {
                nbt.removeTag(c);
                break;
            }
        }
    }

    /**
     * Este remove os "" de String em NbtTasList ele não altera a lista original ele retorna uma cópia da lista aonde cada elemento esta sem as aspas.
     *
     * @Author gamerYToffi
     */
    public static ArrayList<String> removeRedudantChars(NBTTagList nbt) {
        ArrayList<String> list = new ArrayList<>();
        for (int c = 0; c < nbt.tagCount(); c++) {
            String next = nbt.get(c).toString();
            list.add(next.substring(1, next.length() - 1));
        }
        return list;
    }

    /**
     * Este Método retorna true se o elemento ja existe, false caso contrario, ele remove os "".
     *
     * @Author gamerYToffi
     */
    public static boolean isElementAlreadyExists(NBTTagList nbt, String element) {
        return removeRedudantChars(nbt).contains(element);
    }
}
