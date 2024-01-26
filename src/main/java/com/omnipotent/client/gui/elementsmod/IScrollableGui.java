package com.omnipotent.client.gui.elementsmod;

public interface IScrollableGui {

    /**
     * Este método deve ser implementado com a lógica responsavel por scrollar a tela, normalmente mudando o eixo Y dos elementos scrollaveis.
     *
     * @param scroll
     */
    void updateScrollOffset(int scroll);


    /**
     * Este método deve ser implementado para conter a lógica responsavel por renderizar os itens scrollaveis na tela.
     *
     * @param mouseX
     * @param mouseY
     * @param partialTicks
     */
    void renderElements(int mouseX, int mouseY, float partialTicks);


    /**
     * Este método deve ser implementado com a lógica responsavel por adicionar os elementos a uma lista que será renderizada no {@link IScrollableGui#renderElements(int, int, float)}]}
     */
    void addElements();

    void resetGui();
}
