package cz.vut.fekt.project.model;

import java.util.HashMap;
import java.util.Map;

public abstract class Worker {
    protected int id;
    protected String name;
    protected String surname;
    protected int birthYear;
    protected WorkerType type;

    protected Map<Worker, CooperationLevel> relations;

    public Map<Worker, CooperationLevel> getRelations() {
        return relations;
    }
    
    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public int getBirthYear() {
        return birthYear;
    }
    
    public WorkerType getType() {
        return type;
    }
    
    public Worker(int id, String name, String surname, int birthYear, WorkerType type) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.birthYear = birthYear;
        this.type = type;
        this.relations = new HashMap<>();
    }

    public void addRelation(Worker w, CooperationLevel level) {
        relations.put(w, level);
    }

    public void removeRelation(Worker w) {
        relations.remove(w);
    }

    public int getRelationCount() {
        return relations.size();
    }

    public abstract void useSkill();

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Worker w)) return false;
        return id == w.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public String toString() {
        return id + " | " + name + " " + surname + " (" + birthYear + ")";
    }
}