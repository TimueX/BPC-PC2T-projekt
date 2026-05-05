package cz.vut.fekt.project.model;

public class Security extends Worker {

    public Security(int id, String name, String surname, int birthYear) {
        super(id, name, surname, birthYear, WorkerType.SECURITY);
    }

    @Override
    public void useSkill() {
    	System.out.println("=== SKILL ===");
    	System.out.println("Typ: Bezpecnostny specialista");
        double score = 0;

        for (CooperationLevel lvl : relations.values()) {
            switch (lvl) {
                case BAD -> score += 3;
                case AVERAGE -> score += 2;
                case GOOD -> score += 1;
            }
        }

        score *= Math.log(relations.size() + 1);

        System.out.println("Rizikove skore: " + score);
    }
}
