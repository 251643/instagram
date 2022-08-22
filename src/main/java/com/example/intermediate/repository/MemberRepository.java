package com.example.intermediate.repository;

import com.example.intermediate.model.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

  Optional<Member> findById(Long id);
  Optional<Member> findByEmail(String email);
  boolean existsByNickname(String nickname);
}
