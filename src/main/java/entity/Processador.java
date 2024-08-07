package entity;


import java.util.Map;

public class Processador {

    private final MemoriaCache memoriaCache;

    public Processador(int tamanhoCache) {
        this.memoriaCache = new MemoriaCache(tamanhoCache);
    }

    // Dados da memória RAM sem tag
    public int[] acessarRam(RAM ram){
        return null;
    }

    // Busca na memória cache e retorna o endereco da linha da cache que contém o bloco da receita
    public Integer confereDadoCache(int idReceita){
        for (Map.Entry<Integer, MemoriaCache.blocoCache> entry : memoriaCache.cache.entrySet()) {
            int[] dados = entry.getValue().getDados();
            for (int dado : dados) {
                if (dado == idReceita) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }

    public MemoriaCache.blocoCache getBlocoCache(int blocoEndereco){
        return memoriaCache.getBlocoCache(blocoEndereco);
    }

    public int buscaReceitaNoBloco(MemoriaCache.blocoCache bloco, int idReceita){
        for(int i = 0; i < bloco.getDados().length; i++){
            if(bloco.getDados()[i] == idReceita){
                return i;
            }
        }
        return 0;
    }

//    public void setBlocoCache( bloco, MemoriaCache.tags tag, int indiceRAM){

//    }

    public void setBlocoCache(int[] bloco, MemoriaCache.tags tag, int indiceBloco){
        if(bloco == null){
            System.out.println("IdReceita não existe");
        }

        memoriaCache.setBloco(bloco, tag, indiceBloco);
    }

    public void redefinirTagBloc(int enderecoBloco, MemoriaCache.tags tag){

    }

    public void writeBack(int[] bloco, int indiceRam){

    }

    public MemoriaCache getMemoriaCache() {
        return memoriaCache;
    }

}

