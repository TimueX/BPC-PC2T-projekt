package cz.vut.fekt.project;

import cz.vut.fekt.project.model.CooperationLevel;
import cz.vut.fekt.project.model.Worker;
import cz.vut.fekt.project.service.DatabaseService;
import cz.vut.fekt.project.service.FileService;
import cz.vut.fekt.project.service.Registry;
import cz.vut.fekt.project.util.InputUtil;

public class Main {

    public static void main(String[] args) {
        Registry reg = new Registry();
        
        DatabaseService.init();
        DatabaseService.load(reg);

        while (true) {
            System.out.println("\n1 - Pridaj zamestnanca");
            System.out.println("2 - Pridaj vztah zamestnancov");
            System.out.println("3 - Vymaz zamestnanca");
            System.out.println("4 - Skill zamestnanca");
            System.out.println("5 - Vypis zamestnancov");
            System.out.println("6 - Statistiky");
            System.out.println("7 - Uloz zamestnancov");
            System.out.println("8 - Detail zamestnanca");
            System.out.println("9 - Vypis zamestnancov podla skupin");
            System.out.println("10 - Pocet zamestnancov");
            System.out.println("11 - Nacitaj zo suboru");
            System.out.println("0 - Koniec");

            int c = InputUtil.readInt("Volba: ");

            switch (c) {
                case 1 -> {
                	String type;
                    while (true) {
                        type = InputUtil.readString("Typ (A/S): ").trim().toUpperCase();

                        if (type.equals("A") || type.equals("S")) break;

                        System.out.println("Neplatny typ (zadaj A alebo S)");
                    }

                    String name = InputUtil.readString("Meno: ");
                    String sur = InputUtil.readString("Priezvisko: ");
                    int year = InputUtil.readInt("Rok: ");
                    Worker w = reg.addWorker(type, name, sur, year);

                    if (w == null) {
                        System.out.println("Zamestnanec nebol vytvoreny");
                    } else {
                    	System.out.println("Pridany: " + w);
                    }
                }

                case 2 -> {
                	int a;
                	while (true) {
                	    a = InputUtil.readInt("ID1: ");
                	    if (reg.getWorker(a) != null) break;
                	    System.out.println("Zamestnanec neexistuje");
                	}
                	
                	int b;
                	while (true) {
                	    b = InputUtil.readInt("ID2: ");
                	    if (reg.getWorker(b) != null) break;
                	    System.out.println("Zamestnanec neexistuje");
                	}
                	
                	if (a == b) {
                	    System.out.println("Nemozes zadat rovnake ID");
                	    break;
                	}
                	
                    while (true) {
                        int lvl = InputUtil.readInt("0-BAD 1-AVG 2-GOOD: ");

                        if (lvl >= 0 && lvl < CooperationLevel.values().length) {
                            reg.addRelation(a, b, CooperationLevel.values()[lvl]);
                            break;
                        }

                        System.out.println("Zadaj hodnotu 0, 1 alebo 2");
                    }
                }

                case 3 -> {
                    int id = InputUtil.readInt("ID: ");
                    reg.removeWorker(id);
                }

                case 4 -> {
                    int id;

                    while (true) {
                        id = InputUtil.readInt("ID: ");
                        if (reg.getWorker(id) != null) break;
                        System.out.println("Zamestnanec neexistuje");
                    }

                    reg.getWorker(id).useSkill();
                }

                case 5 -> reg.printAllSorted();

                case 6 -> reg.printStats();

                case 7 -> {
                    try {
                        FileService.save("data.txt", reg);
                    } catch (Exception e) {
                        System.out.println("Chyba ulozenia");
                    }
                }
                
                case 8 -> {
                    int id = InputUtil.readInt("ID: ");
                    reg.printWorkerDetail(id);
                }
                
                case 9 -> reg.printGroupedSorted();
                
                case 10 -> reg.printGroupCounts();
                
                case 11 -> {
                    try {
                        FileService.load("data.txt", reg);
                    } catch (Exception e) {
                        System.out.println("Chyba nacitania");
                    }
                }

                case 0 -> {
                    DatabaseService.save(reg);
                    System.exit(0);
                }
            }
        }
    }
}