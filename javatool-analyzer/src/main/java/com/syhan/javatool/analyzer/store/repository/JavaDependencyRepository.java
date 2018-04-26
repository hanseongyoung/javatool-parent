package com.syhan.javatool.analyzer.store.repository;

import com.syhan.javatool.analyzer.entity.JavaDependency;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JavaDependencyRepository extends CrudRepository<JavaDependency, String> {
    //
    List<JavaDependency> findByFromModule(String fromModule);
    List<JavaDependency> findByFromModuleAndToLevel(String fromModule, int toLevel);
    List<JavaDependency> findByFromModuleAndToModule(String fromModule, String toModule);
    long countByFromModuleAndAndToModule(String fromModule, String toModule);

    @Query("SELECT c FROM JavaDependency c WHERE c.fromModule = :fromModule AND c.toModule LIKE :toModule%")
    List<JavaDependency> findByFromModuleAndStartWithToModule(@Param("fromModule") String fromModule, @Param("toModule") String toModule);

    @Query("SELECT c FROM JavaDependency c WHERE c.fromModule = :fromModule AND c.toLevel = :toLevel AND c.toModule LIKE :toModule%")
    List<JavaDependency> findByFromModuleAndStartWithToModule(@Param("fromModule") String fromModule, @Param("toModule") String toModule, @Param("toLevel") int toLevel);
}
