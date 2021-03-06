package com.padelclub.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.padelclub.model.Campeonato;
import com.padelclub.model.Pareja;
import com.padelclub.model.ParejaCampeonato;
import com.padelclub.model.ParejaCampeonatoId;
import com.padelclub.model.Usuario;
import com.padelclub.service.api.CampeonatoService;
import com.padelclub.service.api.EnfrentamientoService;
import com.padelclub.service.api.ParejaCampeonatoService;
import com.padelclub.service.api.ParejaService;
import com.padelclub.service.api.UsuarioService;

@Controller
@RequestMapping("/campeonatos")
public class CampeonatoController {

	@Autowired
	private CampeonatoService campeonatoService;
	@Autowired
	private ParejaCampeonatoService parejaCampeonatoService;
	@Autowired
	private UsuarioService usuarioService;
	@Autowired
	private ParejaService parejaService;
	@Autowired
	private EnfrentamientoService enfrentamientoService;

	@RequestMapping(value = { "", "/" })
	public String index(Model model, Principal usuarioLogeado) {
		campeonatoService.CerrarTorneosFueraDePlazo();
		model.addAttribute("list", campeonatoService.getAll());
		addUserToModel(usuarioLogeado, model);
		return "CampeonatosView/CampeonatosShowAll";
	}

	@GetMapping("/save/{id}")
	public String showSave(@PathVariable("id") Long id, Model model, Principal usuarioLogeado) {
		if (id != null && id != 0) {
			model.addAttribute("campeonato", campeonatoService.get(id));
		} else {
			model.addAttribute("campeonato", new Campeonato());
		}
		addUserToModel(usuarioLogeado, model);
		return "CampeonatosView/CampeonatosForm";
	}

	@PostMapping("/save")
	public String save(Campeonato campeonato, Model model) {
		campeonato.setAbierto(true);
		campeonatoService.save(campeonato);
		return "redirect:/campeonatos/";
	}

	@GetMapping("/delete/{id}")
	public String delete(@PathVariable Long id, Model model) {
		campeonatoService.delete(id);
		return "redirect:/campeonatos/";
	}

	@GetMapping("/formInscribir/{idCampeonato}")
	public String formInscribir(@PathVariable("idCampeonato") Long idCampeonato, Principal usuarioLogeado,
			Model model) {

		Campeonato campeonato = campeonatoService.get(idCampeonato);
		if (campeonato.getMaxNumParticipantes() == parejaCampeonatoService
				.getNumParticipantesByCampeonato(campeonato)) {
			model.addAttribute("error", "Ya se han ocupado todas las plazas");
			return index(model, usuarioLogeado);
		}

		Pareja pareja = new Pareja();
		model.addAttribute("campeonato", campeonato);
		model.addAttribute("pareja", pareja);

		addUserToModel(usuarioLogeado, model);
		return "CampeonatosView/CampeonatosInscripcionForm";
	}

	@RequestMapping("/inscribir/{idCampeonato}")
	public String inscribir(@PathVariable("idCampeonato") Long idCampeonato, @RequestParam("dni") String dni,
			Pareja pareja, Principal usuarioLogeado, Model model) {

		Usuario miembro1 = usuarioService.getUsuario(usuarioLogeado);
		Usuario miembro2 = usuarioService.getUsuarioByDni(dni);
		Campeonato campeonato = campeonatoService.get(idCampeonato);

		if (!miembro1.equals(miembro2) && parejaCampeonatoService.validarInscripcion(miembro1, miembro2, campeonato)) {

			pareja.setMiembro1(miembro1);
			pareja.setMiembro2(miembro2);

			ParejaCampeonato parejaCampeonato = new ParejaCampeonato();
			parejaCampeonato.setId(new ParejaCampeonatoId(campeonato, parejaService.save(pareja)));
			parejaCampeonatoService.save(parejaCampeonato);

			if (campeonato.getMaxNumParticipantes() == parejaCampeonatoService
					.getNumParticipantesByCampeonato(campeonato)) {
				campeonatoService.sorteo(parejaCampeonatoService.getParejasByCampeonato(campeonato), campeonato);
				campeonato.setAbierto(false);
				campeonatoService.save(campeonato);
			}
			return "redirect:/campeonatos/";
		} else {
			model.addAttribute("error", "No cumples los requisitos");
			return index(model, usuarioLogeado);
		}
	}

