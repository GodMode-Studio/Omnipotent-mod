package com.omnipotent.util;


/**
 * @Author <a href="gamerYToffi:"
 * este método retorna verdadeiro caso a string contenha apenas numeros inteiros (O que incluir o sinal de menos) e falso caso contrario.
 */
public class UtillityHelp {
    public static boolean isJustNumber(String text) {
        return text.matches("[-]?\\d+");
    }

    /**
     * @Author <a href="gamerYToffi:"
     *Este método com base no valor recebido e na largura da tela retorna um valor equivalente para qualquer monitor.
     */
    public static int getEquivalentValueOfscreenHeight(int value, int height) {
        double ratio = (double) value / height;
        int equivalentValue = (int) (height * ratio);
        return equivalentValue;
    }
    /**
     * @Author <a href="gamerYToffi:"
     *Este método com base no valor recebido e na largura da tela retorna um valor equivalente para qualquer monitor.
     */
    public static int getEquivalentValueOfscreenWidth(int value, int width) {
        double ratio = (double) value / width;
        int equivalentValue = (int) (width * ratio);
        return equivalentValue;
    }
}
