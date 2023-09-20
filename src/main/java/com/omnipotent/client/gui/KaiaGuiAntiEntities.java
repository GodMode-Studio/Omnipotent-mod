package com.omnipotent.client.gui;

import com.omnipotent.server.network.NetworkRegister;
import com.omnipotent.server.network.nbtpackets.KaiaNbtPacket;
import com.omnipotent.util.KaiaUtil;
import com.omnipotent.util.NbtListUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.omnipotent.util.KaiaConstantsNbt.entitiesCantKill;
import static com.omnipotent.util.NbtListUtil.divisionUUIDAndName;
import static com.omnipotent.util.UtilityHelper.getEquivalentValue;

public class KaiaGuiAntiEntities extends GuiScreen implements IGuiPages {

    private final EntityPlayer player;
    private List<GuiTextField> guisInPage = new ArrayList<>();
    private int page = 0;
    private int oldPage = 0;
    private int idButtons = -1;
    private boolean lastPage = false;
    private int maxYOfScreen;
    private List<String> entitiesThatCannotBeKilled = new ArrayList<>();
    int quantityElementsOfPage = 0;
    Map<Integer, String> uuidAndOrder = new HashMap<>();

    public KaiaGuiAntiEntities(EntityPlayer player) {
        this.player = player;
    }

    @Override
    public void initGui() {
        super.initGui();
        maxYOfScreen = getEquivalentValue(463, height);
        addPageNavigationButtons();
        entitiesThatCannotBeKilled.clear();
        entitiesThatCannotBeKilled.addAll(NbtListUtil.getValueOfElementsOfNbtList(KaiaUtil.getKaiaInMainHand(player).getTagCompound().getTagList(entitiesCantKill, 8)));
        addPages();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        entitiesThatCannotBeKilled.clear();
        entitiesThatCannotBeKilled.addAll(NbtListUtil.getValueOfElementsOfNbtList(KaiaUtil.getKaiaInMainHand(player).getTagCompound().getTagList(entitiesCantKill, 8)));
        addPages();
        drawRect(getEquivalentValue(0, width), getEquivalentValue(0, height), getEquivalentValue(500, width), getEquivalentValue(500, height), Color.BLACK.getRGB());
        drawString(fontRenderer, "Entidades que não podem ser mortas", getEquivalentValue(8, width), getEquivalentValue(30, height), Color.WHITE.getRGB());
        guisInPage.forEach(guiTextField -> guiTextField.drawTextBox());
        buttonList.forEach(button -> button.drawButton(Minecraft.getMinecraft(), mouseX, mouseY, partialTicks));
        if(mouseX>getEquivalentValue(500, width))
            player.closeScreen();
    }

    @Override
    public void addPages() {
        guisInPage.clear();
        uuidAndOrder.clear();
        int spacingBetweenGuis = getEquivalentValue(24, height);
        int heightForGui = getEquivalentValue(17, height);
        int minYForPage = getEquivalentValue(52, height);
        int maxYForPage = maxYOfScreen - getEquivalentValue(22, height);
        int xAxis = getEquivalentValue(14, width);
        int id = -1;
        int i = maxYForPage - minYForPage;
        this.quantityElementsOfPage = (int) Math.ceil(i / (108 / 8.0));
        int pos = 0;
        int order = -1;
        for (int c = 0; c < quantityElementsOfPage; c++) {
            int index = quantityElementsOfPage * page + c;
            if (index >= entitiesThatCannotBeKilled.size())
                break;
            if (entitiesThatCannotBeKilled.get(entitiesThatCannotBeKilled.size() - 1).split(divisionUUIDAndName)[0].equals(entitiesThatCannotBeKilled.get(index).split(divisionUUIDAndName)[0]))
                lastPage = true;
            if (c == 0) {
                guisInPage.add(new GuiTextField(++id, fontRenderer, xAxis, minYForPage, getEquivalentValue(115, width), heightForGui));
                guisInPage.get(guisInPage.size() - 1).setText(entitiesThatCannotBeKilled.get(index).split(divisionUUIDAndName)[1]);
            } else {
//                if(c-1==quantityElementsOfPage && xAxis<){
//                    c=0;
//
//                }
                guisInPage.add(new GuiTextField(++id, fontRenderer, xAxis, minYForPage + spacingBetweenGuis * ++pos, getEquivalentValue(115, width), heightForGui));
                guisInPage.get(guisInPage.size() - 1).setText(entitiesThatCannotBeKilled.get(index).split(divisionUUIDAndName)[1]);
            }
            uuidAndOrder.put(++order, entitiesThatCannotBeKilled.get(index).split(divisionUUIDAndName)[0]);
        }
    }

    @Override
    public void addPageNavigationButtons() {
        GuiButton paginaAnterior = new GuiButton(++idButtons, getEquivalentValue(100, width), getEquivalentValue(440, height), I18n.format("guikaia.enchant.previouspage"));
        paginaAnterior.height = 11;
        String displayString2 = paginaAnterior.displayString.replaceAll("\\s", "");
        paginaAnterior.width = 8 * displayString2.length();
        buttonList.add(paginaAnterior);
        GuiButton proximaPagina = new GuiButton(++idButtons, getEquivalentValue(250, width), getEquivalentValue(440, height), I18n.format("guikaia.enchant.nextpage"));
        proximaPagina.height = 11;
        String displayString = proximaPagina.displayString.replaceAll("\\s", "");
        proximaPagina.width = 8 * displayString.length();
        buttonList.add(proximaPagina);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        for (int c = 0; c < guisInPage.size(); c++) {
            GuiTextField gui = guisInPage.get(c);
            gui.mouseClicked(mouseX, mouseY, mouseButton);
            if (gui.isFocused()) {
                NetworkRegister.ACESS.sendToServer(new KaiaNbtPacket(entitiesCantKill, uuidAndOrder.get(c), 1));
                gui.setFocused(false);
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        switch (button.id) {
            case 0:
                if (!(page == 0)) {
                    if (lastPage)
                        lastPage = false;
                    oldPage = page;
                    this.page--;
                }
                break;
            case 1:
                if (!lastPage) {
                    oldPage = page;
                    this.page++;
                }
                break;
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
