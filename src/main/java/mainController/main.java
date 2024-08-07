package mainController;

import entity.MemoriaCache;
import entity.MemoriaCache.blocoCache;
import entity.Processador;
import entity.RAM;

import java.util.Random;
import java.util.Scanner;

public class main {

    public static void readHit(Processador p, int idReceita){

        int indiceBloco = p.confereDadoCache(idReceita);

        System.out.println(
                "A leitura foi um readHit não foi preciso fazer uma busca na memoria RAM e nem mudar a tag " +
                "do bloco da cache."
        );
        p.getMemoriaCache().printPosicaoCache(indiceBloco);

    }

    public static void readMiss(RAM ram, Processador p1, Processador p2, Processador p3, int idReceita){
        Integer enderecoBloco2 = p2.confereDadoCache(idReceita);
        Integer enderecoBloco3 = p3.confereDadoCache(idReceita);

        if(enderecoBloco2 == null && enderecoBloco3 == null){
            //Não está presente em nenhuma das duas outras caches
            int inicioBlocoRAM = ram.getIndiceBloco(idReceita);
            int[] blocoRAM = ram.getBloco(idReceita);
            p1.setBlocoCache(blocoRAM, MemoriaCache.tags.Exclusivo, inicioBlocoRAM);
            //retornar bloco que contém o a receita procurada pelo usuário
            System.out.println("A leitura foi um readMiss e não estava em nenhuma das outras caches, então foi " +
                    "necessário um acesso à memoria principal e a tag do bloco da cache é exclusiva.");
            p1.getMemoriaCache().printCache();
        }
        else if (enderecoBloco2 != null && enderecoBloco3 == null){
            blocoCache blocoCachep2 = p2.getBlocoCache(enderecoBloco2);

            if(blocoCachep2.getTag() == MemoriaCache.tags.Exclusivo){

                blocoCachep2.setTag(MemoriaCache.tags.Compartilhado);
                //Colocar bloco no processador1 por meio da funcao setBloco(bloco, bloco.tag).
                p1.getMemoriaCache().setBloco(blocoCachep2.getDados(), MemoriaCache.tags.Compartilhado, blocoCachep2.getIndiceRAM());
                //Chamar funcao para printar resultado e logs.
                System.out.println("A leitura foi um readMiss e o dado estava em uma das outras caches, então foi " +
                        "necessário um acesso à cache do processador 2 e a tag do bloco da cache é compartilhada.");
                p1.getMemoriaCache().printCache();
            }
            else if(blocoCachep2.getTag() == MemoriaCache.tags.Modificado){
                ram.updateBloco(blocoCachep2.getDados(), enderecoBloco2);
                blocoCachep2.setTag(MemoriaCache.tags.Compartilhado);
                p1.getMemoriaCache().setBloco(blocoCachep2.getDados(), MemoriaCache.tags.Compartilhado, blocoCachep2.getIndiceRAM());
                System.out.println("A leitura foi um readMiss e o dado estava em uma das outras caches, então foi " +
                        "necessário um acesso à cache do processador 2 e a tag do bloco da cache é compartilhada.");
                p1.getMemoriaCache().printCache();
            }
        }
        else if (enderecoBloco3 != null && enderecoBloco2 == null) {
            blocoCache blocoCachep3 = p3.getBlocoCache(enderecoBloco3);

            if (blocoCachep3.getTag() == MemoriaCache.tags.Exclusivo) {

                blocoCachep3.setTag(MemoriaCache.tags.Compartilhado);
                //Colocar bloco no processador1 por meio da funcao setBloco(bloco, bloco.tag).
                p1.getMemoriaCache().setBloco(blocoCachep3.getDados(), MemoriaCache.tags.Compartilhado, blocoCachep3.getIndiceRAM());
                //Chamar funcao para printar resultado e logs.
                System.out.println("A leitura foi um readMiss e o dado estava em uma das outras caches, então foi " +
                        "necessário um acesso à cache do processador 2 e a tag do bloco da cache é compartilhada.");
                p1.getMemoriaCache().printCache();
            }
            else if (blocoCachep3.getTag() == MemoriaCache.tags.Modificado) {
                ram.updateBloco(blocoCachep3.getDados(), enderecoBloco3);
                blocoCachep3.setTag(MemoriaCache.tags.Compartilhado);
                p1.getMemoriaCache().setBloco(blocoCachep3.getDados(), MemoriaCache.tags.Compartilhado, blocoCachep3.getIndiceRAM());
                System.out.println("A leitura foi um readMiss e o dado estava em uma das outras caches, então foi " +
                        "necessário um acesso à cache do processador 2 e a tag do bloco da cache é compartilhada.");
                p1.getMemoriaCache().printCache();
            }
        }
//        else{
//            blocoCache blocoCachep2 = p2.getBlocoCache(enderecoBloco2);
//            blocoCache blocoCachep3 = p3.getBlocoCache(enderecoBloco3);
//            if(blocoCachep2.getTag() == MemoriaCache.tags.Compartilhado && blocoCachep3.getTag() == MemoriaCache.tags.Compartilhado){
//
//            }
//            else if(blocoCachep2.getTag() == MemoriaCache.tags.Exclusivo && blocoCachep3.getTag() == MemoriaCache.tags.Compartilhado){
//
//            }
//            else if(blocoCachep3.getTag() == MemoriaCache.tags.Exclusivo && blocoCachep2.getTag() == MemoriaCache.tags.Compartilhado){
//
//            }
//        }
    }

    public void writeMiss(){

    }

    public void writeHit(){

    }

    public static void main(String[] args) {

        RAM ram = new RAM();
        Random rand = new Random();
        Scanner ler = new Scanner(System.in);

        for (int i = 0; i < 99; i++) {
            int valorAleatorio = rand.nextInt(1000);
            ram.setLinha(i, valorAleatorio);
        }
        ram.setLinha(99, 333);

        System.out.println("Memória RAM:");
        ram.printMemoria();

        MemoriaCache.tags tag = MemoriaCache.tags.Exclusivo;

        Processador processador1 = new Processador(5);
        Processador processador2 = new Processador(5);
        Processador processador3 = new Processador(5);

        System.out.println("Escolha um processador: (1/2/3)");
        int processadorEscolhido = ler.nextInt();
        System.out.println("Deseja fazer uma leitura ou escrita? (l/e)");
        String modoEscolhido = ler.next();
        System.out.println("Qual dado deseja ler? ");
        int idReceita = ler.nextInt();

        for (int i = 0; i < 7; i++) {
            int[] bloco1 = {i * 10, i * 10 + 1, i * 10 + 2, i * 10 + 3, i * 10 + 4};
            int[] bloco2 = {i * 3, i * 3 + 1, i * 3 + 2, i * 3 + 3, i * 3 + 4};
            processador1.getMemoriaCache().setBloco(bloco1, tag, i);
            processador2.getMemoriaCache().setBloco(bloco2, tag, i);
        }
        int[] bloco = {1,2,3,4,5};
        processador2.getMemoriaCache().setBloco(bloco, MemoriaCache.tags.Modificado, 0);


        switch (processadorEscolhido){
            case 1:
                if(modoEscolhido.equals("l")){
                    if(processador1.confereDadoCache(idReceita) != null){
                        readHit(processador1, idReceita);
                    }
                    readMiss(ram, processador1, processador2, processador3, idReceita);
                }
        }

        //processador1.getMemoriaCache().printCache();
        processador2.getMemoriaCache().printCache();
        //processador3.getMemoriaCache().printCache();

        ram.printMemoria();



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
