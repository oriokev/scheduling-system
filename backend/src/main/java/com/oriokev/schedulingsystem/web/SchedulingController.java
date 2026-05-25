package com.oriokev.schedulingsystem.web;

import com.oriokev.schedulingsystem.service.SchedulingService;
import com.oriokev.schedulingsystem.web.dto.SchedulingRequest;
import com.oriokev.schedulingsystem.web.dto.SchedulingResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/schedulings")
public class SchedulingController {

    private final SchedulingService service;

    public SchedulingController(SchedulingService service) {
        this.service = service;
    }

    @GetMapping
    public List<SchedulingResponse> list() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public SchedulingResponse get(@PathVariable UUID id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SchedulingResponse create(@Valid @RequestBody SchedulingRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public SchedulingResponse update(@PathVariable UUID id,
                                     @Valid @RequestBody SchedulingRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }

    @PostMapping("/{id}/pause")
    public SchedulingResponse pause(@PathVariable UUID id) {
        return service.pause(id);
    }

    @PostMapping("/{id}/resume")
    public SchedulingResponse resume(@PathVariable UUID id) {
        return service.resume(id);
    }
}
