package nsu.kardash.backendsportevents.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/debug/sql")
@PropertySource("classpath:/application.properties")
@RequiredArgsConstructor
class SQLController {

    private final JdbcTemplate jdbcTemplate;

    @PostMapping("/execute")
    @Operation(
            summary = "Admin access. Admin access. Выполнение SQL запросов"
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'ROOT')")
    public ResponseEntity<?> executeSQL(@RequestBody String sql,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size,
                                        @RequestParam(required = false) String sortBy,
                                        @RequestParam(defaultValue = "asc") String sortDirection) {
        try {
            sql = sql.trim();
            if (!sql.substring(0, 6).equalsIgnoreCase("select")) {
                return ResponseEntity.badRequest().body("Only SELECT statements are allowed");
            }

            if (sortBy != null && !sortBy.isBlank()) {
                sql += " order by " + sortBy + " " + (sortDirection.equalsIgnoreCase("asc") ? "asc" : "desc");
            }

            String pagedSql = sql + " limit " + size + " offset " + (page * size);

            System.out.println(pagedSql);

            List<Map<String, Object>> result = jdbcTemplate.queryForList(pagedSql);
            // Подсчитываем общее количество строк (удаляем order by, если он есть)
            String countSql = "SELECT COUNT(*) FROM (" + sql + ") AS total";
            long total = jdbcTemplate.queryForObject(countSql, Long.class);

            Pageable pageable = PageRequest.of(page, size);
            Page<Map<String, Object>> pageResult = new PageImpl<>(result, pageable, total);

            return ResponseEntity.ok(pageResult);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

}
