package mainController;

import entity.MemoriaCache;
import entity.MemoriaCache.blocoCache;
import entity.Processador;
import entity.RAM;

import java.util.Scanner;

public class main {


    //Coloca um bloco da ram num bloco da cache a partir de um dado da ram
    private static void colocaRamCacheComRam(RAM ram, Processador p1, int idReceita) {
        int inicioBlocoRAM = ram.getIndiceBloco(idReceita);
        int[] blocoRAM = ram.getBloco(inicioBlocoRAM);
        p1.setBlocoCache(blocoRAM, MemoriaCache.tags.Exclusivo, inicioBlocoRAM);
    }

    //Coloca um bloco da ram num bloco da cache a partir de um bloco da cache
    private static void colocaRamCacheComCache(RAM ram, Processador p1, blocoCache blocoCache1, Integer enderecoBloco1) {
        int[] blocoRAM = ram.getBloco(blocoCache1.getIndiceRAM());
        blocoCache1.setDados(blocoRAM);
        blocoCache1.setTag(MemoriaCache.tags.Exclusivo);
        p1.getMemoriaCache().atualizaBloco(blocoCache1, enderecoBloco1);
    }

    //Edita a cache no bloco do dado a ser modificado com o dado a ser escrito
    private static void editaCacheHit(int idReceitaAlterado, int idReceita, int enderecoBloco1, Processador p1, blocoCache blocoCachep1) {
        blocoCachep1.getDados()[p1.buscaReceitaNoBloco(blocoCachep1, idReceita)] = idReceitaAlterado;
        blocoCachep1.setTag(MemoriaCache.tags.Modificado);
        p1.getMemoriaCache().atualizaBloco(blocoCachep1, enderecoBloco1);
    }

    //Edita a cache no bloco do dado a ser modificado com o dado a ser escrito
    private static void editaCacheMiss(Processador p1, int idReceita, int idReceitaAlterado) {
        Integer enderecoBloco1 = p1.confereDadoCache(idReceita);
        blocoCache blocoCachep1 = p1.getMemoriaCache().getBlocoCache(enderecoBloco1);
        editaCacheHit(idReceitaAlterado, idReceita, enderecoBloco1, p1, blocoCachep1);
    }

    private static void atualizaBlocoCache(Processador p1, blocoCache blocoCache1, Integer enderecoBloco1, blocoCache blocoCachep3) {
        blocoCachep3.setTag(MemoriaCache.tags.Compartilhado);

        blocoCache1.setDados(blocoCachep3.getDados());
        blocoCache1.setTag(MemoriaCache.tags.Compartilhado);
        p1.getMemoriaCache().atualizaBloco(blocoCache1, enderecoBloco1);
    }

    public static void readHit(Processador p, int idReceita){

        int indiceBloco = p.confereDadoCache(idReceita);

        System.out.println(
                "\nA leitura foi um readHit não foi preciso fazer uma busca na memoria RAM e nem mudar a tag " +
                "do bloco da cache do processador escolhido.\n"
        );
        p.getMemoriaCache().printPosicaoCache(indiceBloco);
        System.out.println("\nMemória cache acessada: ");
        p.getMemoriaCache().printCache();

    }

