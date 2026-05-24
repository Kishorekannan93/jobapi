package com.jobai.repository;
import com.jobai.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    List<Company> findByUserIdAndStatus(Long userId, String status);
    List<Company> findByUserId(Long userId);
    long countByUserIdAndStatus(Long userId, String status);
}