package edu.lgcns.team428.chungbaji_be.member.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.lgcns.team428.chungbaji_be.member.domain.entity.MemberEntity;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, Integer> {

      Optional<MemberEntity> findByEmailAndPhoneNum(String email, String phoneNum);

      Optional<MemberEntity> findByEmail(String email) ;

      Boolean existsByEmail(String email) ;

      Boolean existsByNickname(String nickname);

}