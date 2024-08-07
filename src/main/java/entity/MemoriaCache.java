package entity;
import java.util.*;

public class MemoriaCache {

    private static final int tamanhoCache = 5;
    private final Queue<Integer> filaEndereco;
    public final Map<Integer, blocoCache> cache;
    private final Random random;

    public enum tags{
        Exclusivo, Modificado, Compartilhado, Invalido
    }


    public blocoCache getBlocoCache(int blocoEndereco){
        return cache.get(blocoEndereco);
    }

    public static class blocoCache{
        private int[] dados;
        private tags tag;
        private int indiceRAM;

        blocoCache(int[] dados, tags tag,  int indiceRAM){
            this.dados = dados;
            this.tag = tag;
            this.indiceRAM = indiceRAM;
        }

        public int[] getDados() {
            return dados;
        }
        public tags getTag() {
            return tag;
        }
        public int getIndiceRAM() {
            return indiceRAM;
        }

        public void setDados(int[] dados) {
            this.dados = dados;
        }

        public void setTag(tags tag) {
            this.tag = tag;
        }

        public void setIndiceRAM(int indiceRAM) {
            this.indiceRAM = indiceRAM;
        }
    }

    public MemoriaCache(int tamanhoCache){
        filaEndereco = new LinkedList<Integer>();
        cache = new HashMap<Integer, blocoCache>();
        random = new Random();
    }

    public void setBloco(int[] bloco, tags tag, int indiceRAM) {
        //Mapeamento aleatorio
        if (cache.size() < tamanhoCache) {
            List<Integer> posicoesVazias = new ArrayList<>();
            for (int i = 0; i < tamanhoCache; i++) {
                if (!cache.containsKey(i)) {
                    posicoesVazias.add(i);
                }
            }
            if (!posicoesVazias.isEmpty()) {
                int posicaoAleatoria = posicoesVazias.get(random.nextInt(posicoesVazias.size()));
                filaEndereco.add(posicaoAleatoria);
                cache.put(posicaoAleatoria, new blocoCache(bloco, tags.Exclusivo, indiceRAM));
            }
        }
        //Substituição FIFO
        else {
            int enderecoAntigo = filaEndereco.remove();
            blocoCache blocoAntigo = cache.remove(enderecoAntigo);
            filaEndereco.add(enderecoAntigo);
            cache.put(enderecoAntigo, new blocoCache(bloco, tag, indiceRAM));
        }
    }

    public void printCache() {
        System.out.println("Conteúdo da Cache:");
        for (Map.Entry<Integer, blocoCache> entry : cache.entrySet()) {
            System.out.println("Posição: " + entry.getKey() + ", Dados: " + Arrays.toString(entry.getValue().dados) +
                    ", TAG: (" + entry.getValue().tag + "," + entry.getValue().indiceRAM + ")");
        }
    }

    public Integer procuraCache(int idReceita) {
        for (Map.Entry<Integer, blocoCache> entry : cache.entrySet()) {
            int[] dados = entry.getValue().dados;
            for (int dado : dados) {
                if (dado == idReceita) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }

    public void printPosicaoCache(int posicao) {
        blocoCache bloco = cache.get(posicao);
        System.out.println("Posição: " + posicao + ", Dados: " + Arrays.toString(bloco.dados) +
                ", TAG: (" + bloco.tag + "," + bloco.indiceRAM + ")");
    }

}
