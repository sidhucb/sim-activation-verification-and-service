package com.cardactivation;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
@Qualifier("CardActivationRepository")
public interface CardActivationRepository extends JpaRepository<CardActivationEntity, Long> {
    Optional<CardActivationEntity> findByUserName(String userName);
}