    public static  void readMissInvalido(RAM ram, Processador p1, Processador p2, Processador p3, int idReceita, blocoCache blocoCache1, Integer enderecoBloco1){
        Integer enderecoBloco2 = p2.confereDadoCache(idReceita);
        Integer enderecoBloco3 = p3.confereDadoCache(idReceita);

        if(enderecoBloco2 == null && enderecoBloco3 == null){
            colocaRamCacheComCache(ram, p1, blocoCache1, enderecoBloco1);

            System.out.println("\nA leitura foi um readMiss pois a tag da cache acessada é invalido e não estava em nenhuma outra cache, então " +
                    "foi necessário um acesso a memoria principal e a tag do bloco da cache do processador escolhido é exclusiva");
            System.out.println("\nMemória cache acessada: ");
            p1.getMemoriaCache().printCache();
        }
        else if(enderecoBloco2 != null && enderecoBloco3 == null){
            blocoCache blocoCachep2 = p2.getBlocoCache(enderecoBloco2);

            readMissInvalidoOneCopy(ram, p1, p2, blocoCache1, enderecoBloco1, blocoCachep2);
        }
        else if(enderecoBloco2 == null && enderecoBloco3 != null){
            blocoCache blocoCachep3 = p3.getBlocoCache(enderecoBloco3);

            readMissInvalidoOneCopy(ram, p1, p3, blocoCache1, enderecoBloco1, blocoCachep3);
        }
        else{
            blocoCache blocoCachep2 = p2.getBlocoCache(enderecoBloco2);
            blocoCache blocoCachep3 = p3.getBlocoCache(enderecoBloco3);

            if(blocoCachep2.getTag() == MemoriaCache.tags.Compartilhado && blocoCachep3.getTag() == MemoriaCache.tags.Compartilhado){
                atualizaBlocoCache(p1, blocoCache1, enderecoBloco1, blocoCachep2);

                System.out.println("\nA leitura foi um readMiss, pois a tag da cache acessada é invalido, o dado estava nas duas outras caches e ambas com a tag compartilhado, " +
                        "então foi requisitado uma das duas caches e a tag do bloco da cache do processador escolhido é compartilhado.");
                System.out.println("\nMemória cache acessada: ");
                p1.getMemoriaCache().printCache();
                System.out.println("\nMemória cache requisitada: ");
                p2.getMemoriaCache().printCache();
            }
            else if(blocoCachep2.getTag() == MemoriaCache.tags.Exclusivo && blocoCachep3.getTag() == MemoriaCache.tags.Compartilhado){
                atualizaBlocoCache(p1, blocoCache1, enderecoBloco1, blocoCachep2);

                System.out.println("\nA leitura foi um readMiss, pois a tag da cache acessada é invalido, o dado estava nas duas outras caches uma com a tag compartilhado e " +
                        "a outra com a tag exclusivo, então foi requisitado a cache com a tag do bloco exclusivo e ambas as tags agora são compartilhado.");
                System.out.println("\nMemória cache acessada: ");
                p1.getMemoriaCache().printCache();
                System.out.println("\nMemória cache requisitada: ");
                p2.getMemoriaCache().printCache();
            }
            else if(blocoCachep3.getTag() == MemoriaCache.tags.Exclusivo && blocoCachep2.getTag() == MemoriaCache.tags.Compartilhado){
                atualizaBlocoCache(p1, blocoCache1, enderecoBloco1, blocoCachep3);

                System.out.println("\nA leitura foi um readMiss, pois a tag da cache acessada é invalido, o dado estava nas duas outras caches uma com a tag compartilhado e " +
                        "a outra com a tag exclusivo, então foi requisitado a cache com a tag do bloco exclusivo e ambas as tags agora são compartilhado.");
                System.out.println("\nMemória cache acessada: ");
                p1.getMemoriaCache().printCache();
                System.out.println("\nMemória cache requisitada: ");
                p3.getMemoriaCache().printCache();
            }
        }

    }

    private static void readMissInvalidoOneCopy(RAM ram, Processador p1, Processador p3, blocoCache blocoCache1, Integer enderecoBloco1, blocoCache blocoCachep2) {
        if (blocoCachep2.getTag() == MemoriaCache.tags.Exclusivo) {
            atualizaBlocoCache(p1, blocoCache1, enderecoBloco1, blocoCachep2);

            System.out.println("\nA leitura foi um readMiss, pois a tag da cache acessada é invalido, o dado estava em uma das outras caches e a tag era exclusivo, então " +
                    "essa cache foi requisitada e ambas as tags são compartilhado.");
            System.out.println("\nMemória cache acessada: ");
            p1.getMemoriaCache().printCache();
            System.out.println("\nMemória cache requisitada: ");
            p3.getMemoriaCache().printCache();
        }
        else if (blocoCachep2.getTag() == MemoriaCache.tags.Modificado) {
            ram.updateBloco(blocoCachep2.getDados(), blocoCachep2.getIndiceRAM());

            atualizaBlocoCache(p1, blocoCache1, enderecoBloco1, blocoCachep2);

            System.out.println("\nA leitura foi um readMiss, pois a tag da cache acessada é invalido, o dado estava em uma das outras caches e a tag era modificado, então " +
                    "houve writeBack, essa cache foi requisitada e ambas as tags são compartilhado.");
            System.out.println("\nMemória cache acessada: ");
            p1.getMemoriaCache().printCache();
            System.out.println("\nMemória cache requisitada: ");
            p3.getMemoriaCache().printCache();
            System.out.println("\nMemória RAM: ");
            ram.printMemoria();
        }
    }

