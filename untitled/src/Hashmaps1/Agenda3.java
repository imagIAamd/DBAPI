package Hashmaps1;

import java.util.HashMap;
import java.util.NavigableMap;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.InputMismatchException;

public class Agenda3 {
    private NavigableMap<String, Float> jocs = new TreeMap<String, Float>();
    private Scanner sc = new Scanner(System.in);

    public static void main(String args[]){
        Agenda3 a = new Agenda3();

        int op = 0;

        while (true){
            System.out.println("BOTIGA\n" +
                                "1)Afegir Joc\n" +
                                "2)Modificar Preu\n" +
                                "3)Eliminar Joc\n" +
                                "4)Mostrar Jocs\n\n" +
                                "5)Sortir\n");

            try {
                op = a.sc.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                a.sc.nextLine();
                continue;
            }

            if (op == 1) {
                a.inserirJoc();
            } else if (op == 2) {
                a.modificarPreu();
            } else if (op == 3) {
                a.eliminarJoc();
            } else if (op == 4) {
                a.mostrarJocs();
            } else if (op == 5) {
                break;
            }

        }

    }

    public void inserirJoc(){
        String nom;
        Float preu;

        System.out.println("Introdueix el nom del joc: ");
        nom = sc.next();

        if (existeix(nom)){
            System.out.println("El joc ja existeix.");
            return;
        }
        System.out.println("Introdueix el preu del joc: ");
        preu = sc.nextFloat();

        jocs.put(nom, preu);

    }
    public void mostrarJocs(){
        System.out.println(jocs.descendingMap());
    }

    public boolean existeix(String joc){
        if (jocs.get(joc) == null){
            return false;
        }
        return true;
    }

    public void eliminarJoc(){
        String nom;

        System.out.println("Quin joc vols eliminar?");
        nom = sc.next();

        if(existeix(nom)){
            System.out.println("Segur que vols eliminar " + nom + "?(1=si/0=no)");
            if(sc.nextInt()==1){
                jocs.remove(nom);
                System.out.println("El joc ha sigut eliminat.");
            }

        }
    }

    public void modificarPreu(){
        String nom;
        Float preu;

        System.out.println("Nom del joc: ");
        nom = sc.next();

        if(!existeix(nom)){
            System.out.println("No existeix el joc.");
            return;
        }

        System.out.println("Nou preu: ");
        preu = sc.nextFloat();
        jocs.put(nom, preu);
    }
}
