package cz.vut.fekt.project.model;

import java.util.Comparator;

public class WorkerSurnameComparator implements Comparator<Worker> {

	@Override
	public int compare(Worker a, Worker b) {
	    int c = a.getSurname().compareToIgnoreCase(b.getSurname());
	    if (c != 0) return c;
	    return a.getName().compareToIgnoreCase(b.getName());
	}
}
