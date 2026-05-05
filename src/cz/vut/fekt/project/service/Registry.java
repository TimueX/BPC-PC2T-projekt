package cz.vut.fekt.project.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.vut.fekt.project.model.Analyst;
import cz.vut.fekt.project.model.CooperationLevel;
import cz.vut.fekt.project.model.Security;
import cz.vut.fekt.project.model.Worker;
import cz.vut.fekt.project.model.WorkerType;
import cz.vut.fekt.project.model.WorkerSurnameComparator;

public class Registry {
    private Map<Integer, Worker> workers = new HashMap<>();
    private int nextId = 1;
    int generateId() {
        return nextId++;
    }
    void updateNextId() {
        nextId = workers.keySet().stream().max(Integer::compare).orElse(0) + 1;
    }

    public Worker addWorker(String type, String name, String surname, int year) {
	    
	    	String t = type.trim().toUpperCase();
	
	    	WorkerType wt;
	
	    	if (t.equals("A")) {
	    	    wt = WorkerType.ANALYST;
	    	} else if (t.equals("S")) {
	    	    wt = WorkerType.SECURITY;
	    	} else {
	    	    try {
	    	        wt = WorkerType.valueOf(t);
	    	    } catch (IllegalArgumentException e) {
	    	        System.out.println("Neplatny typ");
	    	        return null;
	    	    }
	    	}
	
	    	Worker w = (wt == WorkerType.ANALYST)
	    		    ? new Analyst(generateId(), name, surname, year)
	    		    : new Security(generateId(), name, surname, year);

        workers.put(w.getId(), w);
        return w;
    }

    public Worker getWorker(int id) {
        return workers.get(id);
    }

    public void removeWorker(int id) {
        Worker w = workers.get(id);

        if (w == null) {
            System.out.println("Zamestnanec s ID " + id + " neexistuje alebo uz bol vymazany");
            return;
        }

        for (Worker other : workers.values()) {
            other.removeRelation(w);
        }

        workers.remove(id);
        System.out.println("Zamestnanec bol vymazany");
    }

    public void addRelation(int id1, int id2, CooperationLevel lvl) {

        if (id1 == id2) {
            System.out.println("Nemoze byt vztah sam so sebou");
            return;
        }

        Worker w1 = workers.get(id1);
        Worker w2 = workers.get(id2);

        if (w1 == null || w2 == null) {
            System.out.println("Zamestnanec neexistuje");
            return;
        }

        if (w1.getRelations().containsKey(w2)) {
            System.out.println("Vztah uz existuje");
            return;
        }
        
        if (lvl == null) {
            System.out.println("Neplatna uroven vztahu");
            return;
        }

        w1.addRelation(w2, lvl);
        w2.addRelation(w1, lvl);
        System.out.println("Vztah bol vytvoreny");
    }

    public void printWorkerDetail(int id) {
        Worker w = workers.get(id);

        if (w == null) {
            System.out.println("Zamestnanec neexistuje");
            return;
        }

        System.out.println("=== DETAIL ===");
        System.out.println("ID: " + w.getId() + " | " + w.getName() + " " + w.getSurname() +  " | rok: " + w.getBirthYear());

        System.out.println("Pocet vazieb: " + w.getRelationCount());

        int bad = 0, avg = 0, good = 0;

        for (var entry : w.getRelations().entrySet()) {

            CooperationLevel lvl = entry.getValue();

            switch (lvl) {
                case BAD -> bad++;
                case AVERAGE -> avg++;
                case GOOD -> good++;
            }
        }

        System.out.println("BAD: " + bad);
        System.out.println("AVERAGE: " + avg);
        System.out.println("GOOD: " + good);
    }
    
    
    public void printGroupedSorted() {

        List<Worker> analysts = new ArrayList<>();
        List<Worker> security = new ArrayList<>();

        for (Worker w : workers.values()) {
            if (w.getType() == WorkerType.ANALYST) {
                analysts.add(w);
            } else if (w.getType() == WorkerType.SECURITY) {
                security.add(w);
            }
        }

        Comparator<Worker> cmp = new WorkerSurnameComparator();

        analysts.sort(cmp);
        security.sort(cmp);

        System.out.println("=== Datovy analytik ===");
        for (Worker w : analysts) {
            System.out.println(w);
        }

        System.out.println("\n=== Bezpecnostny specialista ===");
        for (Worker w : security) {
            System.out.println(w);
        }
    }
    
    public void printAllSorted() {
    	workers.values().stream()
        	.sorted(new WorkerSurnameComparator())
        	.forEach(System.out::println);
    }

    public void printStats() {

        int bad = 0, avg = 0, good = 0;

        for (Worker w : workers.values()) {
        	for (var entry : w.getRelations().entrySet()) {

        	    Worker other = entry.getKey();

        	    if (w.getId() < other.getId()) {

        	        CooperationLevel lvl = entry.getValue();

        	        switch (lvl) {
        	            case BAD -> bad++;
        	            case AVERAGE -> avg++;
        	            case GOOD -> good++;
        	        }
        	    }
        	}
        }

        System.out.println("=== STATISTIKY ===");

        int maxCount = workers.values().stream()
                .mapToInt(Worker::getRelationCount)
                .max()
                .orElse(0);

        System.out.println("Najviac vazieb (" + maxCount + "):");

        for (Worker w : workers.values()) {
            if (w.getRelationCount() == maxCount) {
                System.out.println(w);
            }
        }

        if (bad + avg + good == 0) {
            System.out.println("Prevazujuca kvalita: ZIADNE VAZBY");
        }
        else if (bad == avg && avg == good) {
            System.out.println("Prevazujuca kvalita: REMIZA (vsetky rovnake)");
        }        
        else if (bad == avg && bad > good) {
            System.out.println("Prevazujuca kvalita: REMIZA (BAD, AVERAGE)");
        }
        else if (avg == good && avg > bad) {
            System.out.println("Prevazujuca kvalita: REMIZA (AVERAGE, GOOD)");
        }
        else if (bad == good && bad > avg) {
            System.out.println("Prevazujuca kvalita: REMIZA (BAD, GOOD)");
        }
        else if (bad > avg && bad > good) {
            System.out.println("Prevazujuca kvalita: BAD");
        }
        else if (avg > bad && avg > good) {
            System.out.println("Prevazujuca kvalita: AVERAGE");
        }
        else {
            System.out.println("Prevazujuca kvalita: GOOD");
        }

        System.out.println("--- Detail kvality ---");
        System.out.println("Detail: BAD=" + bad + ", AVG=" + avg + ", GOOD=" + good);
    }
    
    public void printGroupCounts() {

        int analysts = 0;
        int security = 0;

        for (Worker w : workers.values()) {
            if (w.getType() == WorkerType.ANALYST) {
                analysts++;
            } else if (w.getType() == WorkerType.SECURITY) {
                security++;
            }
        }

        System.out.println("=== POCET ZAMESTNANCOV ===");
        System.out.println("Datovi analytici: " + analysts);
        System.out.println("Bezpecnostni specialisti: " + security);
        System.out.println("Spolu: " + (analysts + security));
    }

    public Collection<Worker> getAll() {
        return workers.values();
    }
    
    public void addLoadedWorker(Worker w) {
        workers.put(w.getId(), w);
    }
    
    public void clear() {
        workers.clear();
    }
}