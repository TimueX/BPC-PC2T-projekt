package cz.vut.fekt.project.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import cz.vut.fekt.project.model.Analyst;
import cz.vut.fekt.project.model.CooperationLevel;
import cz.vut.fekt.project.model.Security;
import cz.vut.fekt.project.model.Worker;
import cz.vut.fekt.project.model.WorkerType;

public class FileService {

	public static void save(String file, Registry reg) throws IOException {

	    try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {

	        bw.write("#WORKERS");
	        bw.newLine();

	        for (Worker w : reg.getAll()) {
	            String type = w.getType().name();

	            bw.write(type + ";" +
	                    w.getId() + ";" +
	                    w.getName() + ";" +
	                    w.getSurname() + ";" +
	                    w.getBirthYear());

	            bw.newLine();
	        }

	        bw.write("#RELATIONS");
	        bw.newLine();

	        for (Worker w : reg.getAll()) {
	            for (var entry : w.getRelations().entrySet()) {
	                Worker other = entry.getKey();

	                if (w.getId() < other.getId()) {
	                    bw.write(w.getId() + ";" +
	                            other.getId() + ";" +
	                            entry.getValue());
	                    bw.newLine();
	                }
	            }
	        }
	    }
	}
	
	public static void load(String file, Registry reg) throws IOException {

	    reg.clear();

	    try (BufferedReader br = new BufferedReader(new FileReader(file))) {

	        String line;
	        boolean readingWorkers = false;
	        boolean readingRelations = false;

	        Map<Integer, Worker> temp = new HashMap<>();

	        while ((line = br.readLine()) != null) {

	            if (line.equals("#WORKERS")) {
	                readingWorkers = true;
	                readingRelations = false;
	                continue;
	            }

	            if (line.equals("#RELATIONS")) {
	                readingWorkers = false;
	                readingRelations = true;
	                continue;
	            }

	            if (readingWorkers) {
	                String[] p = line.split(";");

	                String type = p[0];
	                int id = Integer.parseInt(p[1]);
	                String name = p[2];
	                String surname = p[3];
	                int year = Integer.parseInt(p[4]);

	                WorkerType wt;

	                try {
	                    wt = WorkerType.valueOf(type.toUpperCase());
	                } catch (IllegalArgumentException e) {
	                    System.out.println("Neznamy typ, preskakujem riadok: " + type);
	                    continue;
	                }

	                Worker w;

	                if (wt == WorkerType.ANALYST) {
	                    w = new Analyst(id, name, surname, year);
	                } else {
	                    w = new Security(id, name, surname, year);
	                }

	                reg.addLoadedWorker(w);
	                temp.put(id, w);
	            }

	            if (readingRelations) {
	                String[] p = line.split(";");

	                int id1 = Integer.parseInt(p[0]);
	                int id2 = Integer.parseInt(p[1]);
	                CooperationLevel lvl;

	                try {
	                    lvl = CooperationLevel.valueOf(p[2].toUpperCase());
	                } catch (IllegalArgumentException e) {
	                    System.out.println("Neplatna hodnota vztahu, preskakujem riadok: " + p[2]);
	                    continue;
	                }

	                Worker w1 = temp.get(id1);
	                Worker w2 = temp.get(id2);

	                if (w1 == null || w2 == null) {
	                    System.out.println("Preskakujem neplatny vztah: " + id1 + " - " + id2);
	                    continue;
	                }

	                reg.addRelation(w1.getId(), w2.getId(), lvl);
	            }
	        }
	    }

	    reg.updateNextId();
	}
	
	
}