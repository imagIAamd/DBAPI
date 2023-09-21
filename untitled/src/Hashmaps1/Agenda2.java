package Hashmaps1;

import java.util.*;


public class Agenda2{


    private TreeMap llista = new TreeMap();

    public static void main(String args[]){


        // Crearem una nova agenda ordenada
        Agenda2 a2 = new Agenda2();


        // Afegirem una sèrie de parells <key,value> a l’agenda
        a2.llista.put("Metge", "(+52)-4000-5000");
        a2.llista.put("Casa", "(888)-4500-3400");
        a2.llista.put("Germa", "(575)-2042-3233");
        a2.llista.put("Oncle", "(421)-1010-0020");
        a2.llista.put("Sogres", "(334)-6105-4334");
        a2.llista.put("Oficina", "(304)-5205-8454");
        a2.llista.put("Advocat", "(756)-1205-3454");
        a2.llista.put("Pare", "(55)-9555-3270");
        a2.llista.put("Botiga", "(874)-2400-8600");

        // Cridem un mètode que mostrarà la llista de contactes de l’agenda.
        // L’orde establert als element inserits al TreeMap és ascendent
        // del seu ordre d’inserció.
        mostrarAgenda(a2.llista);

        // Definirem dos subarbres utilitzant mètodes específics
        SortedMap AO=a2.llista.subMap("A","O");
        SortedMap PZ=a2.llista.tailMap("P");

        System.out.println("---- Agenda A-O ----");
        mostrarAgenda(AO);
        System.out.println("---- Agenda P-Z ----");
        mostrarAgenda(PZ);
    }

    public static void mostrarAgenda(Map m){
        System.out.println("> Agenda amb " + m.size() + " telefons");
        for(Iterator i=m.keySet().iterator(); i.hasNext();){
            String k=(String)i.next();
            String v=(String)m.get(k);
            System.out.println("  "+k+ " : " +v);
        }
    }
}
