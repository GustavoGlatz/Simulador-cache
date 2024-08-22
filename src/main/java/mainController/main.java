package mainController;

import entity.MemoriaCache;
import entity.MemoriaCache.blocoCache;
import entity.Processador;
import entity.RAM;

import java.util.Scanner;

public class main {

    // Analisa se o indice RAM do bloco solicitado existe em outra cache, caso exista e esteja modificado: writeBack e torna invalido
    public static void analisaWriteback(RAM ram, int idReceita, blocoCache[] blocoCaches) {
        int inicioBlocoRam = ram.getIndiceBloco(idReceita);
        for (int i = 0; i < blocoCaches.length; i++) {
            if(blocoCaches[i].getTag() == MemoriaCache.tags.Modificado && blocoCaches[i].getIndiceRAM() == inicioBlocoRam){
                ram.updateBloco(blocoCaches[i].getDados(), blocoCaches[i].getIndiceRAM());
                blocoCaches[i].setTag(MemoriaCache.tags.Invalido);
                System.out.println("\nO dado procurado foi modificado em outra cache, então foi realizado o write back " +
                        "e a tag deste bloco foi modificado para inválido");
                ram.printMemoria();
            }
        }
    }

    //Coloca um bloco da ram num bloco da cache a partir de um dado da ram
    private static boolean colocaRamCacheComRam(RAM ram, Processador p1, int idReceita) {
        int inicioBlocoRAM = ram.getIndiceBloco(idReceita);
        int[] blocoRAM = ram.getBloco(inicioBlocoRAM);

        blocoCache blocoASerRetirado = p1.getMemoriaCache().getBlocoInicioFila();
        blocoCache[] todosBlocos = p1.getMemoriaCache().getTodosBlocos();
        if(todosBlocos != null) {
            for(int i = 0; i < todosBlocos.length; i++) {
                if(todosBlocos[i].equals(blocoASerRetirado) && blocoASerRetirado.getTag() == MemoriaCache.tags.Modificado && todosBlocos.length == 5){
                    ram.updateBloco(blocoASerRetirado.getDados(), blocoASerRetirado.indiceRAM);
                    p1.setBlocoCache(blocoRAM, MemoriaCache.tags.Exclusivo, inicioBlocoRAM);
                    System.out.println("\nA tag do bloco retirado da cache era modificado, então houve a necessidade de realizar o writeBack do bloco retirado");
                    ram.printMemoria();
                    return true;
                }
                else if(todosBlocos[i].getIndiceRAM() == inicioBlocoRAM) {
                    System.out.println("\nO bloco que continha o dado " + idReceita + " ja está inserido na cache com indice da RAM igual a " + inicioBlocoRAM +
                            " porém este dado foi modificado");
                    return false;
                }
            }
        }
        else if(todosBlocos == null){
            p1.setBlocoCache(blocoRAM, MemoriaCache.tags.Exclusivo, inicioBlocoRAM);
            if(blocoRAM == null){
                return false;
            }
            return true;
        }
        p1.setBlocoCache(blocoRAM, MemoriaCache.tags.Exclusivo, inicioBlocoRAM);
        return true;
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
    private static boolean editaCacheMiss(Processador p1, int idReceita, int idReceitaAlterado) {
        Integer enderecoBloco1 = p1.confereDadoCache(idReceita);
        if(enderecoBloco1 != null){
            blocoCache blocoCachep1 = p1.getMemoriaCache().getBlocoCache(enderecoBloco1);
            editaCacheHit(idReceitaAlterado, idReceita, enderecoBloco1, p1, blocoCachep1);
            return true;
        }
        p1.getMemoriaCache().printCache();
        return false;
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

        blocoCache[] todosBlocosCache2 = p2.getMemoriaCache().getTodosBlocos();
        if (todosBlocosCache2 != null) {
            analisaWriteback(ram, idReceita, todosBlocosCache2);
        }
        blocoCache[] todosBlocosCache3 = p3.getMemoriaCache().getTodosBlocos();
        if (todosBlocosCache3 != null) {
            analisaWriteback(ram, idReceita, todosBlocosCache3);
        }

        if(enderecoBloco2 == null && enderecoBloco3 == null){
            boolean aux = colocaRamCacheComRam(ram, p1, idReceita);

            if (aux) {
                System.out.println("\nA leitura foi um readMiss e não estava em nenhuma das outras caches, então foi " +
                        "necessário um acesso a memoria principal e a tag do bloco da cache do processador escolhido é exclusiva.");
            }
            System.out.println("\nMemória cache acessada: ");
            p1.getMemoriaCache().printCache();
        }
        else if (enderecoBloco2 != null && enderecoBloco3 == null){
            readMissOneCopy(ram, p1, p2, enderecoBloco2);
        }
        else if (enderecoBloco2 == null && enderecoBloco3 != null) {
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
        else if(blocoCachep3.getTag() == MemoriaCache.tags.Invalido) {
            p1.getMemoriaCache().setBloco(blocoCachep3.getDados(), MemoriaCache.tags.Exclusivo, blocoCachep3.getIndiceRAM());

            System.out.println("\nA leitura foi um readMiss, o dado estava em uma das outras caches e a tag era invalido, então " +
                    "a memória RAM foi requistada e a tag é exclusivo");
            p1.getMemoriaCache().printCache();
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

        blocoCache[] todosBlocosCache2 = p2.getMemoriaCache().getTodosBlocos();
        if (todosBlocosCache2 != null) {
            analisaWriteback(ram, idReceita, todosBlocosCache2);
        }
        blocoCache[] todosBlocosCache3 = p3.getMemoriaCache().getTodosBlocos();
        if (todosBlocosCache3 != null) {
            analisaWriteback(ram, idReceita, todosBlocosCache3);
        }

        if(enderecoBloco2 == null && enderecoBloco3 == null){
            boolean aux2 = colocaRamCacheComRam(ram, p1, idReceita);
            boolean aux = false;
            if (aux2) {
                aux = editaCacheMiss(p1, idReceita, idReceitaAlterado);
            }

            if (aux) {
                System.out.println("\nA escrita foi um writeMiss e o bloco da cache não estava em nenhuma outra cache, " +
                        "então foi necessário um acesso à memoria ram e a tag do bloco da cache do processador escolhido é modificado");
                System.out.println("\nMemória cache acessada: ");
                p1.getMemoriaCache().printCache();
            }
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
        else if(blocoCache.getTag() == MemoriaCache.tags.Invalido){
            p1.getMemoriaCache().setBloco(blocoCache.getDados(), MemoriaCache.tags.Exclusivo, blocoCache.getIndiceRAM());

            System.out.println("\nA escrita foi um writeMiss, o dado estava em uma das outras caches e a tag era invalido, então " +
                    "a memória RAM foi requistada e a tag é exclusivo");
            p1.getMemoriaCache().printCache();
        }
    }

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

            processador1.getMemoriaCache().printCache();
            processador2.getMemoriaCache().printCache();
            processador3.getMemoriaCache().printCache();

            ram.printMemoria();

            System.out.println("\nDeseja continuar? (sim:1/não:-1)");
            i = ler.nextInt();
            System.out.println("\n");
        }
    }
}
