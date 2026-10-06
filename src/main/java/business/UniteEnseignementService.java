package business;

import entities.UniteEnseignement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UniteEnseignementService {
    private static final Map<Integer, UniteEnseignement> UNITS = new ConcurrentHashMap<>();

    public boolean create(UniteEnseignement unit) {
        return UNITS.putIfAbsent(unit.getCode(), unit) == null;
    }

    public UniteEnseignement findByCode(int code) { return UNITS.get(code); }

    public List<UniteEnseignement> findAll() { return sorted(UNITS.values()); }

    public List<UniteEnseignement> findBySemester(int semester) {
        List<UniteEnseignement> result = new ArrayList<>();
        for (UniteEnseignement unit : UNITS.values()) if (unit.getSemestre() == semester) result.add(unit);
        result.sort(Comparator.comparingInt(UniteEnseignement::getCode));
        return result;
    }

    public boolean update(int code, UniteEnseignement unit) {
        if (!UNITS.containsKey(code)) return false;
        unit.setCode(code);
        UNITS.put(code, unit);
        return true;
    }

    public boolean delete(int code) { return UNITS.remove(code) != null; }

    private List<UniteEnseignement> sorted(Collection<UniteEnseignement> units) {
        List<UniteEnseignement> result = new ArrayList<>(units);
        result.sort(Comparator.comparingInt(UniteEnseignement::getCode));
        return result;
    }
}