	@GetMapping("/consultar/{id}")
	public String consultar(@PathVariable Long id, Model model, Principal usuarioLogeado) {
		Campeonato campeonato = campeonatoService.get(id);
		if (campeonato.isAbierto()) {
			model.addAttribute("alistados", parejaCampeonatoService.getClasificacion(campeonato));
		} else {
			if (enfrentamientoService.getFaseActual(campeonato) == 5) {
				model.addAttribute("final", true);
			} else {
				model.addAttribute("final", false);
			}
		}
		model.addAttribute("campeonato", campeonato);
		model.addAttribute("map", enfrentamientoService.getEnfrentamientosByFaseAgrupados(campeonato, 1));
		model.addAttribute("idUsuarioLogueado", usuarioService.getUsuario(usuarioLogeado).getId());

		addUserToModel(usuarioLogeado, model);
		return "CampeonatosView/CampeonatoDetail";
	}

	@GetMapping("/consultar/{id}/clasificacion")
	public String consultarClasificacion(@PathVariable Long id, Model model, Principal usuarioLogeado) {
		Campeonato campeonato = campeonatoService.get(id);
		if (enfrentamientoService.getFaseActual(campeonato) == 5) {
			model.addAttribute("final", true);
		} else {
			model.addAttribute("final", false);
		}
		model.addAttribute("campeonato", campeonato);
		model.addAttribute("mapClasificacion", parejaCampeonatoService.getClasificacionAgrupada(campeonato));
		model.addAttribute("idUsuarioLogueado", usuarioService.getUsuario(usuarioLogeado).getId());

		addUserToModel(usuarioLogeado, model);
		return "CampeonatosView/CampeonatoClasificacion";
	}

	@GetMapping("/generate-playoff/{id}")
	public String generatePlayoff(@PathVariable Long id, Model model, Principal usuarioLogeado) {
		Campeonato campeonato = campeonatoService.get(id);
		if (enfrentamientoService.faseRecienFinalizada(campeonato, 2)) {
			campeonatoService.playoff(campeonato);
		} else if (enfrentamientoService.faseRecienFinalizada(campeonato, 3)) {
			campeonatoService.playoff2(campeonato);
		} else if (enfrentamientoService.faseRecienFinalizada(campeonato, 4)) {
			campeonatoService.playoff3(campeonato);
		} else if (enfrentamientoService.faseRecienFinalizada(campeonato, 5)
				&& campeonatoService.finalCampeonato(campeonato)) {
		} else {
			model.addAttribute("error", "No se pueden generar enfrentamientos de playoff");
		}
		return playoff(id, model, usuarioLogeado);
	}

	@GetMapping("/playoff/{id}")
	public String playoff(@PathVariable Long id, Model model, Principal usuarioLogeado) {
		Campeonato campeonato = campeonatoService.get(id);
		model.addAttribute("campeonato", campeonato);
		if (enfrentamientoService.getFaseActual(campeonato) == 5) {
			model.addAttribute("final", true);
		} else {
			model.addAttribute("final", false);
		}
		int faseActual = enfrentamientoService.getFaseActual(campeonato);
		if (faseActual > 1 && faseActual < 5) {
			model.addAttribute("map", enfrentamientoService.getEnfrentamientosByFaseAgrupados(campeonato, faseActual));
		} else if (faseActual == 5) {
			model.addAttribute("map", enfrentamientoService.getEnfrentamientosByFaseAgrupados(campeonato, 4));
		}

		model.addAttribute("idUsuarioLogueado", usuarioService.getUsuario(usuarioLogeado).getId());

		addUserToModel(usuarioLogeado, model);
		return "CampeonatosView/CampeonatoPlayoff";
	}

	@GetMapping("/final/{id}")
	public String fianl(@PathVariable Long id, Model model, Principal usuarioLogeado) {
		Campeonato campeonato = campeonatoService.get(id);
		model.addAttribute("campeonato", campeonato);
		int faseActual = enfrentamientoService.getFaseActual(campeonato);
		if (faseActual == 5) {
			model.addAttribute("list", enfrentamientoService.getEnfrentamientosByFase(campeonato, faseActual));
		}
		model.addAttribute("idUsuarioLogueado", usuarioService.getUsuario(usuarioLogeado).getId());

		addUserToModel(usuarioLogeado, model);
		return "CampeonatosView/CampeonatoFinal";
	}

	public void addUserToModel(Principal usuario, Model model) {
		model.addAttribute("sesion", usuarioService.getUsuario(usuario));
	}

}