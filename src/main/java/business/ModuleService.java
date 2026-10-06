package business;

import entities.Module;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ModuleService {
    private static final Map<String, Module> MODULES = new ConcurrentHashMap<>();

    public boolean create(Module module) { return MODULES.putIfAbsent(module.getMatricule(), module) == null; }
    public Module findByMatricule(String matricule) { return MODULES.get(matricule); }
    public List<Module> findAll() { return sorted(new ArrayList<>(MODULES.values())); }

    public List<Module> findByUniteEnseignementCode(int code) {
        List<Module> result = new ArrayList<>();
        for (Module module : MODULES.values()) {
            if (module.getUniteEnseignement() != null && module.getUniteEnseignement().getCode() == code) result.add(module);
        }
        return sorted(result);
    }

    public boolean update(String matricule, Module module) {
        if (!MODULES.containsKey(matricule)) return false;
        module.setMatricule(matricule);
        MODULES.put(matricule, module);
        return true;
    }

    public boolean delete(String matricule) { return MODULES.remove(matricule) != null; }

    private List<Module> sorted(List<Module> modules) {
        modules.sort(Comparator.comparing(Module::getMatricule));
        return modules;
    }
}