    public static void readMiss(RAM ram, Processador p1, Processador p2, Processador p3, int idReceita){
        Integer enderecoBloco2 = p2.confereDadoCache(idReceita);
        Integer enderecoBloco3 = p3.confereDadoCache(idReceita);

        if(enderecoBloco2 == null && enderecoBloco3 == null){
            colocaRamCacheComRam(ram, p1, idReceita);

            System.out.println("\nA leitura foi um readMiss e não estava em nenhuma das outras caches, então foi " +
                    "necessário um acesso a memoria principal e a tag do bloco da cache do processador escolhido é exclusiva.");
            System.out.println("\nMemória cache acessada: ");
            p1.getMemoriaCache().printCache();
        }
        else if (enderecoBloco2 != null && enderecoBloco3 == null){
            readMissOneCopy(ram, p1, p2, enderecoBloco2);
        }
        else if (enderecoBloco2 == null) {
            readMissOneCopy(ram, p1, p3, enderecoBloco3);
        }
        else{
            blocoCache blocoCachep2 = p2.getBlocoCache(enderecoBloco2);
            blocoCache blocoCachep3 = p3.getBlocoCache(enderecoBloco3);

            if(blocoCachep2.getTag() == MemoriaCache.tags.Compartilhado && blocoCachep3.getTag() == MemoriaCache.tags.Compartilhado){
                p1.getMemoriaCache().setBloco(blocoCachep2.getDados(), MemoriaCache.tags.Compartilhado, blocoCachep2.getIndiceRAM());

                System.out.println("\nA leitura foi um readMiss, o dado estava nas duas outras caches e ambas com a tag compartilhado, " +
                        "então foi requisitado uma das duas caches e a tag do bloco da cache do processador escolhido é compartilhado.");
                System.out.println("\nMemória cache acessada: ");
                p1.getMemoriaCache().printCache();
                System.out.println("\nMemória cache requisitada: ");
                p3.getMemoriaCache().printCache();
            }
            else if(blocoCachep2.getTag() == MemoriaCache.tags.Exclusivo && blocoCachep3.getTag() == MemoriaCache.tags.Compartilhado){
                blocoCachep2.setTag(MemoriaCache.tags.Compartilhado);
                p1.getMemoriaCache().setBloco(blocoCachep2.getDados(), MemoriaCache.tags.Compartilhado, blocoCachep2.getIndiceRAM());

                System.out.println("\nA leitura foi um readMiss, o dado estava nas duas outras caches uma com a tag compartilhado e " +
                        "a outra com a tag exclusivo, então foi requisitado a cache com a tag do bloco exclusivo e ambas as tags agora são compartilhado.");
                System.out.println("\nMemória cache acessada: ");
                p1.getMemoriaCache().printCache();
                System.out.println("\nMemória cache requisitada: ");
                p2.getMemoriaCache().printCache();
            }
            else if(blocoCachep3.getTag() == MemoriaCache.tags.Exclusivo && blocoCachep2.getTag() == MemoriaCache.tags.Compartilhado){
                blocoCachep3.setTag(MemoriaCache.tags.Compartilhado);
                p1.getMemoriaCache().setBloco(blocoCachep3.getDados(), MemoriaCache.tags.Compartilhado, blocoCachep3.getIndiceRAM());

                System.out.println("\nA leitura foi um readMiss, o dado estava nas duas outras caches uma com a tag compartilhado e " +
                        "a outra com a tag exclusivo, então foi requisitado a cache com a tag do bloco exclusivo e ambas as tags agora são compartilhado.");
                System.out.println("\nMemória cache acessada: ");
                p1.getMemoriaCache().printCache();
                System.out.println("\nMemória cache requisitada: ");
                p3.getMemoriaCache().printCache();
            }
        }
    }

