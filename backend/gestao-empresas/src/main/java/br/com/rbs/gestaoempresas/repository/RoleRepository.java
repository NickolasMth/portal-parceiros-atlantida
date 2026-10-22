package br.com.rbs.gestaoempresas.repository;


import br.com.rbs.gestaoempresas.entity.Role;
import org.springframework.data.jpa.repository.*;


public interface RoleRepository extends JpaRepository<Role, Long> {
}
