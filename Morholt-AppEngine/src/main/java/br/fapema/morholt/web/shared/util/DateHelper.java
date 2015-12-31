package br.fapema.morholt.web.shared.util;

import java.util.Date;
import java.util.logging.Logger;

import com.google.gwt.i18n.client.DateTimeFormat;

public class DateHelper {


	private static final Logger log = Logger.getLogger(DateHelper.class.getName());
	
	public DateHelper() {
	}
	
	public static Date convertYMDString(String ymd) {
		DateTimeFormat format = DateTimeFormat.getFormat("yyyy/MM/dd");  
		Date date = new Date(format.parse(ymd).getTime());  
		return date;
	}
	
	/**
	 * 
	 * @param date
	 * @param format e.g. dd/MM/yyyy
	 * @return
	 */
	public static String convertDate(Date date, String format) {
		if(date == null) return "";
		String result = DateTimeFormat.getFormat(format).format(date);
		return result;
	}

}
