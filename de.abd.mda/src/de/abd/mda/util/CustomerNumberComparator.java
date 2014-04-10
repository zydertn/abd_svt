package de.abd.mda.util;

import java.util.Comparator;

import de.abd.mda.persistence.dao.Customer;
import de.abd.mda.persistence.dao.DaoObject;

public class CustomerNumberComparator implements Comparator<DaoObject> {

	@Override
	public int compare(DaoObject c1, DaoObject c2) {
		// TODO Auto-generated method stub
		return ((Customer) c1).getCustomernumber().compareTo(((Customer) c2).getCustomernumber());
	}

}
