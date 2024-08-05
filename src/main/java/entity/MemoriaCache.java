package entity;
import java.util.*;

public class MemoriaCache {

    private final int tamanhoCache;
    private final Queue<Integer> filaEndereco;
    private final Map<Integer, blocoCache> cache;
    private final Random random;

    public enum tags{
        Exclusivo, Modificado, Compartilhado, Invalido;
    }


    public blocoCache getBlocoCache(int idReceita){
        return cache.get(idReceita);
    }

    private class blocoCache{
        int[] dados;
         tags tag;

        blocoCache(int[] dados){
            this.dados = dados;
            this.tag = tags.Exclusivo;
        }
    }

    public MemoriaCache(int tamanhoCache){
        this.tamanhoCache = tamanhoCache;
        filaEndereco = new LinkedList<Integer>();
        cache = new HashMap<Integer, blocoCache>();
        random = new Random();
    }

    public void setBloco(int[] bloco, tags tag) {
        //Mapeamento aleatoriamente
        if (cache.size() < tamanhoCache) {
            int posicaoAleatoria = random.nextInt(tamanhoCache);
            filaEndereco.add(posicaoAleatoria);
            cache.put(posicaoAleatoria, new blocoCache(bloco));

        }
        //Substituição FIFO
        else {
            int enderecoAntigo = filaEndereco.remove();
            blocoCache blocoAntigo = cache.remove(enderecoAntigo);
            filaEndereco.add(enderecoAntigo);
            cache.put(enderecoAntigo, new blocoCache(bloco));
        }


    }


}
