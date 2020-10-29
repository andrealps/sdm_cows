package es.uniovi.eii.cows.controller.reader.filter;

import android.util.Log;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;

import es.uniovi.eii.cows.model.NewsItem;

public class CovidFilter {

	public static String[] relatedTerms = {
			"covid",
			"covid-19",
			"coronavirus",
			"sars-cov-2"
	};

	public static void evaluate(NewsItem item) {
		if (!item.isCovidRelated()) {
			long value = 0;
			value += evaluateString(item.getTitle()) * 2;
			value += evaluateString(item.getDescription());
			item.setCovidRelated(value > 3);
			Log.d("VALUE", String.valueOf(value));
		}
	}

	public static boolean filter(NewsItem item) {
		return item.isCovidRelated();
	}

	private static long evaluateString(String s) {
		String[] words = s.trim().toLowerCase().split(" ");
		AtomicLong value = new AtomicLong(0);
		Arrays.stream(relatedTerms)
				.map(t-> Arrays.stream(words).filter(w -> w.equals(t)).count())
				.reduce(Long::sum).ifPresent(value::set);
		return value.get();
	}

}
