package ru.sea.port.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sea.port.dto.ShipDto;
import ru.sea.port.mapper.ShipMapper;
import ru.sea.port.model.ship.Ship;
import ru.sea.port.service.ship.ShipService;

import java.util.List;

@RestController
@RequestMapping("/api/stevedore")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('TERMINAL_OPERATOR','TALLYMAN')")
public class ShipController {

    private final ShipService shipService;
    private final ShipMapper shipMapper;

    /**
     * Возвращает список всех судов в системе.
     */
    @GetMapping("/ships")
    public ResponseEntity<List<ShipDto>> listShips() {
        List<Ship> ships = shipService.getAllShips();
        // маппим через MapStruct
        List<ShipDto> dtos = ships.stream()
                .map(shipMapper::toDto)
                .toList();
        return ResponseEntity.ok(dtos);
    }

}
