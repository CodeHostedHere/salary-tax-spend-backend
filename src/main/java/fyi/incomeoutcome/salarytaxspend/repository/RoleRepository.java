package fyi.incomeoutcome.salarytaxspend.repository;

import fyi.incomeoutcome.salarytaxspend.data.Role;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

@Repository
public interface RoleRepository extends CrudRepository<Role, Long> {

    List<Role> findAll();
}
