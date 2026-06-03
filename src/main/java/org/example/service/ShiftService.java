package org.example.service;

import org.example.entity.Shift;
import java.util.List;

public interface ShiftService {
    List<Shift> findAll();
    void create(Shift shift);
    void update(Shift shift);
    void deleteById(int id);
}
