package com.padelclub.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.padelclub.model.Pista;
import com.padelclub.model.Reserva;
import com.padelclub.service.api.PistaService;
import com.padelclub.service.api.ReservaService;
import com.padelclub.service.api.UsuarioService;

@Controller
@RequestMapping("/reservas")
public class ReservasController {

	@Autowired
	private ReservaService reservaService;
	@Autowired
	private UsuarioService usuarioService;
	@Autowired
	private PistaService pistaService;

	@RequestMapping(value = { "", "/" })
	public String index(Principal usuario, Model model, Principal usuarioLogeado) {
		model.addAttribute("list", reservaService.getAllFromUser(usuarioService.getUsuario(usuario)));
		addUserToModel(usuarioLogeado, model);
		return "ReservasView/ReservasShowAll";
	}

	@GetMapping("/save/{id}")
	public String showSave(@PathVariable("id") Long id, Model model, Principal usuarioLogeado) {
		if (id != null && id != 0) {
			model.addAttribute("reserva", reservaService.get(id));
		} else {
			model.addAttribute("reserva", new Reserva());
		}
		addUserToModel(usuarioLogeado, model);
		return "ReservasView/ReservasForm";
	}

	@PostMapping("/save")
	public String save(Reserva reserva, @RequestParam("pistaId") Long idPista, Principal usuario, Model model) {
		reserva.setPista(pistaService.get(idPista));
		reserva.setDisponible(false);
		reserva.setUsuario(usuarioService.getUsuario(usuario));
		reservaService.save(reserva);
		return "redirect:/reservas/";
	}

	@PostMapping("/buscar")
	public String buscar(Reserva reserva, Model model, Principal usuarioLogeado) {
		List<Pista> pistas = pistaService.getAll();
		model.addAttribute("map", reservaService.getReservasDao(reserva, pistas));
		model.addAttribute("pistas", pistas);
		model.addAttribute("fecha", reserva.getFecha());
		model.addAttribute("reserva", reserva);

		addUserToModel(usuarioLogeado, model);
		return "ReservasView/ReservasShowByFecha";
	}

	@GetMapping("/delete/{id}")
	public String delete(@PathVariable Long id, Model model) {
		reservaService.delete(id);
		return "redirect:/reservas/";
	}

	@GetMapping("/inscribir/{idReserva}/{idUsuario}")
	public String inscribir(@PathVariable("idReserva") Long idReserva, @PathVariable("idUsuario") Long idUsuario,
			Model model) {
		Reserva reserva = reservaService.get(idReserva);
		reserva.setUsuario(usuarioService.get(idUsuario));
		reserva.setDisponible(false);
		reservaService.save(reserva);
		return "redirect:/reservas/";
	}

//	@GetMapping("/liberar/{id}")
//	public String liberar(@PathVariable Long id, Model model) {
//		Reserva reserva = reservaService.get(id);
//		reserva.setUsuario(null);
//		reserva.setDisponible(true);
//		reservaService.save(reserva);
//		return "redirect:/reservas/";
//	}

	public void addUserToModel(Principal usuario, Model model) {
		model.addAttribute("sesion", usuarioService.getUsuario(usuario));
	}
}