    private static void readMissOneCopy(RAM ram, Processador p1, Processador p3, Integer enderecoBloco3) {
        blocoCache blocoCachep3 = p3.getBlocoCache(enderecoBloco3);

        if (blocoCachep3.getTag() == MemoriaCache.tags.Exclusivo) {

            blocoCachep3.setTag(MemoriaCache.tags.Compartilhado);
            p1.getMemoriaCache().setBloco(blocoCachep3.getDados(), MemoriaCache.tags.Compartilhado, blocoCachep3.getIndiceRAM());

            System.out.println("\nA leitura foi um readMiss, o dado estava em uma das outras caches e a tag era exclusivo, então " +
                    "essa cache foi requisitada e ambas as tags são compartilhado.");
            System.out.println("\nMemória cache acessada: ");
            p1.getMemoriaCache().printCache();
            System.out.println("\nMemória cache requisitada: ");
            p3.getMemoriaCache().printCache();
        }
        else if (blocoCachep3.getTag() == MemoriaCache.tags.Modificado) {
            ram.updateBloco(blocoCachep3.getDados(), blocoCachep3.getIndiceRAM());

            blocoCachep3.setTag(MemoriaCache.tags.Compartilhado);
            p1.getMemoriaCache().setBloco(blocoCachep3.getDados(), MemoriaCache.tags.Compartilhado, blocoCachep3.getIndiceRAM());

            System.out.println("\nA leitura foi um readMiss, o dado estava em uma das outras caches e a tag era modificado, então " +
                    "houve writeBack, essa cache foi requisitada e ambas as tags são compartilhado.");
            System.out.println("\nMemória cache acessada: ");
            p1.getMemoriaCache().printCache();
            System.out.println("\nMemória cache requisitada: ");
            p3.getMemoriaCache().printCache();
            System.out.println("\nMemória RAM: ");
            ram.printMemoria();
        }
    }

