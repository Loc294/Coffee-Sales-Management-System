package org.example.repository;

import org.example.entity.Shift;
import java.util.List;

public interface ShiftRepository {
    List<Shift> findAll();
    void save(Shift shift);
    void update(Shift shift);
    void deleteById(int id);
}
