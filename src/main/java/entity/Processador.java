package entity;


public class Processador {

    private final MemoriaCache memoriaCache;

    public Processador(int tamanhoCache) {
        this.memoriaCache = new MemoriaCache(tamanhoCache);
    }

    // Dados da memória RAM sem tag
    public int[] acessarRam(RAM ram){
        return null;
    }

    // Busca na memória cacahe e retorna o endereco da linha da cache que contém o bloco da receita
    public int confereDadoCache(int idReceita){
        return 0;
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

    public void redefinirTagBloc(int enderecoBloco, MemoriaCache.tags tag){

    }
}