    public static void writeHit(int idReceitaAlterado, int idReceita, int enderecoBloco1 , RAM ram, Processador p1, Processador p2, Processador p3){
        blocoCache blocoCachep1 = p1.getMemoriaCache().getBlocoCache(enderecoBloco1);

        if(blocoCachep1.getTag() == MemoriaCache.tags.Modificado){
            editaCacheHit(idReceitaAlterado, idReceita, enderecoBloco1, p1, blocoCachep1);

            System.out.println("\nA escrita foi um writeHit e o bloco da cache estava com a tag modificado, " +
                    "portanto mantém a mesma tag");
            System.out.println("\nMemória cache acessada: ");
            p1.getMemoriaCache().printCache();
        }
        else if(blocoCachep1.getTag() == MemoriaCache.tags.Compartilhado){
            Integer enderecoBloco2 = p2.confereDadoCache(idReceita);
            Integer enderecoBloco3 = p3.confereDadoCache(idReceita);

            if (enderecoBloco2 != null && enderecoBloco3 != null){
                blocoCache blocoCachep2 = p2.getMemoriaCache().getBlocoCache(enderecoBloco2);
                blocoCache blocoCachep3 = p3.getMemoriaCache().getBlocoCache(enderecoBloco3);

                editaCacheHit(idReceitaAlterado, idReceita, enderecoBloco1, p1, blocoCachep1);

                blocoCachep2.setTag(MemoriaCache.tags.Invalido);
                p2.getMemoriaCache().atualizaBloco(blocoCachep2, enderecoBloco2);

                blocoCachep3.setTag(MemoriaCache.tags.Invalido);
                p3.getMemoriaCache().atualizaBloco(blocoCachep3, enderecoBloco3);

                System.out.println("\nA escrita foi um writeHit, o bloco da cache estava com a tag compartilhado " +
                        "e foi encontrado nas outras duas caches que tiveram suas tags dos blocos invalidadas");
                System.out.println("\nMemória cache acessada: ");
                p1.getMemoriaCache().printCache();
                System.out.println("\nMemória cache requisitada: ");
                p2.getMemoriaCache().printCache();
                System.out.println("\nMemória cache requisitada: ");
                p3.getMemoriaCache().printCache();
            }
            else if (enderecoBloco2 == null) {
                blocoCache blocoCachep3 = p3.getMemoriaCache().getBlocoCache(enderecoBloco3);

                editaCacheHit(idReceitaAlterado, idReceita, enderecoBloco1, p1, blocoCachep1);

                blocoCachep3.setTag(MemoriaCache.tags.Invalido);
                p3.getMemoriaCache().atualizaBloco(blocoCachep3, enderecoBloco3);

                System.out.println("\nA escrita foi um writeHit, o bloco da cache estava com a tag compartilhado " +
                        "e foi encontrado em outra cache que teve sua tag do bloco invalidada");
                System.out.println("\nMemória cache acessada: ");
                p1.getMemoriaCache().printCache();
                System.out.println("\nMemória cache requisitada: ");
                p3.getMemoriaCache().printCache();
            }
            else if (enderecoBloco3 == null) {
                blocoCache blocoCachep2 = p2.getMemoriaCache().getBlocoCache(enderecoBloco2);

                editaCacheHit(idReceitaAlterado, idReceita, enderecoBloco1, p1, blocoCachep1);

                blocoCachep2.setTag(MemoriaCache.tags.Invalido);
                p2.getMemoriaCache().atualizaBloco(blocoCachep2, enderecoBloco2);

                System.out.println("A escrita foi um writeHit, o bloco da cache estava com a tag compartilhado " +
                        "e foi encontrado em outra cache que teve sua tag do bloco invalidada");
                System.out.println("\nMemória cache acessada: ");
                p1.getMemoriaCache().printCache();
                System.out.println("\nMemória cache requisitada: ");
                p2.getMemoriaCache().printCache();
            }
        }
        else if(blocoCachep1.getTag() == MemoriaCache.tags.Exclusivo){
            editaCacheHit(idReceitaAlterado, idReceita, enderecoBloco1, p1, blocoCachep1);

            System.out.println("\nA escrita foi um writeHit e o bloco da cache estava com a tag exclusivo " +
                    "portanto a tag foi alterada para modificado");
            System.out.println("\nMemória cache acessada: ");
            p1.getMemoriaCache().printCache();
        }
    }

    private static void writeMissInvalido(RAM ram, Processador p1, Processador p2, Processador p3, int idReceita, blocoCache blocoCache1, Integer enderecoBloco1, int idReceitaAlterado) {
        Integer enderecoBloco2 = p2.confereDadoCache(idReceita);
        Integer enderecoBloco3 = p3.confereDadoCache(idReceita);

        if(enderecoBloco2 == null && enderecoBloco3 == null){
            colocaRamCacheComCache(ram, p1, blocoCache1, enderecoBloco1);
            editaCacheHit(idReceitaAlterado, idReceita, enderecoBloco1, p1, blocoCache1);

            System.out.println("\nA leitura foi um readMiss, pois a tag da cache acessada é invalido e não estava em nenhuma outra cache, então " +
                    "foi necessário um acesso a memoria principal e a tag do bloco da cache do processador escolhido é exclusiva");
            System.out.println("\nMemória cache acessada: ");
            p1.getMemoriaCache().printCache();
        }
        else if(enderecoBloco2 != null && enderecoBloco3 == null){
            writeMissInvalidoOneCopy(ram, p1, p2, idReceita, idReceitaAlterado, blocoCache1, enderecoBloco1, enderecoBloco2);
        }
        else if(enderecoBloco2 == null && enderecoBloco3 != null){
            writeMissInvalidoOneCopy(ram, p1, p3, idReceita, idReceitaAlterado, blocoCache1, enderecoBloco1, enderecoBloco3);
        }
    }

