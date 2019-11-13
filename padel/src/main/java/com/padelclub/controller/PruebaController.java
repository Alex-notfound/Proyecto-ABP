package com.padelclub.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PruebaController {
	@RequestMapping("/prueba")
	public String prueba(Model model) {
		return "campeonato";
	}

	@RequestMapping("/prueba2")
	public String prueba2(Model model) {
		return "login";
	}

	@RequestMapping("/prueba3")
	public String prueba3(Model model) {
		return "ofertados";
	}
	
	@RequestMapping("/prueba4")
	public String prueba4(Model model) {
		return "promocionados";
	}
}