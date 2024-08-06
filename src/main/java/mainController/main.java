package mainController;

import entity.MemoriaCache;
import entity.MemoriaCache.blocoCache;
import entity.Processador;
import entity.RAM;

import java.util.Random;

public class main {

    public void readHit(Processador p, int blocoEndereco, int idReceita){

        blocoCache bloco = p.getBlocoCache(blocoEndereco);
        //Funçao que printa resultado
            System.out.println(idReceita);
            System.out.println(bloco.tag);

        System.out.println(
                "A leitura foi um readHit não foi preciso fazer uma busca na memoria RAM e nem mudar a tag" +
                "do bloco da cache."
        );
    }

    public void readMiss(Processador p1, Processador p2, int idReceita){
        int enderecoBloco1 = p1.confereDadoCache(idReceita);
        int enderecoBloco2 = p2.confereDadoCache(idReceita);
        if(enderecoBloco1 != 0 && enderecoBloco2 == 0){
//
            System.out.println("ola");
        }
    }


    public static void main(String[] args) {

        RAM ram = new RAM();
        Random rand = new Random();

        for (int i = 0; i < 100; i++) {
            int valorAleatorio = rand.nextInt(1000);
            ram.setLinha(i, valorAleatorio);
        }

        System.out.println("Memória RAM:");
        ram.printMemoria();

        MemoriaCache.tags tag = MemoriaCache.tags.Exclusivo;

        Processador processador1 = new Processador(5);
        Processador processador2 = new Processador(5);
        Processador processador3 = new Processador(5);

        /*

        - Funcao para printar resultado levando em conta o hashMap contendo as receitas.
        - Funcao para printar o log falando sobre se foi um ReadHit, etc e a linha da cache que sofreu alteracao e o que ocorreu.


        print(Escolher um processador) - return: (1,2,3) - Escolhido: 1
        print(Deseja fazer uma leitura ou escrita? - return: (l,e)

        if l:
            print(Qual receita voce deseja ler? ) - return: int idReceita(dado)
            int blocoEndereco = processador1.confereDadoCache(int idReceita) - return: endereco do bloco da cache
            if bloco != null:
                ReadHit() -> {
                    receita = processador1.cache.getBloco(blocoEndereco)
//                  print(receita)
                }
            else:
                ReadMiss() -> {
                    if( processador2.buscarReceita(idReceita) == null && processador3.buscarReceita(idReceita) == null){
                        processador1.accessoRam(idReceita) -> {
                            blocoRAM = RAM.getBloco(idReceita)
                            cache.setBloco(bloco, tag)
                            receita = processador1.cache.getBloco(bloco, idReceita)
                            return print(receita)
                        }
                    }
                    else if(processador2.buscarReceita(idReceita) != null && processador3.buscarReceita(idReceita) == null){
                        bloco = processador2.buscarReceita(idReceita)

                        if(bloco.tag == exclusivo){
                            //redefinir tag do bloco do processador 1 e 2 de exlusivo para compartilhado.
                            //Colocar bloco no processador1 por meio da funcao setBloco(bloco, bloco.tag).
                            //Chamar funcao para printar resultado e logs.
                            //use return nesse if
                        }
                        //tag modificado
                        //Dar um slice no bloco e pegar apenas os dados
                        processador2.writeBack(dadosBloco, indice)
                        //redefenir tag do bloco do processador 1 e 2 para compartilhado
                        //chamar funcao para printar resultado e log

                    }
                    else if(processador2.buscarReceita(idReceita) == null && processador3.buscarReceita(idReceita) != null){
                        bloco = processador3.buscarReceita(idReceita)

//                        if(bloco.tag == exclusivo){
//                            //redefinir tag do bloco do processador 1 e 3 de exlusivo para compartilhado.
//                            //Colocar bloco no processador1 por meio da funcao setBloco(bloco, bloco.tag).
//                            //Chamar funcao para printar resultado e logs.
//                            //use return nesse if
                        }
                        //tag modificado

                        //Colocar bloco no processador1 por meio da funcao set Bloco(bloco, bloc.tag)
                        //Dar um slice no bloco e pegar apenas os dados
                        processador3.writeBack(dadosBloco, indice)
                        //redefenir tag do bloco do processador 1 e 3 para compartilhado
                        //return chamar funcao para printar resultado e log
                    }

                    bloco = processador3.buscarReceita(idReceita)
                    // colocar bloco da memoria 2 ou 3 na memoria cache do processador 1

                }

        */
    }
}
