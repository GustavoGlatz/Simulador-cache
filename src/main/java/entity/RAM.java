package entity;
import java.util.*;

public class RAM {

    private class LinhaRAM {
        private int linha;
        private int dado;

        public LinhaRAM(int linha, int dado) {
            this.linha = linha;
            this.dado = dado;
        }

        public int getIndice() {
            return linha;
        }

        public int getDado() {
            return dado;
        }

        public void setDado(int dado) {
            this.dado = dado;
        }

        public String toString() {
            return "LinhaRAM{" +
                    "linha=" + linha +
                    ", dado=" + dado +
                    '}';
        }
    }

    private Map<Integer, LinhaRAM> ram;
    private static final int TAMANHO_DO_BLOCO = 5;

    public RAM() {
        ram = new HashMap<>();
    }

    public void setLinha(int indice, int dado) {
        ram.put(indice, new LinhaRAM(indice, dado));
    }

    public LinhaRAM getLinha(int indice) {
        return ram.get(indice);
    }

    public LinhaRAM[] getBloco(int inicio) {
        LinhaRAM[] bloco = new LinhaRAM[TAMANHO_DO_BLOCO];
        for (int i = 0; i < TAMANHO_DO_BLOCO; i++) {
            int linhaAtual = inicio + i;
            bloco[i] = ram.get(linhaAtual);
        }
        return bloco;
    }

    public void updateBloco(LinhaRAM[] bloco) {
        for (LinhaRAM linhaRAM : bloco) {
            setLinha(linhaRAM.getIndice(), linhaRAM.getDado());
        }
    }

    public void printMemoria() {
        for (Map.Entry<Integer, LinhaRAM> entry : ram.entrySet()) {
            System.out.println(entry.getValue());
        }
    }

}
