package com.padelclub.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.padelclub.model.Usuario2;
import com.padelclub.service.api.ReservaService;
import com.padelclub.service.api.UsuarioCampeonatoService;
import com.padelclub.service.api.UsuarioService;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

	@Autowired
	private UsuarioService usuarioService;
	@Autowired
	private ReservaService reservaService;
	@Autowired
	private UsuarioCampeonatoService usuarioCampeonatoService;

	@RequestMapping(value = { "", "/" })
	public String index(Model model) {
		model.addAttribute("list", usuarioService.getAll());
		return "UsuariosView/UsuariosShowAll";
	}

	@GetMapping("/save/{id}")
	public String showSave(@PathVariable("id") Long id, Model model) {
		if (id != null && id != 0) {
			model.addAttribute("usuario", usuarioService.get(id));
		} else {
			model.addAttribute("usuario", new Usuario2());
		}
		return "UsuariosView/UsuariosForm";
	}

	@PostMapping("/save")
	public String save(Usuario2 usuario, Model model) {
		usuarioService.save(usuario);
		return "redirect:/usuarios/";
	}

	@GetMapping("/delete/{id}")
	public String delete(@PathVariable Long id, Model model) {
		usuarioService.delete(id);
		return "redirect:/usuarios/";
	}

	@GetMapping("/profile")
	public String myprofile(Principal principalUsuario, Model model) {
		Usuario2 usuario = usuarioService.getUsuario(principalUsuario);
		model.addAttribute("usuario", usuario);
		model.addAttribute("reservas", reservaService.findAllByUsuario(usuario));
		model.addAttribute("campeonatos", usuarioCampeonatoService.findAllCampeonatosByUsuario(usuario));
		return "UsuariosView/UsuariosProfile";
	}

	@GetMapping("/profile/{id}")
	public String profileId(@PathVariable Long id, Model model) {
		Usuario2 usuario = usuarioService.get(id);
		model.addAttribute("usuario", usuario);
		model.addAttribute("reservas", reservaService.findAllByUsuario(usuario));
		model.addAttribute("campeonatos", usuarioCampeonatoService.findAllCampeonatosByUsuario(usuario));
		return "UsuariosView/UsuariosProfile";
	}

}