    public static void writeMissInvalidoOneCopy(RAM ram, Processador p1, Processador p2, int idReceita, int idReceitaAlterado, blocoCache blocoCache1 ,Integer enderecoBloco1, Integer enderecoBloco2) {
        blocoCache blocoCache2 = p2.getMemoriaCache().getBlocoCache(enderecoBloco2);
        if(blocoCache2.getTag() == MemoriaCache.tags.Modificado){
            //writeBack
            ram.updateBloco(blocoCache2.getDados(), blocoCache2.getIndiceRAM());
            blocoCache2.setTag(MemoriaCache.tags.Invalido);

            colocaRamCacheComCache(ram, p1, blocoCache1, enderecoBloco1);
            editaCacheHit(idReceitaAlterado, idReceita, enderecoBloco1, p1, blocoCache1);

            System.out.println("\nA escrita foi um writeMiss, pois a tag da cache acessada é invalido, o dado escolhido estava em uma das outras caches e " +
                    "a tag do bloco era modificado, então houve writeBack, a tag desse bloco foi invalidada, foi necessário um acesso à memoria ram " +
                    "e a tag do bloco da cache do processador escolhido é modificado");
            System.out.println("\nMemória cache acessada: ");
            p1.getMemoriaCache().printCache();
            System.out.println("\nMemória cache requisitada: ");
            p2.getMemoriaCache().printCache();
            System.out.println("\nMemória RAM: ");
            ram.printMemoria();
        }
        else if(blocoCache2.getTag() == MemoriaCache.tags.Compartilhado || blocoCache2.getTag() == MemoriaCache.tags.Exclusivo){
            blocoCache2.setTag(MemoriaCache.tags.Invalido);

            colocaRamCacheComCache(ram, p1, blocoCache1, enderecoBloco1);
            editaCacheHit(idReceitaAlterado, idReceita, enderecoBloco1, p1, blocoCache1);

            System.out.println("\nA escrita foi um writeMiss, pois a tag da cache acessada é invalido, o dado escolhido estava em uma das outras caches e " +
                    "a tag era compartilhado ou exclusivo, então a tag desse bloco foi invalidada, foi necessário um acesso à memoria ram " +
                    "e a tag do bloco da cache do processador escolhido é modificado");
            System.out.println("\nMemória cache acessada: ");
            p1.getMemoriaCache().printCache();
            System.out.println("\nMemória cache requisitada: ");
            p2.getMemoriaCache().printCache();
        }
    }

    public static void writeMiss(RAM ram, Processador p1, Processador p2, Processador p3, int idReceita, int idReceitaAlterado){
        Integer enderecoBloco2 = p2.confereDadoCache(idReceita);
        Integer enderecoBloco3 = p3.confereDadoCache(idReceita);

        if(enderecoBloco2 == null && enderecoBloco3 == null){
            colocaRamCacheComRam(ram, p1, idReceita);
            editaCacheMiss(p1, idReceita, idReceitaAlterado);

            System.out.println("\nA escrita foi um writeMiss e o bloco da cache não estava em nenhuma outra cache, " +
                    "então foi necessário um acesso à memoria ram e a tag do bloco da cache do processador escolhido é modificado");
            System.out.println("\nMemória cache acessada: ");
            p1.getMemoriaCache().printCache();
        }
        else if(enderecoBloco3 == null && enderecoBloco2 != null){
            writeMissOneCopy(ram, p1, p2, idReceita, idReceitaAlterado, enderecoBloco2);
        }
        else if(enderecoBloco3 != null && enderecoBloco2 == null){
            writeMissOneCopy(ram, p1, p3, idReceita, idReceitaAlterado, enderecoBloco3);
        }
    }

