package it.codiceFiscale;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class Controller {

	@GetMapping("/dati-anagrafici")

	public Map<String, Object> getDatiAnagrafici(@RequestParam String codiceFiscale) {
		Map<String, Object> response = new HashMap<>();

		try {
			LocalDate dataDiNascita = calcoloDataDiNascita(codiceFiscale);
			int eta = calcolaEta(dataDiNascita);

			response.put("Data di Nascita", dataDiNascita);
			response.put("Età", eta);
		} catch (IllegalArgumentException e) {
			response.put("error", e.getMessage());
		}

		return response;
	}

	private LocalDate calcoloDataDiNascita(String codiceFiscale) {
		if (codiceFiscale.length() != 16 || !verificaCodiceFiscale(codiceFiscale)) {
			throw new IllegalArgumentException("Codice Fiscale non valido");
		}

		String giorno = codiceFiscale.substring(9, 11);
		String mese = codiceFiscale.substring(8, 9);
		String anno = codiceFiscale.substring(6, 8);

		int giornoInt = Integer.parseInt(giorno);
		if (giornoInt > 40) {
			giornoInt -= 40;
		}

		mese = analisiMese(mese);

		int annoInt = Integer.parseInt(anno);
		if (annoInt >= 0 && annoInt <= 23) {
			anno = "20" + anno;
		} else {
			anno = "19" + anno;
		}

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		return LocalDate.parse(String.format("%02d/%s/%s", giornoInt, mese, anno), formatter);
	}
	
	private boolean verificaCodiceFiscale(String codiceFiscale) {
	    String pattern = "[A-Za-z]{6}\\d{2}[A-Za-z]\\d{2}[A-Za-z]\\d{3}[A-Za-z]";

	    return codiceFiscale.matches(pattern);
	}


	private String analisiMese(String mese) {
		switch (mese.toUpperCase()) {
		case "A":
			return "01";
		case "B":
			return "02";
		case "C":
			return "03";
		case "D":
			return "04";
		case "E":
			return "05";
		case "H":
			return "06";
		case "L":
			return "07";
		case "M":
			return "08";
		case "P":
			return "09";
		case "R":
			return "10";
		case "S":
			return "11";
		case "T":
			return "12";
		default:
			throw new IllegalArgumentException("Mese non valido");
		}
	}

	private int calcolaEta(LocalDate dataDiNascita) {
		return Period.between(dataDiNascita, LocalDate.now()).getYears();
	}
}
