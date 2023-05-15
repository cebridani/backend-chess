package io.github.cebridani.chesswebapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.cebridani.chesswebapi.entity.User;
import io.github.cebridani.chesswebapi.payload.Pgn;

public interface PgnRepository extends JpaRepository<Pgn, Long>{
	
	Optional<Pgn> findByUser(User user);

}
