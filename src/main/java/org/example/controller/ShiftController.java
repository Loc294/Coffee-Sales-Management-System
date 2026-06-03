package org.example.controller;

import org.example.entity.Shift;
import org.example.service.ShiftService;
import org.example.service.impl.ShiftServiceImpl;

import java.time.LocalTime;
import java.util.List;

public class ShiftController {

    private final ShiftService service = new ShiftServiceImpl();

    public List<Shift> getAll() {
        return service.findAll();
    }

    public void create(String name, LocalTime start, LocalTime end) {
        service.create(new Shift(name, start, end));
    }

    public void update(Shift shift) {
        service.update(shift);
    }

    public void delete(int id) {
        service.deleteById(id);
    }
}