    private static void writeMissOneCopy(RAM ram, Processador p1, Processador p2, int idReceita, int idReceitaAlterado, Integer enderecoBloco) {
        blocoCache blocoCache = p2.getMemoriaCache().getBlocoCache(enderecoBloco);
        if(blocoCache.getTag() == MemoriaCache.tags.Modificado){
            //writeBack
            ram.updateBloco(blocoCache.getDados(), blocoCache.getIndiceRAM());
            blocoCache.setTag(MemoriaCache.tags.Invalido);

            colocaRamCacheComRam(ram, p1, idReceita);
            editaCacheMiss(p1, idReceita, idReceitaAlterado);

            System.out.println("\nA escrita foi um writeMiss, o dado escolhido estava em uma das outras caches e a tag do bloco era modificado, então " +
                    "houve writeBack, a tag desse bloco foi invalidada, foi necessário um acesso à memoria ram " +
                    "e a tag do bloco da cache do processador escolhido é modificado");
            System.out.println("\nMemória cache acessada: ");
            p1.getMemoriaCache().printCache();
            System.out.println("\nMemória cache requisitada: ");
            p2.getMemoriaCache().printCache();
            System.out.println("\nMemória RAM: ");
            ram.printMemoria();
        }
        else if(blocoCache.getTag() == MemoriaCache.tags.Compartilhado || blocoCache.getTag() == MemoriaCache.tags.Exclusivo){
            blocoCache.setTag(MemoriaCache.tags.Invalido);

            colocaRamCacheComRam(ram, p1, idReceita);
            editaCacheMiss(p1, idReceita, idReceitaAlterado);

            System.out.println("\nA escrita foi um writeMiss, o dado escolhido estava em uma das outras caches e a tag era compartilhado ou exclusivo, então " +
                    "a tag desse bloco foi invalidada, foi necessário um acesso à memoria ram e a tag do bloco da cache do processador escolhido é modificado");
            System.out.println("\nMemória cache acessada: ");
            p1.getMemoriaCache().printCache();
            System.out.println("\nMemória cache requisitada: ");
            p2.getMemoriaCache().printCache();
        }
    }


