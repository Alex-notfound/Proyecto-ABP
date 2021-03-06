package com.padelclub.service.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import com.padelclub.commons.GenericServiceImpl;
import com.padelclub.dao.api.EnfrentamientoRepository;
import com.padelclub.model.Campeonato;
import com.padelclub.model.Enfrentamiento;
import com.padelclub.model.Reserva;
import com.padelclub.service.api.EnfrentamientoService;

@Service
public class EnfrentamientoServiceImpl extends GenericServiceImpl<Enfrentamiento, Long>
		implements EnfrentamientoService {

	@Autowired
	private EnfrentamientoRepository enfrentamientoRepository;

	@Override
	public CrudRepository<Enfrentamiento, Long> getDao() {
		return enfrentamientoRepository;
	}

	@Override
	public List<Enfrentamiento> getAllByCampeonato(Campeonato campeonato) {
		return enfrentamientoRepository.findAllByCampeonato(campeonato);
	}

	@Override
	public Enfrentamiento getByReserva(Reserva reserva) {
		return enfrentamientoRepository.findByReserva(reserva);
	}

	@Override
	public List<Enfrentamiento> getAllByCampeonatoOrdererByGroup(Campeonato campeonato) {
		return enfrentamientoRepository.findAllByCampeonatoOrderByGrupo(campeonato);
	}

	@Override
	public Map<Integer, List<Enfrentamiento>> getEnfrentamientosByFaseAgrupados(Campeonato campeonato, int fase) {
		List<Enfrentamiento> list = getAllByCampeonatoOrdererByGroup(campeonato);
		Map<Integer, List<Enfrentamiento>> map = new LinkedHashMap<>();
		List<Enfrentamiento> listToPut = new ArrayList<>();
		int grupoActual = 1;
		for (Enfrentamiento enfrentamiento : list) {
			if (grupoActual != enfrentamiento.getGrupo()) {
				map.put(grupoActual, listToPut);
				listToPut = new ArrayList<>();
				grupoActual++;
			}
			if (enfrentamiento.getFase() == fase) {
				listToPut.add(enfrentamiento);
			}
		}
		map.put(grupoActual, listToPut);
		return map;
	}

	@Override
	public boolean faseRecienFinalizada(Campeonato campeonato, int fase) {
		List<Enfrentamiento> list = getAllByCampeonato(campeonato);
		for (Enfrentamiento enfrentamiento : list) {
			if (enfrentamiento.getFase() == fase || enfrentamiento.getGanador() == null) {
				return false;
			}
		}
		return true;
	}

	@Override
	public List<Enfrentamiento> getEnfrentamientosByFase(Campeonato campeonato, int fase) {
		return enfrentamientoRepository.findByCampeonatoAndFase(campeonato, fase);
	}

	@Override
	public int getFaseActual(Campeonato campeonato) {
		return enfrentamientoRepository.findTopByCampeonatoOrderByFaseDesc(campeonato).getFase();
	}

	@Override
	public void deleteAllByCampeonato(Campeonato campeonato) {
		enfrentamientoRepository.deleteAllByCampeonato(campeonato);
	}

	@Override
	public boolean existeEnfrentamiento(Reserva reserva) {
		return enfrentamientoRepository.findByReserva(reserva) != null;
	}

}
