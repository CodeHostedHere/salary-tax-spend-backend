package fyi.incomeoutcome.salarytaxspend.role;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RoleController {

    private final RoleRepository roleRepository;

    RoleController(RoleRepository repository){
        this.roleRepository = repository;
    }

    @GetMapping("/role/all")
    List<Role> all(){
        return roleRepository.findAll();
    }

}
