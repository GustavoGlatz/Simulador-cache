package entity;
import java.util.*;

public class RAM {

    private final Map<Integer, Integer> ram;
    private static final int TAMANHO_DO_BLOCO = 5;

    public RAM() {
        ram = new HashMap<>();
    }

    public Integer getLinha(int dado){ return ram.get(dado); }

    public void setLinha(int indice, int dado) {
        ram.put(indice, dado);
    }

    public int getIndiceBloco(int dado) {
        Integer indiceEncontrado = null;

        for (Map.Entry<Integer, Integer> entry : ram.entrySet()) {
            if (entry.getValue().equals(dado)) {
                indiceEncontrado = entry.getKey();
                break;
            }
        }
        if (indiceEncontrado == null) {
            return -1;
        }

        return (indiceEncontrado / TAMANHO_DO_BLOCO) * TAMANHO_DO_BLOCO;
    }

    public int[] getBloco(int dado){
        int inicioBloco = getIndiceBloco(dado);
        if(inicioBloco == -1){
            return null;
        }

        int[] bloco = new int[TAMANHO_DO_BLOCO];
        for (int i = 0; i < TAMANHO_DO_BLOCO; i++) {
            int linhaAtual = inicioBloco + i;
            bloco[i] = ram.get(linhaAtual);
        }
        return bloco;
    }

    public void updateBloco(int[] bloco, int indice) {
        for (int i = 0; i < TAMANHO_DO_BLOCO; i++) {
            setLinha(indice + i, bloco[i]);
        }
    }

    public void printMemoria() {
        for (Map.Entry<Integer, Integer> entry : ram.entrySet()) {
            System.out.println("Índice: " + entry.getKey() + ", " + entry.getValue());
        }
    }

}
