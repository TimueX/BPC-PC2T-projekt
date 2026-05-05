package cz.vut.fekt.project.model;

public class Analyst extends Worker {

    public Analyst(int id, String name, String surname, int birthYear) {
        super(id, name, surname, birthYear, WorkerType.ANALYST);
    }

    @Override
    public void useSkill() {
    	System.out.println("=== SKILL ===");
    	System.out.println("Typ: Datovy analytik");
        Worker best = null;
        int max = -1;

        for (Worker w : relations.keySet()) {
            int common = countCommon(w);
            if (common > max) {
                max = common;
                best = w;
            }
        }

        if (best != null) {
            System.out.println("Najviac spolocnych ma s: " + best);
        } else {
            System.out.println("Ziadne data");
        }
    }

    private int countCommon(Worker other) {
        int count = 0;
        for (Worker w : relations.keySet()) {
        	if (other.getRelations().containsKey(w)) {
                count++;
            }
        }
        return count;
    }
}