    /* Ainda falta colocar o caso de writeMiss e WriteMissInvalido quando as outras duas caches tem o dado(Modificado, Exclusivo e Compartilhado)
       Acrescentar o caso de modificado para ReadMiss, ReadMissInvalido, WriteMiss e WriteMissInvalido quando o dado esta presente nas outras duas caches  */
    public static void main(String[] args) {

        RAM ram = new RAM();
        Scanner ler = new Scanner(System.in);

        Processador processador1 = new Processador(5);
        Processador processador2 = new Processador(5);
        Processador processador3 = new Processador(5);

        for (int i = 0; i < 100; i++) {
            ram.setLinha(i, i);
        }
        System.out.println("\nConteudo memória RAM:");
        ram.printMemoria();

        //TESTE
//        for (int i = 0; i < 7; i++) {
//            int[] bloco1 = {i * 10, i * 10 + 1, i * 10 + 2, i * 10 + 3, i * 10 + 4};
//            int[] bloco2 = {i * 3, i * 3 + 1, i * 3 + 2, i * 3 + 3, i * 3 + 4};
//            int[] bloco3 = {i * 7, i * 7 + 1, i * 7 + 2, i * 7 + 3, i * 7 + 4};
//            processador1.getMemoriaCache().setBloco(bloco1, MemoriaCache.tags.Exclusivo, i);
//            processador2.getMemoriaCache().setBloco(bloco2, MemoriaCache.tags.Exclusivo, i);
//            processador3.getMemoriaCache().setBloco(bloco3, MemoriaCache.tags.Exclusivo, i);
//        }
//        int[] bloco = {1,2,66,88,5};
//        processador1.getMemoriaCache().setBloco(bloco, MemoriaCache.tags.Invalido, 0);
//        int[] bloco2 = {1,2,66,88,5};
//        processador2.getMemoriaCache().setBloco(bloco2, MemoriaCache.tags.Modificado, 0);
//        int[] bloco3 = {0,2,66,4,5};
//        processador3.getMemoriaCache().setBloco(bloco3, MemoriaCache.tags.Compartilhado, 0);

        System.out.println("\nMemoria cache p1: ");
        processador1.getMemoriaCache().printCache();
        System.out.println("\nMemoria cache p2: ");
        processador2.getMemoriaCache().printCache();
        System.out.println("\nMemoria cache p3: ");
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

            System.out.println("Qual dado deseja ler/alterar? ");
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
                            blocoCache blocoCache1 = processador1.getBlocoCache(enderecoBloco1);
                            if(blocoCache1.getTag() == MemoriaCache.tags.Invalido){
                                readMissInvalido(ram, processador1, processador2, processador3, idReceita, blocoCache1, enderecoBloco1);
                                break;
                            }
                            readHit(processador1, idReceita);
                            break;
                        }
                        readMiss(ram, processador1, processador2, processador3, idReceita);
                        break;
                    }
                    else {
                        System.out.println("O que deseja escrever/alterar? ");
                        int dado = Integer.parseInt(ler.next());
                        if(enderecoBloco1 != null){
                            blocoCache blocoCache1 = processador1.getBlocoCache(enderecoBloco1);
                            if(blocoCache1.getTag() == MemoriaCache.tags.Invalido){
                                writeMissInvalido(ram, processador1, processador2, processador3, idReceita, blocoCache1, enderecoBloco1, dado);
                                break;
                            }
                            writeHit(dado, idReceita, enderecoBloco1, ram, processador1, processador2, processador3);
                            break;
                        }
                        writeMiss(ram, processador1, processador2, processador3, idReceita, dado);
                        break;
                    }
                case 2:
                    Integer enderecoBloco2 = processador2.confereDadoCache(idReceita);
                    if(modoEscolhido.equals("l")){
                        if(enderecoBloco2 != null){
                            blocoCache blocoCache2 = processador2.getBlocoCache(enderecoBloco2);
                            if(blocoCache2.getTag() == MemoriaCache.tags.Invalido){
                                readMissInvalido(ram, processador2, processador1, processador3, idReceita, blocoCache2, enderecoBloco2);
                                break;
                            }
                            readHit(processador2, idReceita);
                            break;
                        }
                        readMiss(ram, processador2, processador1, processador3, idReceita);
                        break;
                    }
                    else {
                        System.out.println("O que deseja escrever/alterar? ");
                        int dado = Integer.parseInt(ler.next());
                        if(enderecoBloco2 != null){
                            blocoCache blocoCache2 = processador2.getBlocoCache(enderecoBloco2);
                            if(blocoCache2.getTag() == MemoriaCache.tags.Invalido){
                                writeMissInvalido(ram, processador2, processador1, processador3, idReceita, blocoCache2, enderecoBloco2, dado);
                                break;
                            }
                            writeHit(dado, idReceita, enderecoBloco2, ram, processador2, processador1, processador3);
                            break;
                        }
                        writeMiss(ram, processador2, processador1, processador3, idReceita, dado);
                        break;
                    }
                case 3:
                    Integer enderecoBloco3 = processador3.confereDadoCache(idReceita);
                    if(modoEscolhido.equals("l")){
                        if(enderecoBloco3 != null){
                            readHit(processador3, idReceita);
                            break;
                        }
                        readMiss(ram, processador3, processador2, processador1, idReceita);
                        break;
                    }
                    else {
                        System.out.println("O que deseja escrever/alterar? ");
                        int dado = Integer.parseInt(ler.next());
                        if(enderecoBloco3 != null){
                            blocoCache blocoCache3 = processador3.getBlocoCache(enderecoBloco3);
                            if(blocoCache3.getTag() == MemoriaCache.tags.Invalido){
                                readMissInvalido(ram, processador3, processador1, processador2, idReceita, blocoCache3, enderecoBloco3);
                                break;
                            }
                            readHit(processador3, idReceita);
                            break;
                        }
                        writeMiss(ram, processador3, processador2, processador1, idReceita, dado);
                        break;
                    }
                default:
                    System.out.println("O número inserido é inválido! Digite um número entre 1 e 3");
            }

//            processador1.getMemoriaCache().printCache();
//            processador2.getMemoriaCache().printCache();
//            processador3.getMemoriaCache().printCache();
//
//            ram.printMemoria();

            System.out.println("\nDeseja continuar? (sim:1/não:-1)");
            i = ler.nextInt();
            System.out.println("\n");
        }


//        processador1.getMemoriaCache().printCache();
//        processador2.getMemoriaCache().printCache();
//        processador3.getMemoriaCache().printCache();

//        ram.printMemoria();

    }
}
