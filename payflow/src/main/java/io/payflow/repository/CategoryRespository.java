package io.payflow.repository;

import io.payflow.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRespository extends JpaRepository<Category, Long> {

}
