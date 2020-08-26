package com.yank.ecommerce;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Extractor {
	private HashSet<String> links;
	private List<List<String>> notbooks;

	public Extractor() {
		links = new HashSet<>();
		notbooks = new ArrayList<>();
	}

	public void getPageLinks(String URL) {
		if (!links.contains(URL)) {
			try {
				Document document = Jsoup.connect(URL).get();
				Elements otherLinks = document.select("a[href^=\"/produto/\"]");

				for (Element page : otherLinks) {
					if (links.add(URL)) {
						System.out.println(URL);
					}
					getPageLinks(page.attr("abs:href"));
				}
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		}
	}

	public void getNotebooks() {
		links.forEach(x -> {
			Document document;
			try {
				document = Jsoup.connect(x).get();
				Elements notebookLinks = document.select("a[href^=\"/produto/\"]");
				for (Element notbook : notebookLinks) {

					if (notbook.text().matches("^.*?(produto).*$")) {

						ArrayList<String> temporary = new ArrayList<>();
						temporary.add(notbook.text());
						temporary.add(notbook.attr("abs:href"));
						notbooks.add(temporary);
					}
				}
				getFiltro();
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		});
	}

	public void getFiltro() {
		// Mais barato
		Document document;
		try {
			document = Jsoup.connect(
					"https://www.americanas.com.br/categoria/informatica/notebooks/notebooks?ordenacao=lowerPrice")
					.get();
			Elements notebookLinks = document.select("a[href^=\"/produto/\"]");
			for (Element notbook : notebookLinks) {

				if (notbook.text().matches("^.*?(produto).*$")) {

					ArrayList<String> temporary = new ArrayList<>();
					temporary.add(" Mais barato - " + notbook.text());
					temporary.add(notbook.attr("abs:href"));
					notbooks.add(temporary);
					break;
				}
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}

		// Mais Popular
		try {
			document = Jsoup.connect(
					"https://www.americanas.com.br/categoria/informatica/notebooks/notebooks?ordenacao=topSelling")
					.get();
			Elements notebookLinks = document.select("a[href^=\"/produto/\"]");
			for (Element notbook : notebookLinks) {

				if (notbook.text().matches("^.*?(produto).*$")) {

					ArrayList<String> temporary = new ArrayList<>();
					temporary.add(" Mais popular - " + notbook.text());
					temporary.add(notbook.attr("abs:href"));
					notbooks.add(temporary);
					break;
				}
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}

		// Mais avaliado
		try {
			document = Jsoup
					.connect("https://www.americanas.com.br/categoria/informatica/notebooks/notebooks?ordenacao=rating")
					.get();
			Elements notebookLinks = document.select("a[href^=\"/produto/\"]");
			for (Element notbook : notebookLinks) {

				if (notbook.text().matches("^.*?(produto).*$")) {

					ArrayList<String> temporary = new ArrayList<>();
					temporary.add(" Mais avaliado - " + notbook.text());
					temporary.add(notbook.attr("abs:href"));
					notbooks.add(temporary);
					break;
				}
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}

	}

	public void writeToFile(String filename) {
		FileWriter writer;
		try {
			writer = new FileWriter(filename);
			notbooks.forEach(a -> {
				try {
					String temp = notbooks.indexOf(a) + "- Title: " + a.get(0) + " (link: " + a.get(1) + ")\n";

					System.out.println(temp);

					writer.write(temp);

				} catch (IOException e) {
					System.err.println(e.getMessage());
				}
			});
			writer.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	public static void main(String[] args) {
		Extractor bwc = new Extractor();
		bwc.getPageLinks("https://www.americanas.com.br/categoria/informatica/notebooks/");
		bwc.getNotebooks();
		bwc.writeToFile("Notbooks");
	}
}