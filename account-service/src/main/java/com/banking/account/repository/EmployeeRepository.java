package com.banking.account.repository;

import com.banking.account.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    /**
     * Native SQL query to fetch the second-highest salary from the employees table.
     * Demonstrates core SQL proficiency expected in BFSI banking interviews.
     */
    @Query(value = "SELECT DISTINCT salary FROM employees ORDER BY salary DESC LIMIT 1 OFFSET 1", nativeQuery = true)
    Optional<Double> findSecondHighestSalary();
}
