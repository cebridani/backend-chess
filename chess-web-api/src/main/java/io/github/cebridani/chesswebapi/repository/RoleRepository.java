package io.github.cebridani.chesswebapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.cebridani.chesswebapi.entity.Role;


public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
