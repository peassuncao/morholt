package br.fapema.morholt.web.server;

import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

import br.fapema.morholt.web.shared.FilterInterface;
import br.fapema.morholt.web.shared.MyCompositeFilter;
import br.fapema.morholt.web.shared.MyFilterPredicate;

import static br.fapema.morholt.web.shared.MyFilterPredicate.MyFilterOperator;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class FilterConversor {
	public static Filter convert (FilterInterface filterInterface) {
		if (filterInterface instanceof MyFilterPredicate) {
			return convertMyFilterPredicate((MyFilterPredicate)filterInterface);
		}
		else {
			MyCompositeFilter myCompositeFilter = (MyCompositeFilter) filterInterface;
			
			CompositeFilter compositeFilter = convertMyCompositeFilter(myCompositeFilter);
			return compositeFilter;
		}
	}
	
	private static FilterPredicate convertMyFilterPredicate(MyFilterPredicate myFilterPredicate) {
		String propertyName = myFilterPredicate.getName();
		FilterOperator filterOperator = convertMyFilterOperator(myFilterPredicate.getType());
		
		Object value = myFilterPredicate.getValue();
		if (myFilterPredicate.getDateValue() != null) {
			Date date = (Date) value;
			GregorianCalendar gregorianCalendar = new GregorianCalendar();
			gregorianCalendar.setTime(date);
			GregorianCalendar newGregorianCalendar = new GregorianCalendar();
			newGregorianCalendar.set(gregorianCalendar.get(Calendar.YEAR), gregorianCalendar.get(Calendar.MONTH), gregorianCalendar.get(Calendar.DAY_OF_MONTH));
			newGregorianCalendar.set(GregorianCalendar.HOUR_OF_DAY, 0);
			newGregorianCalendar.set(GregorianCalendar.MINUTE, 0);
			newGregorianCalendar.set(GregorianCalendar.SECOND, 0);
			newGregorianCalendar.set(GregorianCalendar.MILLISECOND, 0);
			
			value = newGregorianCalendar.getTime();
		}
		
		return new FilterPredicate(propertyName, filterOperator, value);
	}
	
	private static CompositeFilter convertMyCompositeFilter(MyCompositeFilter myFilterPredicate) {
		FilterInterface first = myFilterPredicate.getFilter1();
		FilterInterface second = myFilterPredicate.getFilter2();
		
		if(myFilterPredicate.getType().value.equals(CompositeFilterOperator.AND.toString())) {
			return CompositeFilterOperator.and(convert(first), convert(second) );
		}
		else {
			return CompositeFilterOperator.or(convert(first), convert(second) );
		}
	}
	
	
	private static FilterOperator convertMyFilterOperator (MyFilterOperator myFilterOperator) {
		String value = myFilterOperator.value;
		
		for(FilterOperator filterOperator : FilterOperator.values()) {
			if(filterOperator.toString().equals(value)) {
				return filterOperator;
			}
		}
		throw new RuntimeException("can´t convert MyFilterPredicate.MyFilterOperator: " + myFilterOperator);
	}
}
