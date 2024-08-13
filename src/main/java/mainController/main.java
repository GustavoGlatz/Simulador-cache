package mainController;

import entity.MemoriaCache;
import entity.MemoriaCache.blocoCache;
import entity.Processador;
import entity.RAM;

import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class main {


    public static void readHit(Processador p, int idReceita){

        int indiceBloco = p.confereDadoCache(idReceita);

        System.out.println(
                "\nA leitura foi um readHit não foi preciso fazer uma busca na memoria RAM e nem mudar a tag " +
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
            //retornar bloco que contém o id da receita procurada pelo usuário
            System.out.println("\nA leitura foi um readMiss e não estava em nenhuma das outras caches, então foi " +
                    "necessário um acesso à memoria principal e a tag do bloco da cache é exclusiva.");
            p1.getMemoriaCache().printCache();
        }
        else if (enderecoBloco2 != null && enderecoBloco3 == null){
            readMissOneCopy(ram, p1, p2, enderecoBloco2, 2);
        }
        else if (enderecoBloco2 == null) {
            readMissOneCopy(ram, p1, p3, enderecoBloco3, 3);
        }
        else{
            blocoCache blocoCachep2 = p2.getBlocoCache(enderecoBloco2);
            blocoCache blocoCachep3 = p3.getBlocoCache(enderecoBloco3);

            if(blocoCachep2.getTag() == MemoriaCache.tags.Compartilhado && blocoCachep3.getTag() == MemoriaCache.tags.Compartilhado){
                p1.getMemoriaCache().setBloco(blocoCachep2.getDados(), MemoriaCache.tags.Compartilhado, blocoCachep2.getIndiceRAM());

                System.out.println("\nA leitura foi um readMiss, o dado estava nas duas outras caches e ambas com a tag compartilhado, " +
                        "então foi acessado uma das duas caches e a tag do bloco da cache é compartilhado.");
                p1.getMemoriaCache().printCache();
                p2.getMemoriaCache().printCache();
            }
            else if(blocoCachep2.getTag() == MemoriaCache.tags.Exclusivo && blocoCachep3.getTag() == MemoriaCache.tags.Compartilhado){
                blocoCachep2.setTag(MemoriaCache.tags.Compartilhado);
                p1.getMemoriaCache().setBloco(blocoCachep2.getDados(), MemoriaCache.tags.Compartilhado, blocoCachep2.getIndiceRAM());

                System.out.println("\nA leitura foi um readMiss, o dado estava nas duas outras caches uma com a tag compartilhado e " +
                        "a outra com a tag exclusivo, então foi acessado a cache com a tag do bloco exclusivo e ambas as tags agora são compartilhado.");
                p1.getMemoriaCache().printCache();
                p2.getMemoriaCache().printCache();
            }
            else if(blocoCachep3.getTag() == MemoriaCache.tags.Exclusivo && blocoCachep2.getTag() == MemoriaCache.tags.Compartilhado){
                blocoCachep3.setTag(MemoriaCache.tags.Compartilhado);
                p1.getMemoriaCache().setBloco(blocoCachep3.getDados(), MemoriaCache.tags.Compartilhado, blocoCachep3.getIndiceRAM());

                System.out.println("\nA leitura foi um readMiss, o dado estava nas duas outras caches uma com a tag compartilhado e " +
                        "a outra com a tag exclusivo, então foi acessado a cache com a tag do bloco exclusivo e ambas as tags agora são compartilhado.");
                p1.getMemoriaCache().printCache();
                p3.getMemoriaCache().printCache();
            }
        }
    }

    private static void readMissOneCopy(RAM ram, Processador p1, Processador p3, Integer enderecoBloco3, Integer processdaorUsado) {
        blocoCache blocoCachep3 = p3.getBlocoCache(enderecoBloco3);

        if (blocoCachep3.getTag() == MemoriaCache.tags.Exclusivo) {

            blocoCachep3.setTag(MemoriaCache.tags.Compartilhado);
            //Colocar bloco no processador1 por meio da funcao setBloco(bloco, bloco.tag).
            p1.getMemoriaCache().setBloco(blocoCachep3.getDados(), MemoriaCache.tags.Compartilhado, blocoCachep3.getIndiceRAM());
            //Chamar funcao para printar resultado e logs.
            System.out.println("\nA leitura foi um readMiss e o dado estava em uma das outras caches, então foi " +
                    "necessário um acesso à cache do processador "+ processdaorUsado +" e a tag do bloco da cache é compartilhada.");
            p1.getMemoriaCache().printCache();
        }
        else if (blocoCachep3.getTag() == MemoriaCache.tags.Modificado) {
            ram.updateBloco(blocoCachep3.getDados(), enderecoBloco3);
            blocoCachep3.setTag(MemoriaCache.tags.Compartilhado);
            p1.getMemoriaCache().setBloco(blocoCachep3.getDados(), MemoriaCache.tags.Compartilhado, blocoCachep3.getIndiceRAM());
            System.out.println("\nA leitura foi um readMiss e o dado estava em uma das outras caches, então foi " +
                    "necessário um acesso à cache do processador "+ processdaorUsado +" e a tag do bloco da cache é compartilhada.");
            p1.getMemoriaCache().printCache();
        }
    }

    public void writeMiss(){

    }

    public static void writeHit(int idReceitaAlterado, int idReceita, int enderecoBloco1 , RAM ram, Processador p1, Processador p2, Processador p3){
        blocoCache blocoCachep1 = p1.getMemoriaCache().getBlocoCache(enderecoBloco1);

        if(blocoCachep1.getTag() == MemoriaCache.tags.Modificado){
            blocoCachep1.getDados()[p1.buscaReceitaNoBloco(blocoCachep1, idReceita)] = idReceitaAlterado;
            blocoCachep1.setTag(MemoriaCache.tags.Modificado);
            p1.getMemoriaCache().atualizaBloco(blocoCachep1, enderecoBloco1);
            System.out.println("\nA escrita foi um writeHit e o bloco da cache estava com a tag modificado, " +
                    "portanto mantém a mesma tag");
            p1.getMemoriaCache().printCache();
        }
        else if(blocoCachep1.getTag() == MemoriaCache.tags.Compartilhado){
            Integer enderecoBloco2 = p2.confereDadoCache(idReceita);
            Integer enderecoBloco3 = p3.confereDadoCache(idReceita);

            if (enderecoBloco2 != null && enderecoBloco3 != null){
                blocoCache blocoCachep2 = p2.getMemoriaCache().getBlocoCache(enderecoBloco2);
                blocoCache blocoCachep3 = p3.getMemoriaCache().getBlocoCache(enderecoBloco3);

                blocoCachep1.getDados()[p1.buscaReceitaNoBloco(blocoCachep1, idReceita)] = idReceitaAlterado;
                blocoCachep1.setTag(MemoriaCache.tags.Modificado);
                p1.getMemoriaCache().atualizaBloco(blocoCachep1, enderecoBloco1);

                blocoCachep2.setTag(MemoriaCache.tags.Invalido);
                p2.getMemoriaCache().atualizaBloco(blocoCachep2, enderecoBloco2);

                blocoCachep3.setTag(MemoriaCache.tags.Invalido);
                p3.getMemoriaCache().atualizaBloco(blocoCachep3, enderecoBloco3);

                System.out.println("\nA escrita foi um writeHit, o bloco da cache estava com a tag compartilhado " +
                        "e foi encontrado nas outras duas caches");
                p1.getMemoriaCache().printCache();
                p2.getMemoriaCache().printCache();
                p3.getMemoriaCache().printCache();
            }
            else if (enderecoBloco2 == null) {
                blocoCache blocoCachep3 = p3.getMemoriaCache().getBlocoCache(enderecoBloco3);

                blocoCachep1.getDados()[p1.buscaReceitaNoBloco(blocoCachep1, idReceita)] = idReceitaAlterado;
                blocoCachep1.setTag(MemoriaCache.tags.Modificado);
                p1.getMemoriaCache().atualizaBloco(blocoCachep1, enderecoBloco1);

                blocoCachep3.setTag(MemoriaCache.tags.Invalido);
                p3.getMemoriaCache().atualizaBloco(blocoCachep3, enderecoBloco3);

                System.out.println("\nA escrita foi um writeHit, o bloco da cache estava com a tag compartilhado " +
                        "e foi encontrado em outra cache");
                p1.getMemoriaCache().printCache();
                p3.getMemoriaCache().printCache();
            }
            else if (enderecoBloco3 == null) {
                blocoCache blocoCachep2 = p2.getMemoriaCache().getBlocoCache(enderecoBloco2);

                blocoCachep1.getDados()[p1.buscaReceitaNoBloco(blocoCachep1, idReceita)] = idReceitaAlterado;
                blocoCachep1.setTag(MemoriaCache.tags.Modificado);
                p1.getMemoriaCache().atualizaBloco(blocoCachep1, enderecoBloco1);

                blocoCachep2.setTag(MemoriaCache.tags.Invalido);
                p2.getMemoriaCache().atualizaBloco(blocoCachep2, enderecoBloco2);

                System.out.println("A escrita foi um writeHit, o bloco da cache estava com a tag compartilhado " +
                        "e foi encontrado em outra cache");
                p1.getMemoriaCache().printCache();
                p2.getMemoriaCache().printCache();
            }
        }
        else if(blocoCachep1.getTag() == MemoriaCache.tags.Exclusivo){
            blocoCachep1.getDados()[p1.buscaReceitaNoBloco(blocoCachep1, idReceita)] = idReceitaAlterado;
            blocoCachep1.setTag(MemoriaCache.tags.Modificado);
            p1.getMemoriaCache().atualizaBloco(blocoCachep1, enderecoBloco1);

            System.out.println("\nA escrita foi um writeHit e o bloco da cache estava com a tag exclusivo " +
                    "portanto a tag foi alterada para modificado");
            p1.getMemoriaCache().printCache();
        }
    }

    public static void main(String[] args) {

        RAM ram = new RAM();
        Scanner ler = new Scanner(System.in);

        for (int i = 0; i < 100; i++) {
            ram.setLinha(i, i);
        }
        //ram.setLinha(99, 333);

        System.out.println("\nConteudo memória RAM:");
        ram.printMemoria();

        Processador processador1 = new Processador(5);
        Processador processador2 = new Processador(5);
        Processador processador3 = new Processador(5);


        //TESTE
//        for (int i = 0; i < 7; i++) {
//            int[] bloco1 = {i * 10, i * 10 + 1, i * 10 + 2, i * 10 + 3, i * 10 + 4};
//            int[] bloco2 = {i * 3, i * 3 + 1, i * 3 + 2, i * 3 + 3, i * 3 + 4};
//            int[] bloco3 = {i * 7, i * 7 + 1, i * 7 + 2, i * 7 + 3, i * 7 + 4};
//            processador1.getMemoriaCache().setBloco(bloco1, tag, i);
//            processador2.getMemoriaCache().setBloco(bloco2, tag, i);
//            processador3.getMemoriaCache().setBloco(bloco3, tag, i);
//        }
//        int[] bloco = {1,2,3,4,5};
//        processador1.getMemoriaCache().setBloco(bloco, MemoriaCache.tags.Exclusivo, 0);
//        int[] bloco2 = {1,2,3,4,2};
//        processador2.getMemoriaCache().setBloco(bloco2, MemoriaCache.tags.Compartilhado, 0);
//        int[] bloco3 = {1,2,3,4,3};
//        processador3.getMemoriaCache().setBloco(bloco3, MemoriaCache.tags.Exclusivo, 0);

        //REAL
        for (int i = 0; i < 4; i++) {
            int[] bloco = {0, 0, 0, 0, 0};
            processador1.getMemoriaCache().setBloco(bloco, MemoriaCache.tags.Invalido, -1);
            processador2.getMemoriaCache().setBloco(bloco, MemoriaCache.tags.Invalido, -1);
            processador3.getMemoriaCache().setBloco(bloco, MemoriaCache.tags.Invalido, -1);
        }


        processador1.getMemoriaCache().printCache();
        processador2.getMemoriaCache().printCache();
        processador3.getMemoriaCache().printCache();


        int i = 0;
        while (i != -1) {
            System.out.println("\nEscolha um processador: (1/2/3)");
            int processadorEscolhido = ler.nextInt();
            if(processadorEscolhido != 1 && processadorEscolhido != 2 && processadorEscolhido != 3){
                System.out.println("Processador não identificado!");
                break;
            }

            System.out.println("Deseja fazer uma leitura ou escrita? (l/e)");
            String modoEscolhido = ler.next();
            if(!(modoEscolhido.equals("l") || modoEscolhido.equals("e"))){
                System.out.println("Modo de operação não identificado!");
                break;
            }

            System.out.println("Qual dado deseja ler? ");
            int idReceita = ler.nextInt();
            if(ram.getLinha(idReceita) == null ) {
                System.out.println("O dado requisitado não existe na memoria RAM!");
                break;
            }

            switch (processadorEscolhido){
                case 1:
                    Integer enderecoBloco1 = processador1.confereDadoCache(idReceita);
                    if(modoEscolhido.equals("l")){
                        if(enderecoBloco1 != null){
                            readHit(processador1, idReceita);
                        }
                        readMiss(ram, processador1, processador2, processador3, idReceita);
                    }
                    else {
                        System.out.println("O que deseja escrever/alterar? ");
                        int dado = Integer.parseInt(ler.next());
                        if(enderecoBloco1 != null){
                            writeHit(dado, idReceita, enderecoBloco1, ram, processador1, processador2, processador3);
                        }
                        //writeMiss();
                    }
                    break;
                case 2:
                    Integer enderecoBloco2 = processador2.confereDadoCache(idReceita);
                    if(modoEscolhido.equals("l")){
                        if(enderecoBloco2 != null){
                            readHit(processador2, idReceita);
                        }
                        readMiss(ram, processador2, processador1, processador3, idReceita);
                    }
                    else {
                        System.out.println("O que deseja escrever/alterar? ");
                        int dado = Integer.parseInt(ler.next());
                        if(enderecoBloco2 != null){
                            writeHit(dado, idReceita, enderecoBloco2, ram, processador2, processador1, processador3);
                        }
                        //writeMiss();
                    }
                    break;
                case 3:
                    Integer enderecoBloco3 = processador3.confereDadoCache(idReceita);
                    if(modoEscolhido.equals("l")){
                        if(enderecoBloco3 != null){
                            readHit(processador3, idReceita);
                        }
                        readMiss(ram, processador3, processador2, processador1, idReceita);
                    }
                    else {
                        System.out.println("O que deseja escrever/alterar? ");
                        int dado = Integer.parseInt(ler.next());
                        if(enderecoBloco3 != null){
                            writeHit(dado, idReceita, enderecoBloco3, ram, processador3, processador2, processador1);
                        }
                        //writeMiss();
                    }
                    break;
                default:
                    System.out.println("O número inserido é inválido! Digite um número entre 1 e 3");
            }

            System.out.println("\nDeseja continuar? (sim:1/não:-1)");
            i = ler.nextInt();
            System.out.println("\n");
        }


//        processador1.getMemoriaCache().printCache();
//        processador2.getMemoriaCache().printCache();
//        processador3.getMemoriaCache().printCache();

//        ram.printMemoria();



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
